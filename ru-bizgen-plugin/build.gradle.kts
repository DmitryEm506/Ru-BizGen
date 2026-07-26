import org.jetbrains.changelog.Changelog
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
  implementation(project(":ru-bizgen-core"))

  testImplementation(libs.bundles.tests.integration) {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-test-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
  }

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
      select {
        channels = listOf(Channel.RELEASE, Channel.RC, Channel.PATCH)
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
  reports {
    total {
      filters {
        excludes {
          packages(
            "ru.eda.plgn.plugin.bizgen.ui"
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
