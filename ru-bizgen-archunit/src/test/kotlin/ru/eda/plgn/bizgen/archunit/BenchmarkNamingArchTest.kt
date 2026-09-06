package ru.eda.plgn.bizgen.archunit

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import io.kotest.matchers.shouldBe
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider
import ru.eda.plgn.bizgen.perf.bench.BaseGeneratorBenchmark
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * ArchUnit-проверки соответствия JMH-бенчмарков генераторам Ru BizGen: количество, взаимно-однозначное сопоставление имён и согласованность
 * с [GeneratorInfoProvider].
 *
 * @author Dmitry_Emelyanenko
 */
@AnalyzeClasses(
  packages = ["ru.eda.plgn.bizgen"],
  importOptions = [ImportOption.DoNotIncludeTests::class],
)
internal class BenchmarkNamingArchTest {

  @ArchTest
  fun concreteBenchmarkCountMatchesGeneratorInfos(importedClasses: JavaClasses) {
    concreteBenchmarks(importedClasses).size shouldBe GeneratorInfoProvider.generatorInfos.size
  }

  @ArchTest
  fun everyConcreteGeneratorHasMatchingBenchmark(importedClasses: JavaClasses) {
    val benchmarkSimpleNames = concreteBenchmarks(importedClasses).map { it.simpleName }.toSet()

    ArchRuleDefinition.classes()
      .that(isConcreteGenerator())
      .should(haveBenchmarkNamed(benchmarkSimpleNames))
      .because("для каждого генератора должен существовать JMH-бенчмарк с именем <GeneratorSimpleName>Benchmark")
      .check(importedClasses)
  }

  @ArchTest
  fun everyConcreteBenchmarkHasMatchingGenerator(importedClasses: JavaClasses) {
    val generatorSimpleNames = concreteGenerators(importedClasses).map { it.simpleName }.toSet()

    ArchRuleDefinition.classes()
      .that(isConcreteBenchmark())
      .should(haveGeneratorNamed(generatorSimpleNames))
      .because("имя каждого бенчмарка должно совпадать с именем генератора плюс суффикс Benchmark")
      .check(importedClasses)
  }

  private companion object {

    private fun concreteGenerators(classes: JavaClasses): JavaClasses =
      classes.that(isConcreteGenerator())

    private fun concreteBenchmarks(classes: JavaClasses): JavaClasses =
      classes.that(isConcreteBenchmark())

    private fun isConcreteGenerator(): DescribedPredicate<JavaClass> =
      DescribedPredicate.describe("являются конкретными реализациями Generator") { javaClass ->
        javaClass.isAssignableTo(Generator::class.java) &&
            !javaClass.isInterface &&
            !javaClass.modifiers.contains(JavaModifier.ABSTRACT) &&
            !javaClass.name.contains(".jmh_generated.")
      }

    private fun isConcreteBenchmark(): DescribedPredicate<JavaClass> =
      DescribedPredicate.describe("являются конкретными реализациями BaseGeneratorBenchmark") { javaClass ->
        javaClass.isAssignableTo(BaseGeneratorBenchmark::class.java) &&
            !javaClass.isInterface &&
            !javaClass.modifiers.contains(JavaModifier.ABSTRACT) &&
            !javaClass.name.contains(".jmh_generated.") &&
            javaClass.simpleName != BaseGeneratorBenchmark::class.simpleName &&
            javaClass.simpleName != StrGeneratorBenchmark::class.simpleName
      }

    private fun haveBenchmarkNamed(benchmarkSimpleNames: Set<String>): ArchCondition<JavaClass> =
      object : ArchCondition<JavaClass>("иметь бенчмарк <SimpleName>Benchmark") {
        override fun check(generator: JavaClass, events: ConditionEvents) {
          val expectedBenchmarkName = "${generator.simpleName}Benchmark"
          if (expectedBenchmarkName !in benchmarkSimpleNames) {
            events.add(
              SimpleConditionEvent.violated(
                generator,
                "для генератора ${generator.name} ожидается бенчмарк $expectedBenchmarkName",
              ),
            )
          }
        }
      }

    private fun haveGeneratorNamed(generatorSimpleNames: Set<String>): ArchCondition<JavaClass> =
      object : ArchCondition<JavaClass>("иметь генератор без суффикса Benchmark") {
        override fun check(benchmark: JavaClass, events: ConditionEvents) {
          val expectedGeneratorName = benchmark.simpleName.removeSuffix("Benchmark")
          if (expectedGeneratorName !in generatorSimpleNames) {
            events.add(
              SimpleConditionEvent.violated(
                benchmark,
                "для бенчмарка ${benchmark.name} ожидается генератор $expectedGeneratorName",
              ),
            )
          }
        }
      }
  }
}
