import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun environment(key: String) = providers.environmentVariable(key)

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.21"
  id("org.jetbrains.intellij.platform") version "2.5.0"
  id("org.jetbrains.changelog") version "2.2.0"
}

group = "ru.eda.plgn.bizgen"
version = "1.7.252"

apply(from = "gradle/ic-version.gradle.kts")

// Получаем значения из extra properties
val buildNumber: String by extra
val icVersion: String by extra

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    create("IC", icVersion)
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    pluginVerifier()
    zipSigner()
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
}

changelog {
  version.set(project.version.toString())
  headerParserRegex = """(\d+\.\d+)""".toRegex()
}

tasks {
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }

  patchPluginXml {
    val changes = changelog.getAll().values.joinToString("<hr>\n") { changelogItem ->
      changelog.renderItem(changelogItem, Changelog.OutputType.HTML)
    }

    changeNotes.set(provider { changes })
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
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
