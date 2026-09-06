plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
}

dependencies {
  testImplementation(project(":ru-bizgen-core"))
  testImplementation(project(":ru-bizgen-mcp"))
  testImplementation(project(":ru-bizgen-perf"))
  testImplementation(project(path = ":ru-bizgen-perf", configuration = "jmhApiByConf"))

  testImplementation(libs.archunit.junit5)
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)

  testRuntimeOnly(libs.junit.jupiter.engine)
}
