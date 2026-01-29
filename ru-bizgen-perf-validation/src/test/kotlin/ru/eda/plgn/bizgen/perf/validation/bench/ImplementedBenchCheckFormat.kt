package ru.eda.plgn.bizgen.perf.validation.bench

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider
import ru.eda.plgn.bizgen.perf.bench.BaseGeneratorBenchmark
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Модульные тесты, проверяющие:
 * - Наличие бенчмарка для каждого генератора
 * - Правильность названия класса, который осуществляет бенчмарк
 *
 * @author Dmitry_Emelyanenko
 */
internal class ImplementedBenchCheckFormat {

  @Test
  internal fun `Should have benchmarks on all generators`() {
    // given
    val foundImplementedBenchmarks = findImplemented(BaseGeneratorBenchmark::class)

    // then
    foundImplementedBenchmarks.size shouldBe GeneratorInfoProvider.generatorInfos.size
  }

  @Test
  internal fun `Should Benchmark class have correct name like Name of generator plus const eq Benchmark`() {
    // given
    val foundImplementedBenchmarks = findImplemented(BaseGeneratorBenchmark::class)
    val foundImplementedGenerators = findImplemented(Generator::class)

    // when
    val namesFromBenchmarks = foundImplementedBenchmarks.map { it.simpleName.removeSuffix("Benchmark") }
    val namesGenerators = foundImplementedGenerators.map { it.simpleName }

    // then
    namesFromBenchmarks shouldContainExactlyInAnyOrder namesGenerators
  }

  private companion object {
    fun <T : Any> findImplemented(baseClass: KClass<T>): List<Class<out T>> {
      return Reflections(baseClass.java.packageName, Scanners.SubTypes).getSubTypesOf(baseClass.java).asSequence()
        .filterNot { it.isInterface || Modifier.isAbstract(it.modifiers) }
        // исключаем классы, которые были сгенерированы самим JMH
        .filterNot { it.name.contains(".jmh_generated.") }
        .toList()
    }
  }
}