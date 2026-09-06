package ru.eda.plgn.bizgen.perf.jmh.report.data

import kotlinx.serialization.Serializable

/**
 * Исходный JMH отчет.
 *
 * @property jmhVersion версия JMH
 * @property jdkVersion версия JDK
 * @property forks количество запуском JVM
 * @property warmupIterations количество циклов разогрева
 * @property warmupTime время разогрева
 * @property measurementIterations количество циклов измерения
 * @property measurementTime время измерения
 * @property jvmArgs аргументы JVM
 * @property vmName название JM
 * @property vmVersion версия JM
 * @property benchmark бенчмарк
 * @property mode режим работы
 * @property primaryMetric основная метрика
 * @property secondaryMetrics список второстепенных метрик
 * @author Dmitry_Emelyanenko
 */
@Serializable
data class JmhSourceReportResult(
  val jmhVersion: String,
  val jdkVersion: String,
  val forks: Int,
  val warmupIterations: Int,
  val warmupTime: String,
  val measurementIterations: Int,
  val measurementTime: String,
  val jvmArgs: List<String>,
  val vmName: String,
  val vmVersion: String,
  val benchmark: String,
  val mode: String,
  val primaryMetric: JmhSourceReportMetric,
  val secondaryMetrics: Map<String, JmhSourceReportMetric>? = null,
)


/**
 * Метрика.
 *
 * @property score значение метрики
 * @author Dmitry_Emelyanenko
 */
@Serializable
data class JmhSourceReportMetric(
  @Serializable(with = JmhScoreSerializer::class)
  val score: Double? = null,
)