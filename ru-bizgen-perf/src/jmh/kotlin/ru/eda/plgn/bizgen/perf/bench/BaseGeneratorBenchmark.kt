package ru.eda.plgn.bizgen.perf.bench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import ru.eda.plgn.bizgen.core.generator.Generator
import java.util.concurrent.TimeUnit

/**
 * Базовый класс для запуска бенчмарков генераторов.
 *
 * @param T тип генерируемого значения
 * @property generatorSupplier функция создания генератора
 * @author Dmitry_Emelyanenko
 */
// Общее состояние для всех потоков
@State(Scope.Benchmark)
// Измеряем среднее время
@BenchmarkMode(Mode.AverageTime)
// Результаты в микросекундах
@OutputTimeUnit(TimeUnit.MICROSECONDS)
// секция разогрева
@Warmup(
  // 5 итерации разогрева
  iterations = 5,

  // каждая по 1 секунде
  time = 1,
  timeUnit = TimeUnit.SECONDS
  // Всего 5 секунды разогрева
)
// секция измерения
@Measurement(
  // 10 итераций измерения
  iterations = 10,
  // каждая по 2 секунде
  time = 2,
  timeUnit = TimeUnit.SECONDS
  // Всего 20 секунд измерений
)
// секция JVM
@Fork(
  // 2 отдельных запуска JVM
  value = 2,
  jvmArgsAppend = [
    // Фиксированный heap 2G
    "-Xms2g", "-Xmx2g",
    // Использовать G1 сборщик мусора
    "-XX:+UseG1GC"
  ]
)
abstract class BaseGeneratorBenchmark<T : Any>(private val generatorSupplier: () -> Generator<T>) {
  private lateinit var generator: Generator<T>

  /** Подготовка генератора к тестированию. */
  @Setup
  fun setup() {
    generator = generatorSupplier()
  }

  /**
   * Тестирование работы генератора.
   *
   * @return сгенерированное значение
   */
  @Benchmark
  fun generate(): String {
    return generator.generate().toEditor
  }
}