package ru.eda.plgn.bizgen.perf.jmh.report.data

/**
 * Результирующая структура JMH отчёта.
 *
 * @property environment окружение
 * @property benchmarks список бенчмарков
 * @author Dmitry_Emelyanenko
 */
data class ResultReportData(
  val environment: Environment,
  val benchmarks: List<Benchmark>,
)

/**
 * Окружение.
 *
 * @property vmNameAndVersion название и версия VM
 * @property jdkVersion версия JDK
 * @property jmhVersion версия JMH
 * @property fork количество запуском JVM
 * @property warmup секция разогрева
 * @property measurement секция измерения
 * @author Dmitry_Emelyanenko
 */
data class Environment(
  val vmNameAndVersion: String,
  val jdkVersion: String,
  val jmhVersion: String,
  val fork: Fork,
  val warmup: Warmup,
  val measurement: Measurement,
)

/**
 * Бенчмарк.
 *
 * @property name название бенчмарка. **Должно соответствовать названию генератора**.
 * @property avgTimeMs среднее время генерации
 * @property allocBytes выделяемый объём памяти в байтах
 * @author Dmitry_Emelyanenko
 */
data class Benchmark(
  val name: String,
  val avgTimeMs: Double,
  val allocBytes: Double,
)

/**
 * JVM процесс.
 *
 * @property value количество запусков JVM
 * @property args аргументы JVM
 * @author Dmitry_Emelyanenko
 */
data class Fork(
  val value: Int,
  val args: List<String>,
)

/**
 * Разогрев.
 *
 * @property iterations количество циклов разогрева
 * @property time время разогрева
 * @author Dmitry_Emelyanenko
 */
data class Warmup(
  val iterations: Int,
  val time: String,
)

/**
 * Измерение.
 *
 * @property iterations количество циклов измерения
 * @property time время измерения
 * @author Dmitry_Emelyanenko
 */
data class Measurement(
  val iterations: Int,
  val time: String,
)