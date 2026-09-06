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
import org.openjdk.jmh.annotations.Threads
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
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(1)
@Warmup(
  iterations = 3,
  time = 1,
  timeUnit = TimeUnit.SECONDS
)
@Measurement(
  iterations = 10,
  time = 2,
  timeUnit = TimeUnit.SECONDS
)
@Fork(
  value = 2,
  jvmArgsAppend = [
    "-Xms1g", "-Xmx1g",
    "-XX:+UseG1GC",
    "-XX:+AlwaysPreTouch"
  ]
)
abstract class BaseGeneratorBenchmark<T : Any>(private val generatorSupplier: () -> Generator<T>) {
  private lateinit var generator: Generator<T>

  /** Подготовка генератора к тестированию. */
  @Setup
  @Suppress("unused")
  fun setup() {
    generator = generatorSupplier()
  }

  /**
   * Тестирование работы генератора.
   *
   * @return сгенерированное значение
   */
  @Benchmark
  @Suppress("unused")
  fun generate(): String {
    return generator.generate().toEditor
  }
}