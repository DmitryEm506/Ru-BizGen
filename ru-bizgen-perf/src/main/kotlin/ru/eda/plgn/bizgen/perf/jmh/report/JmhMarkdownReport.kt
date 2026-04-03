package ru.eda.plgn.bizgen.perf.jmh.report

import ru.eda.plgn.bizgen.perf.jmh.report.service.JmhReportExtractor
import ru.eda.plgn.bizgen.perf.jmh.report.service.ReportConverter
import ru.eda.plgn.bizgen.perf.jmh.report.service.ReportMdConfigurer

/** Точка входа для процедуры генерации отчета по производительности на основе сформированного jmh отчета формата JSON. */
fun main() {
  JmhReportExtractor.loadGeneratedFile()
    .let(ReportConverter::toResultReport)
    .let(ReportMdConfigurer::configureMd)
    .let(JmhReportExtractor::saveConfiguredReport)
    .also { println("Markdown report generated: $it") }
}
