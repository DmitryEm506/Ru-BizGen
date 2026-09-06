package ru.eda.plgn.bizgen.perf.jmh.report.service

import ru.eda.plgn.bizgen.perf.jmh.report.data.Benchmark
import ru.eda.plgn.bizgen.perf.jmh.report.data.Environment
import ru.eda.plgn.bizgen.perf.jmh.report.data.Fork
import ru.eda.plgn.bizgen.perf.jmh.report.data.JmhSourceReportResult
import ru.eda.plgn.bizgen.perf.jmh.report.data.Measurement
import ru.eda.plgn.bizgen.perf.jmh.report.data.ResultReportData
import ru.eda.plgn.bizgen.perf.jmh.report.data.Warmup

/**
 * Преобразование исходного JMH отчета в удобную структуру.
 *
 * @author Dmitry_Emelyanenko
 */
object ReportConverter {

  /** Постфикс бенчмарка. Принято соглашение, что название бенчмарка = <Название генератора>Benchmark. */
  private const val BENCHMARK_NAME_POSTFIX = "Benchmark"

  /** Обрабатываемый режим бенчмарка. */
  private const val BENCHMARK_MODE_AVERAGE_TIME = "avgt"

  /**
   * Преобразование исходного [JmhSourceReportResult] в [ResultReportData].
   *
   * @param source исходный JMH отчет
   * @return преобразованная структура
   */
  fun toResultReport(source: List<JmhSourceReportResult>): ResultReportData {
    return ResultReportData(
      environment = extractEnvironment(source),
      benchmarks = extractRows(source),
    )
  }

  /**
   * Извлечение параметров окружения.
   *
   * @param source исходный JMH отчет
   * @return параметры окружения
   */
  private fun extractEnvironment(source: List<JmhSourceReportResult>): Environment {
    return source.first().let {
      Environment(
        vmNameAndVersion = "${it.vmName} (${it.vmVersion})",
        jdkVersion = it.jdkVersion,
        jmhVersion = it.jmhVersion,
        fork = Fork(value = it.forks, args = it.jvmArgs.filterNot { arg -> arg.startsWith("-D") }),
        warmup = Warmup(iterations = it.warmupIterations, time = it.warmupTime),
        measurement = Measurement(iterations = it.measurementIterations, time = it.measurementTime),
      )
    }
  }

  /**
   * Извлечение результатов бенчмарка.
   *
   * @param source исходный JMH отчет
   * @return
   */
  private fun extractRows(source: List<JmhSourceReportResult>): List<Benchmark> {
    return source.asSequence()
      .filter { it.mode == BENCHMARK_MODE_AVERAGE_TIME }
      .mapNotNull { r ->
        val alloc = r.secondaryMetrics?.get("·gc.alloc.rate.norm") ?: return@mapNotNull null

        Benchmark(
          name = r.benchmark.split(".").takeLast(2).first().replace(BENCHMARK_NAME_POSTFIX, ""),
          avgTimeMs = r.primaryMetric.score ?: 0.0,
          allocBytes = alloc.score ?: 0.0,
        )
      }
      .toList()
  }
}