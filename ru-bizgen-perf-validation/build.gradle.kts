plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.testing-convention")
}

dependencies {
  implementation(libs.reflections)

  // Подключаем именно классы бенчмарков через созданную конфигурацию
  testImplementation(project(path = ":ru-bizgen-perf", configuration = "jmhApiByConf"))

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)

  testRuntimeOnly(libs.junit.jupiter.engine)
}
