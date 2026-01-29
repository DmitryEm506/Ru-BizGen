package ru.eda.plgn.bizgen.perf.jmh.report.service

import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider
import ru.eda.plgn.bizgen.perf.jmh.report.data.ResultReportData

/**
 * Конфигурация итогового отчета.
 *
 * @author Dmitry_Emelyanenko
 */
object ReportMdConfigurer {
  val provider = GeneratorInfoProvider.generatorInfos.associate { info ->
    info.generator.javaClass.simpleName to info.name
  }

  val priority = GeneratorInfoProvider.generatorInfos.mapIndexed { index, info ->
    info.generator.javaClass.simpleName to index
  }.toMap()

  /**
   * Конфигурация отчета в формате Markdown.
   *
   * @param data структура отчета
   * @return отчет в формате Markdown
   */
  fun configureMd(data: ResultReportData): String {
    return configureDescription(data) + configureTable(data)
  }

  private fun configureDescription(data: ResultReportData): String {
    return """
# ⚡ Отчет производительности

В этом разделе представлены результаты тестирования скорости работы генераторов, выполненные с использованием [JMH](https://github.com/openjdk/jmh) 
(Java Microbenchmark Harness).

Все измерения проводятся в изолированных условиях для обеспечения точности и воспроизводимости результатов.

## 🛠 Тестовое окружение

| Компонент                  | Спецификация                                                   |
|:---------------------------|:---------------------------------------------------------------|
| **Виртуальная машина**     | ${data.environment.vmNameAndVersion}                           |
| **Версия JDK**             | ${data.environment.jdkVersion}                                 |
| **Фреймворк бенчмаркинга** | JMH ${data.environment.jmhVersion}                             |
| **Цель тестирования**      | Измерение среднего времени выполнения одного вызова генератора |

## ⚙️ Конфигурация JMH

### 📈 Режим измерений

* **Основной режим:** `AverageTime` (среднее время на операцию)
* **Единица измерения:** микросекунды (µs) на операцию

### 🔥 Фаза разогрева (Warmup)

Позволяет JVM выполнить оптимизацию кода и стабилизировать производительность.

* **Количество итераций:** ${data.environment.warmup.iterations}
* **Длительность итерации:** ${data.environment.warmup.time}

### ⏱️ Фаза измерения (Measurement)

Непосредственный замер производительности.

* **Количество итераций:** ${data.environment.measurement.iterations}
* **Длительность итерации:** ${data.environment.measurement.time}

### 🖥 Конфигурация JVM

Каждый тест запускается в отдельном процессе для чистоты эксперимента.

* **Количество процессов (Fork):** ${data.environment.fork.value}
* **Параметры JVM:** `${data.environment.fork.args.joinToString(" ")}`

## 📊 Результаты тестирования

    """.trimIndent()
  }

  private fun configureTable(data: ResultReportData): String {
    return buildString {
      appendLine("| Генератор | Среднее время µs/оп | Выделяемый объем данных в KB |")
      appendLine("|----------|------------------|------------------|")

      data.benchmarks
        .sortedBy { priority[it.name] ?: Int.MAX_VALUE }
        .forEach { benchmark ->
          val testedGenerator = benchmark.name
          val foundGenerator = provider[testedGenerator]
            ?.let { "$testedGenerator - _${it}_" }
            ?: testedGenerator

          appendLine(
            "| $foundGenerator | " +
                "${benchmark.avgTimeMs.round(6)} | " +
                "${(benchmark.allocBytes / 1024.0).round(3)} |"
          )
        }
    } + """

> **💡 Вывод:** Все генераторы работают в **микросекундном диапазоне**, 
> обеспечивая мгновенный отклик в интерфейсе IDE без заметных задержек для пользователя.

> **📌 Примечание:** Запуск бенчмарков и создание отчёта происходит в модуле `ru-bizgen-perf/`
        """.trimIndent()
  }

  private fun Double?.round(digits: Int): String =
    "%.${digits}f".format(this)
}