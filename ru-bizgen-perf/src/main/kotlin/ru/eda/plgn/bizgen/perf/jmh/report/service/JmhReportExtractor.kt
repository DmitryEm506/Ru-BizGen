package ru.eda.plgn.bizgen.perf.jmh.report.service

import kotlinx.serialization.json.Json
import ru.eda.plgn.bizgen.perf.jmh.report.data.JmhSourceReportResult
import java.io.File
import java.io.FileNotFoundException

/**
 * Работа с отчетами JMH.
 *
 * @author Dmitry_Emelyanenko
 */
object JmhReportExtractor {
  //  для запуска через gradle таску
  private const val SOURCE_REPORT_DIR = "build\\reports\\jmh"
  private const val SOURCE_REPORT_FILE = "jmh-result.json"

  //  для запуска через main (без gradle таски)
//  private const val SOURCE_REPORT_DIR = "ru-bizgen-perf\\" + "build\\reports\\jmh"
//  private const val SOURCE_REPORT_FILE = "jmh-result.json"

  /** Настройка для json, чтобы забирать только необходимые поля. */
  private val json = Json { ignoreUnknownKeys = true }

  /**
   * Загрузка исходного JMH отчета в формате JSON.
   *
   * @return список проведенных бенчмарков
   */
  fun loadGeneratedFile(): List<JmhSourceReportResult> {
    val sourceFilePath = "$SOURCE_REPORT_DIR${File.separator}$SOURCE_REPORT_FILE"
    val inputFile = File(sourceFilePath).takeIf { it.exists() } ?: throw FileNotFoundException(sourceFilePath)

    return json.decodeFromString<List<JmhSourceReportResult>>(inputFile.readText())
  }

  /**
   * Сохранение преобразованного отчета.
   *
   * @param content отчет
   * @return путь до сохраненного файла
   */
  fun saveConfiguredReport(content: String): String {
    val resultFilePath = "$SOURCE_REPORT_DIR${File.separator}jmh-report.md"
    val output = File(resultFilePath)
    output.parentFile.mkdirs()
    output.writeText(content)

    return output.absolutePath
  }
}