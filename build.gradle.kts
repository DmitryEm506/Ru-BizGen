import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.changelog.Changelog
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Year

fun environment(key: String) = providers.environmentVariable(key)

plugins {
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.gradleIntelliJPlugin)
}

group = "ru.eda.plgn.bizgen"
version = "1.9.252"

apply(from = "gradle/ic-version.gradle.kts")

val buildNumber: String by extra
val icVersion: String by extra

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  testImplementation(libs.bundles.test)
  testRuntimeOnly(libs.junit.jupiter.engine)


  intellijPlatform {
    create("IC", icVersion)

    pluginVerifier()
    zipSigner()
    testFramework(TestFrameworkType.JUnit5)
  }

  dokkaHtmlPlugin(libs.dokkaVersioningPlugin)
}

// Критично. Принудительный переход на JUnit 6. В рамках платформы intellij осталась основа ещё на 4 версии, но Jetbrains рекомендует
// переходить как минимум на версию 5, решил сразу "прыгнуть" на версию 6, так как сейчас только unit тесты
testing {
  suites {
    @Suppress("unused") val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = buildNumber
    }

    description = file("src/main/resources/META-INF/description.html").readText()
  }

  signing {
    certificateChain.set(environment("CERTIFICATE_CHAIN"))
    privateKey.set(environment("PRIVATE_KEY"))
    password.set("PRIVATE_KEY_PASSWORD")
  }

  pluginVerification {
    ides { recommended() }
  }
}

changelog {
  version.set(project.version.toString())
  headerParserRegex = """(\d+\.\d+)""".toRegex()
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
    useJUnitPlatform {
      val distanceFinderEnabled = project.hasProperty("runDistanceFinderTests") || System.getProperty("runDistanceFinderTests") == "true"

      if (distanceFinderEnabled) {
        includeTags("distanceFinderTests")
      } else {
        excludeTags("distanceFinderTests")
      }
    }

    testLogging {
      events = setOf(TestLogEvent.FAILED)
      exceptionFormat = TestExceptionFormat.FULL
    }
  }

  // Documentation
  dokka {
    moduleName.set(rootProject.name)
    moduleVersion.set(version.toString())

    dokkaSourceSets.main {
      jdkVersion.set(libs.versions.java.get().toInt())
      languageVersion.set(libs.versions.kotlin.get())

      documentedVisibilities.set(
        setOf(
          VisibilityModifier.Public,
          VisibilityModifier.Private,
          VisibilityModifier.Protected,
          VisibilityModifier.Internal,
          VisibilityModifier.Package,
        )
      )
      reportUndocumented.set(true)

      sourceLink {
        localDirectory.set(file("src/main/kotlin"))
        remoteUrl("https://github.com/DmitryEm506/Plugin_EDA_Bizgen/blob/main/src/main/kotlin")
        remoteLineSuffix.set("#L")
      }
    }
    dokkaPublications.html {
      suppressInheritedMembers.set(true)
      offlineMode.set(true)
    }
    pluginsConfiguration.html {
      // TODO: Пока костыль, необходимо ждать исправления https://github.com/Kotlin/dokka/issues/4369
      customAssets.from(file(".config/dokka/logo-icon.svg"))
      footerMessage.set("&copy; ${Year.now().value} Dmitry&nbsp;A.&nbsp;Emelyanenko")
    }
    pluginsConfiguration.versioning {
      if (project.hasProperty("dokka.pagesDir")) {
        val pagesDir = project.property("dokka.pagesDir")
        olderVersions.setFrom(file("$pagesDir"))
        olderVersionsDir.set(file("$pagesDir/older/"))
      }
    }
  }

//  afterEvaluate {
//    tasks.named("buildPlugin") {
//      doLast {
//        val jarDir = layout.buildDirectory.dir("libs").get().asFile
//        val releasesDir = layout.projectDirectory.dir("releases").asFile
//
//        releasesDir.mkdirs()
//
//        val pluginName = "${rootProject.name}-$version.jar"
//
//        jarDir.listFiles { file -> file.name == pluginName }
//          ?.forEach { jarFile ->
//            println("Copying plugin ${jarFile.name} to releases folder")
//            jarFile.copyTo(File(releasesDir, jarFile.name), overwrite = true)
//          }
//      }
//    }
//  }
}