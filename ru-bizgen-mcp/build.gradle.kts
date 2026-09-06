plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
  application
}

group = rootProject.group
version = rootProject.version

application {
  mainClass = "ru.eda.plgn.bizgen.mcp.McpServerAppKt"
  // Логи сервера на русском: без явной кодировки они нечитаемы в консоли Windows
  // (в Docker на Linux локаль уже UTF-8).
  applicationDefaultJvmArgs = listOf(
    "-Dmcp.version=${project.version}",
    "-Dfile.encoding=UTF-8",
    "-Dsun.stdout.encoding=UTF-8",
    "-Dsun.stderr.encoding=UTF-8",
  )
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
