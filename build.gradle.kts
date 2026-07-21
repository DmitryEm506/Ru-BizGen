
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import java.time.Year

group = "ru.eda.plgn.bizgen"
version = "1.12.261"

plugins {
  alias(libs.plugins.jmh) apply false
  alias(libs.plugins.dokka)
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

/**
 * Принудительное копирование отчета покрытия тестов в dokka.
 *
 * Причем приходится копировать именно в папку "dokka/html/images", так как она используется как рутовая папка для относительных ссылок,
 * вставляемых в Докка отчёт.
 */
val copyKoverToDokka = tasks.register<Copy>("copyKoverToDokka") {
  group = "documentation"
  description = "Копирует HTML-отчёт покрытия тестов Kover в каталог Dokka для относительных ссылок."
  dependsOn("koverHtmlReport")
  from(layout.buildDirectory.dir("reports/kover/html"))
  into(layout.buildDirectory.dir("dokka/html/images/kover"))
}

tasks {
  dokka {
    pluginsConfiguration.html {
      // TODO: Нет возможности стандартным образом прокинуть логотип и указать путь до него. Поэтому приходится называть именно так файл https://github.com/Kotlin/dokka/issues/4369
      customAssets.from(
        file(".config/dokka/logo-icon.svg")
      )

      footerMessage.set(
        """
            &copy; ${Year.now().value} Dmitry&nbsp;A.&nbsp;Emelyanenko | 
            <a href="images/kover/index.html">Code Coverage</a>
        """.trimIndent()
      )
    }
  }

  withType<DokkaGenerateTask>().configureEach {
    finalizedBy(copyKoverToDokka)
  }
}