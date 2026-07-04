import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  alias(libs.plugins.kotlin)
  application
}

group = rootProject.group
version = rootProject.version

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

application {
  mainClass = "ru.eda.plgn.bizgen.mcp.McpServerAppKt"
  applicationDefaultJvmArgs = listOf("-Dmcp.version=${project.version}")
}

dependencies {
  implementation(project(":ru-bizgen-core"))
  implementation(libs.mcp.kotlin.sdk.server)
  implementation(libs.ktor.server.netty)

  runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

  testImplementation(kotlin("test"))
  testImplementation(project(":ru-bizgen-core"))
  testImplementation(libs.bundles.tests.unit)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
  test {
    useJUnitPlatform()

    testLogging {
      events = setOf(TestLogEvent.FAILED)
      exceptionFormat = TestExceptionFormat.FULL
    }
  }
}
