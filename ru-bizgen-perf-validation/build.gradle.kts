plugins {
  alias(libs.plugins.kotlin)
}

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
  implementation(libs.reflections)

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)
}

dependencies {
  implementation(libs.reflections)

  // Подключаем именно классы бенчмарков через созданную конфигурацию
  testImplementation(project(path = ":ru-bizgen-perf", configuration = "jmhApiByConf"))

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.tests.unit)
}

tasks.test {
  useJUnitPlatform()
}