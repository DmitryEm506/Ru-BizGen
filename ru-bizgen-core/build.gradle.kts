
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlin)
}

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
  implementation(kotlin("stdlib"))

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

kover {
  reports {
    total {
      xml { onCheck = true }
      html { onCheck = true }
    }
  }
}

tasks {
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