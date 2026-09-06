import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import java.time.Year

/**
 * Корневая Dokka HTML: агрегация, footer, Kover/презентации → images/, манифест Guides.
 *
 * Контракт презентаций: docs/presentation-template/PUBLISH_DOKKA.md
 */
plugins {
  id("org.jetbrains.dokka")
}

/**
 * Принудительное копирование отчёта покрытия тестов в Dokka.
 * Путь `dokka/html/images`, т.к. он используется как корень относительных ссылок.
 */
val copyKoverToDokka = tasks.register<Copy>("copyKoverToDokka") {
  group = "documentation"
  description = "Копирует HTML-отчёт покрытия тестов Kover в каталог Dokka для относительных ссылок."
  dependsOn("koverHtmlReport")
  from(layout.buildDirectory.dir("reports/kover/html"))
  into(layout.buildDirectory.dir("dokka/html/images/kover"))
}

/**
 * Runtime презентаций: только styles.css + app.js (без markdown-контрактов шаблона).
 */
val copyPresentationRuntimeToDokka = tasks.register<Copy>("copyPresentationRuntimeToDokka") {
  group = "documentation"
  description = "Копирует runtime-assets шаблона презентаций (CSS/JS) в каталог Dokka."
  from(file("docs/presentation-template")) {
    include("styles.css", "app.js")
  }
  into(layout.buildDirectory.dir("dokka/html/images/presentation-template"))
}

/**
 * Все презентации: index.html + медиа. outline.md и прочий markdown не публикуются.
 */
val copyPresentationsToDokka = tasks.register<Copy>("copyPresentationsToDokka") {
  group = "documentation"
  description = "Копирует HTML-презентации из docs/presentations в каталог Dokka."
  from(file("docs/presentations")) {
    include("*/index.html")
    include("**/*.png")
    include("**/*.jpg")
    include("**/*.jpeg")
    include("**/*.webp")
    include("**/*.svg")
    include("**/*.gif")
    exclude("**/*.md")
  }
  into(layout.buildDirectory.dir("dokka/html/images/presentations"))
}

val presentationsDocsDir = layout.projectDirectory.dir("docs/presentations")
val presentationsGuidesFile = layout.buildDirectory.file("dokka/html/images/presentations/guides.json")

/**
 * Манифест презентаций для вкладки Guides в шапке Dokka.
 * Новые docs/presentations/<slug>/index.html попадают в меню без правок header.ftl.
 */
val generatePresentationsGuides = tasks.register("generatePresentationsGuides") {
  group = "documentation"
  description = "Генерирует images/presentations/guides.json для автозаполнения Guides."

  inputs.dir(presentationsDocsDir).optional().ignoreEmptyDirectories()
  outputs.file(presentationsGuidesFile)

  val guidesOutput = presentationsGuidesFile
  val presentationsInput = presentationsDocsDir
  val presentationsOutDir = layout.buildDirectory.dir("dokka/html/images/presentations")

  doLast {
    val titleRegex = Regex("""<title>(.*?)</title>""", RegexOption.IGNORE_CASE)
    val descriptionRegex = Regex(
      """<meta\s+name=["']description["']\s+content=["'](.*?)["']""",
      setOf(RegexOption.IGNORE_CASE),
    )

    fun jsonEscape(value: String): String =
      buildString(value.length + 8) {
        value.forEach { ch ->
          when (ch) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> Unit
            '\t' -> append("\\t")
            else -> append(ch)
          }
        }
      }

    fun guideName(title: String, slug: String): String {
      val afterDot = title.substringAfterLast('·', missingDelimiterValue = "").trim()
      return afterDot.ifBlank { title.trim().ifBlank { slug } }
    }

    fun guideHint(description: String): String {
      val oneLine = description.replace(Regex("\\s+"), " ").trim()
      if (oneLine.length <= 72) return oneLine
      return oneLine.take(71).trimEnd() + "…"
    }

    fun isPublishedToGuides(dir: java.io.File): Boolean {
      val outline = java.io.File(dir, "outline.md")
      if (!outline.isFile) return true
      val text = outline.readText(Charsets.UTF_8)
      val fm = Regex("^---\\s*\\r?\\n([\\s\\S]*?)\\r?\\n---").find(text)?.groupValues?.get(1)
        ?: return true
      val guidesLine = Regex("""(?m)^guides:\s*(false|true)\s*$""").find(fm)?.groupValues?.get(1)
      return guidesLine != "false"
    }

    val root = presentationsInput.asFile
    val entries = if (!root.isDirectory) {
      emptyList<Triple<String, String, String>>()
    } else {
      root.listFiles()
        .orEmpty()
        .filter { dir -> dir.isDirectory && java.io.File(dir, "index.html").isFile }
        .filter { dir -> isPublishedToGuides(dir) }
        .sortedBy { dir -> dir.name }
        .map { dir ->
          val html = java.io.File(dir, "index.html").readText(Charsets.UTF_8)
          val title = titleRegex.find(html)?.groupValues?.get(1)?.trim().orEmpty()
          val description = descriptionRegex.find(html)?.groupValues?.get(1)?.trim().orEmpty()
          Triple(dir.name, guideName(title, dir.name), guideHint(description))
        }
    }

    val json = entries.joinToString(
      prefix = "[\n",
      postfix = "\n]\n",
      separator = ",\n",
    ) { entry ->
      val slug = entry.first
      val name = entry.second
      val hint = entry.third
      """  {"slug":"${jsonEscape(slug)}","name":"${jsonEscape(name)}","hint":"${jsonEscape(hint)}","href":"images/presentations/${jsonEscape(slug)}/index.html"}"""
    }

    val out = guidesOutput.get().asFile
    out.parentFile.mkdirs()
    out.writeText(json, Charsets.UTF_8)

    // Убрать устаревший hub, если остался от предыдущих сборок.
    java.io.File(presentationsOutDir.get().asFile, "index.html").delete()
  }
}

/**
 * Универсальная синхронизация презентаций в вывод Dokka.
 * Новая презентация = docs/presentations/<slug>/index.html → Guides автоматически.
 */
val syncDocsToDokka = tasks.register("syncDocsToDokka") {
  group = "documentation"
  description = "Копирует presentation runtime, все презентации и манифест Guides."
  dependsOn(
    copyPresentationRuntimeToDokka,
    copyPresentationsToDokka,
    generatePresentationsGuides,
  )
}

generatePresentationsGuides {
  mustRunAfter(copyPresentationsToDokka, copyPresentationRuntimeToDokka)
}

dokka {
  moduleName.set(rootProject.name)

  dokkaPublications.html {
    includes.from(file(".config/dokka/Module.md"))
  }

  pluginsConfiguration.html {
    // TODO: Нет возможности стандартным образом прокинуть логотип и указать путь до него. Поэтому приходится называть именно так файл https://github.com/Kotlin/dokka/issues/4369
    customAssets.from(
      file(".config/dokka/logo-icon.svg")
    )
    customStyleSheets.from(
      file(".config/dokka/archunit-nav.css")
    )
    templatesDir.set(file(".config/dokka/templates"))

    footerMessage.set(
      """
            &copy; ${Year.now().value} Dmitry&nbsp;A.&nbsp;Emelyanenko |
            <a href="images/kover/index.html">Code Coverage</a> |
            <a href="images/presentations/archunit/index.html">ArchUnit</a>
        """.trimIndent()
    )
  }
}

tasks.withType<DokkaGenerateTask>().configureEach {
  finalizedBy(copyKoverToDokka, syncDocsToDokka)
}
