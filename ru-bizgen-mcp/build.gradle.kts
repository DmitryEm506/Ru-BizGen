plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
  application
}

group = rootProject.group
version = rootProject.version

application {
  mainClass = "ru.eda.plgn.bizgen.mcp.McpServerAppKt"
  applicationDefaultJvmArgs = listOf("-Dmcp.version=${project.version}")
}

dependencies {
  implementation(project(":ru-bizgen-core"))
  implementation(libs.mcp.kotlin.sdk.server)
  implementation(libs.ktor.server.netty)

  runtimeOnly(libs.slf4j.simple)

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)
  testRuntimeOnly(libs.junit.jupiter.engine)
}
