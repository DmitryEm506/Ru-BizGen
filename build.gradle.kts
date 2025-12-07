
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun environment(key: String) = providers.environmentVariable(key)

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.dokka)
}

group = "ru.eda.plgn.bizgen"
version = "1.8.242"

apply(from = "gradle/ic-version.gradle.kts")

val buildNumber: String by extra
val icVersion: String by extra

kotlin {
  jvmToolchain(21)
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
    password.set("test")
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
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
  }

  patchPluginXml {
    val changes = changelog.getAll().values.joinToString("<hr>\n") {
      changelog.renderItem(it, Changelog.OutputType.HTML)
    }
    changeNotes.set(provider { changes })
  }

  afterEvaluate {
    tasks.named("buildPlugin") {
      doLast {
        val jarDir = layout.buildDirectory.dir("libs").get().asFile
        val releasesDir = layout.projectDirectory.dir("releases").asFile

        releasesDir.mkdirs()

        val pluginName = "${rootProject.name}-$version.jar"

        jarDir.listFiles { file -> file.name == pluginName }
          ?.forEach { jarFile ->
            println("Copying plugin ${jarFile.name} to releases folder")
            jarFile.copyTo(File(releasesDir, jarFile.name), overwrite = true)
          }
      }
    }
  }
}