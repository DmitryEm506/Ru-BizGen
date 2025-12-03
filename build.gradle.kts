import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.21"
  id("org.jetbrains.intellij.platform") version "2.5.0"
  id("org.jetbrains.changelog") version "2.2.0"
}

group = "ru.eda.plgn.bizgen"
version = "1.7.242-SNAPSHOT"

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    create("IC", "2024.2")
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "242"
    }

    description = file("src/main/resources/META-INF/description.html").readText()
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
