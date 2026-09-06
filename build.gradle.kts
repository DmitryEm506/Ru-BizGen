group = "ru.eda.plgn.bizgen"
version = "1.12.261"

plugins {
  alias(libs.plugins.jmh) apply false
  id("ru-bizgen.dokka-root-convention")
  alias(libs.plugins.kover)
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.changelog) apply false
  alias(libs.plugins.kotlin.allopen) apply false
  alias(libs.plugins.gradleIntelliJPlugin) apply false
  alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
  repositories {
    mavenCentral()
  }
}

dependencies {
  listOf(
    project(":ru-bizgen-core"),
    project(":ru-bizgen-plugin")
  ).forEach { dep ->
    kover(dep)
    dokka(dep)
  }

  dokka(project(":ru-bizgen-perf"))
}

// Корневой отчёт — тот, что публикуется в CI и в Dokka (build/reports/kover).
// Фильтры отчётов задаются на уровне того проекта, чей отчёт формируется,
// поэтому исключения из ru-bizgen-plugin сюда не наследуются и дублируются здесь.
kover {
  reports {
    total {
      filters {
        excludes {
          packages(
            "ru.eda.plgn.bizgen.plugin.ui"
          )
        }
      }
    }
  }
}
