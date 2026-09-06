import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.intellij.platform.gradle.models.ProductRelease.Channel

fun environment(key: String): Provider<String> = providers.environmentVariable(key)

plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
  id("ru-bizgen.dokka-convention")
  id("ru-bizgen.kover-convention")
  alias(libs.plugins.changelog)
  alias(libs.plugins.gradleIntelliJPlugin)
}

group = rootProject.group
version = rootProject.version

val buildNumber = version.toString().substringAfterLast(".").take(3)
val ideaVersion = "20" + buildNumber.take(2) + "." + buildNumber.last().toString()

repositories {
  intellijPlatform {
    defaultRepositories()
  }
}

sourceSets {
  create("integrationTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
  }
}

val integrationTestImplementation = configurations.getByName("integrationTestImplementation") {
  extendsFrom(configurations.testImplementation.get())
}

val integrationTestRuntimeOnly = configurations.getByName("integrationTestRuntimeOnly") {
  extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
  // Kotlin stdlib приходит из IntelliJ Platform (см. kotlin.stdlib.default.dependency=false
  // в gradle.properties), поэтому транзитивную копию из ru-bizgen-core в плагин не тащим:
  // вторая копия stdlib в lib/ плагина — источник конфликтов загрузчиков на будущих версиях IDE.
  // В ru-bizgen-core зависимость остаётся: там она нужна, и ей пользуются mcp/perf/archunit.
  implementation(project(":ru-bizgen-core")) {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    exclude(group = "org.jetbrains", module = "annotations")
  }

  testImplementation(libs.bundles.tests.integration) {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
  }
  testImplementation(libs.archunit.junit5)

  testRuntimeOnly(libs.junit.jupiter.engine)

  intellijPlatform {
    when (buildNumber.toInt() >= 252) {
      true -> intellijIdea(ideaVersion) { useInstaller = false }
      else -> intellijIdeaCommunity(buildNumber)
    }

    pluginVerifier()
    zipSigner()
    testFramework(TestFrameworkType.JUnit5)
    testFramework(TestFrameworkType.Platform)
    testFramework(TestFrameworkType.Starter, configurationName = "integrationTestImplementation")
  }

  integrationTestImplementation(libs.kodein.di.jvm)
  integrationTestImplementation(libs.kotlinx.coroutines.core.jvm)
  integrationTestImplementation(libs.junit.jupiter.api)
  integrationTestRuntimeOnly(libs.junit.platform.launcher)

  dokkaHtmlPlugin(libs.dokkaVersioningPlugin)
}

testing {
  suites {
    @Suppress("unused") val test = getByName<JvmTestSuite>("test") {
      useJUnitJupiter()
    }
  }
}

// Симметрично runDistanceFinderTests (testing-convention.gradle.kts:8-9):
// проверяем и gradle property (-PrunIntegrationTests), и system property
// (-DrunIntegrationTests=true, выставляется в IDE VM options или на gradle daemon),
// чтобы задачу можно было включить обоими способами из IDE.
val runIntegrationTests = project.hasProperty("runIntegrationTests") ||
  System.getProperty("runIntegrationTests") == "true"

// Полная матрица verifyPlugin (-PverifyAllIdes) качает по IDE на каждую версию из всех каналов —
// это гигабайты трафика. По умолчанию проверяются только границы диапазона совместимости.
val verifyAllIdes = project.hasProperty("verifyAllIdes")

// Минимальная поддерживаемая версия IDE — должна соответствовать sinceBuild ниже.
val MIN_SUPPORTED_IDE = "2024.2"

// Последний вышедший релиз IDEA. untilBuild = null — это заявка на совместимость со всеми
// будущими версиями платформы, поэтому каждый новый релиз проверяется верификатором отдельно,
// не дожидаясь перехода на него. Обновляется при выходе новой версии IDEA.
val LATEST_IDE = "2026.2"

val integrationTest = intellijPlatformTesting.testIdeUi.register("integrationTest") {
  task {
    val integrationTestSourceSet = sourceSets.getByName("integrationTest")
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    useJUnitPlatform()

    dependsOn("buildPlugin")
    systemProperty(
      "path.to.build.plugin",
      tasks.buildPlugin.get().archiveFile.get().asFile.absolutePath,
    )

    // Версия IDE для тестирования = версия, под которую собирается плагин (ideaVersion,
    // выводится из version проекта). В BaseUIIntegrationTest.newContextWithPlugin читается
    // из system property `bizgen.test.ide.version` с fallback на поле ideVersion
    // (для ручного запуска из IDE без gradle).
    // Переопределяема через `-Pbizgen.test.ide.version=<версия>` (эксперименты на других сборках).
    systemProperty(
      "bizgen.test.ide.version",
      (project.findProperty("bizgen.test.ide.version") as String?) ?: ideaVersion,
    )

    // Пробрасываем флаг включения в test JVM, чтобы @EnabledIfSystemProperty
    // на BaseIntegrationTest пропустил тесты (gradle property сама туда не попадает).
    if (runIntegrationTests) {
      systemProperty("runIntegrationTests", "true")
    }

    // Интеграционные тесты (старт IDE + Driver) тяжёлые и запускаются ночью
    // через ci-integration.yml. По умолчанию отключены, чтобы не попадать в `check`.
    enabled = runIntegrationTests
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "242"
      untilBuild = provider { null }
    }

    description = file("src/main/resources/META-INF/description.html").readText()
  }

  signing {
    certificateChain.set(environment("CERTIFICATE_CHAIN"))
    privateKey.set(environment("PRIVATE_KEY"))
    password.set(environment("PRIVATE_KEY_PASSWORD"))
  }

  pluginVerification {
    ides {
      when (verifyAllIdes) {
        // Полная матрица: все каналы во всём диапазоне совместимости. Тяжёлая (несколько IDE
        // по ~1 ГБ каждая), поэтому гоняется вручную через ci-all.yml.
        true -> select {
          channels = listOf(Channel.RELEASE, Channel.RC, Channel.PATCH)
        }

        // Края заявленного диапазона совместимости — именно там вылезают несовместимости API:
        // минимальная поддерживаемая версия (sinceBuild = 242), целевая сборка и последний
        // вышедший релиз платформы.
        false -> {
          // Нижняя граница: до 2025.3 (253) IDEA публиковалась раздельно, Community-дистрибутива
          // достаточно — плагин зависит только от com.intellij.modules.platform.
          create(IntelliJPlatformType.IntellijIdeaCommunity, MIN_SUPPORTED_IDE)

          // Целевая сборка. Тип и способ доставки те же, что у платформы в dependencies выше:
          // с 253 отдельного IC больше нет, остаётся единый IntellijIdea, а useInstaller = false
          // берёт дистрибутив из intellij-repository вместо инсталлятора.
          when (buildNumber.toInt() >= 252) {
            true -> create(IntelliJPlatformType.IntellijIdea, ideaVersion) { useInstaller = false }
            else -> create(IntelliJPlatformType.IntellijIdeaCommunity, ideaVersion)
          }

          // Верхняя граница: последний релиз платформы, если он ушёл вперёд целевой сборки.
          if (LATEST_IDE != ideaVersion) {
            create(IntelliJPlatformType.IntellijIdea, LATEST_IDE) { useInstaller = false }
          }
        }
      }
    }
  }

  publishing {
    token.set(environment("PUBLISH_TOKEN"))
  }
}

changelog {
  version.set(project.version.toString())
  headerParserRegex = """(\d+\.\d+)""".toRegex()
}

kover {
  // Классы source set'а integrationTest компилируются вместе с плагином и иначе попадают
  // в отчёт как непокрытый продакшен-код.
  currentProject {
    sources {
      excludedSourceSets.add("integrationTest")
    }
  }

  reports {
    total {
      filters {
        excludes {
          packages(
            "ru.eda.plgn.bizgen.plugin.ui"
          )
        }
      }
    }
  }
}

tasks {
  patchPluginXml {
    val changes = changelog.getAll().values.joinToString("<hr>\n") {
      changelog.renderItem(it, Changelog.OutputType.HTML)
    }
    changeNotes.set(provider { changes })
  }
}
