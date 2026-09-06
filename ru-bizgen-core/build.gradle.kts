plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
  id("ru-bizgen.dokka-convention")
  id("ru-bizgen.kover-convention")
}

dependencies {
  implementation(kotlin("stdlib"))

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)
  testRuntimeOnly(libs.junit.jupiter.engine)
}
