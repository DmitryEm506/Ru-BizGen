package ru.eda.plgn.bizgen.generators

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseTest

/**
 * Базовый класс для тестирования генераторов.
 *
 * @property generator генератор
 * @property uniqDistance дистанция, количество попыток, когда считается, что должны быть уникальные значения
 * @author Dmitry_Emelyanenko
 */
internal abstract class GeneratorBaseTest<T : Any>(protected val generator: Generator<T>, private val uniqDistance: Int = 30) : BaseTest() {

  @Test
  protected fun `Should return a result`() {
    assertSoftly(generator.generate()) {
      toClipboard shouldNotBe null
      toEditor shouldNotBe null
    }
  }

  @Test
  protected fun `Should return unique values on distance`() {
    val sequence = List(uniqDistance) { generator.generate().toEditor }

    val duplicates = sequence
      .groupBy { it }
      .filter { it.value.size > 1 }

    if (duplicates.isNotEmpty()) {
      error(
        buildString {
          appendLine("Found duplicates in generated values!")
          appendLine()
          appendLine("Duplicates:")
          duplicates.forEach { (value, occurrences) ->
            appendLine("  '$value' occurred ${occurrences.size} times")
          }
          appendLine()
          appendLine("Full sequence:")
          sequence.forEach { appendLine("  $it") }
        }
      )
    }
  }

  /**
   * Тестирование генератора на дистанции.
   *
   * *Обязательно* должна присутствовать аннотация [TestFactory]
   *
   * @param count количество запусков генератора
   * @param test функция проверки полученного результата
   * @return возвращает список динамических тестов
   */
  protected fun testsOnDistance(count: Int = uniqDistance, test: (T) -> Unit): Iterable<DynamicTest> {
    return generator.repeatsTests(count, test)
  }

  protected companion object {

    /**
     * Расширение для генератора, которое позволяет запускать его множество раз и тестировать полученный результат.
     *
     * *Обязательно* должна присутствовать аннотация [TestFactory]
     *
     * @param T тип генерируемого значения
     * @param count количество запусков генератора
     * @param test функция тестирования значения
     * @return возвращает список динамических тестов
     */
    protected fun <T : Any> Generator<T>.repeatsTests(count: Int, test: (T) -> Unit): Iterable<DynamicTest> =
      (0..<count).map {
        val testData = generate().toClipboard
        DynamicTest.dynamicTest(testData.toString()) {
          test(testData)
        }
      }
  }
}