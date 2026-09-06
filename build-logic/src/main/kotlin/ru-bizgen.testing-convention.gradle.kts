import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

tasks {
  named<Test>("test") {
    useJUnitPlatform {
      val distanceFinderEnabled = project.hasProperty("runDistanceFinderTests") ||
        System.getProperty("runDistanceFinderTests") == "true"

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
}
