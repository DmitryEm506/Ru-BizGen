import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.changelog.Changelog
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.intellij.platform.gradle.models.ProductRelease.Channel
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun environment(key: String): Provider<String> = providers.environmentVariable(key)

plugins {
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.gradleIntelliJPlugin)
}

group = rootProject.group
version = rootProject.version

val buildNumber = version.toString().substringAfterLast(".").take(3)
val ideaVersion = "20" + buildNumber.take(2) + "." + buildNumber.last().toString()

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

repositories {
  intellijPlatform {
    defaultRepositories()
  }
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
    // Unified Platform Distribution https://blog.jetbrains.com/platform/2025/11/intellij-platform-2025-3-what-plugin-developers-should-know
    when (buildNumber.toInt() >= 252) {
      true -> intellijIdea(ideaVersion) { useInstaller = false }
      else -> intellijIdeaCommunity(buildNumber)
    }

    pluginVerifier()
    zipSigner()
    testFramework(TestFrameworkType.JUnit5)
    testFramework(TestFrameworkType.Platform)
  }

  dokkaHtmlPlugin(libs.dokkaVersioningPlugin)
}

// Критично. Принудительный переход на JUnit 6. В рамках платформы intellij осталась основа ещё на 4 версии, но Jetbrains рекомендует
// переходить как минимум на версию 5, решил сразу "прыгнуть" на версию 6, так как сейчас только unit тесты
testing {
  suites {
    @Suppress("unused") val test = getByName<JvmTestSuite>("test") {
      useJUnitJupiter()
    }
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
      create(ProductReleasesValueSource {
        // явное указание, так как в recommended() включены версии Channel.EAP, а это ломает локальную проверку, так как они могут быть не доступны
        channels.convention(listOf(Channel.RELEASE, Channel.RC, Channel.PATCH))
      })
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
      xml { onCheck = true }
      html { onCheck = true }
    }
  }
}

tasks {
  withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.valueOf("JVM_${libs.versions.java.get()}"))
  }

  patchPluginXml {
    val changes = changelog.getAll().values.joinToString("<hr>\n") {
      changelog.renderItem(it, Changelog.OutputType.HTML)
    }
    changeNotes.set(provider { changes })
  }

  test {
    useJUnitPlatform {}

    testLogging {
      events = setOf(TestLogEvent.FAILED)
      exceptionFormat = TestExceptionFormat.FULL
    }
  }

  // Documentation
  dokka {
    moduleName.set(project.name)
    moduleVersion.set(version.toString())

    dokkaSourceSets.main {
      jdkVersion.set(libs.versions.java.get().toInt())
      languageVersion.set(libs.versions.kotlin.get())
      reportUndocumented.set(true)

      documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

      sourceLink {
        localDirectory.set(file("src/main/kotlin"))
        remoteUrl("https://github.com/DmitryEm506/Ru-BizGen/blob/dev/${project.name}/src/main/kotlin")
        remoteLineSuffix.set("#L")
      }
    }

    dokkaPublications.html {
      suppressInheritedMembers.set(true)
      offlineMode.set(true)
    }
  }
}