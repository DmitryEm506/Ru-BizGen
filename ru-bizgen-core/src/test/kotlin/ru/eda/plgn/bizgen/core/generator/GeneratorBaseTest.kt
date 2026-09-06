package ru.eda.plgn.bizgen.core.generator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.BaseTest

/**
 * Базовый класс для тестирования генераторов.
 *
 * @property generator генератор
 * @author Dmitry_Emelyanenko
 */
internal abstract class GeneratorBaseTest<T : Any>(protected val generator: Generator<T>) : BaseTest() {

  @Test
  protected fun `Should return a result`() {
    assertSoftly(generator.generate()) {
      toClipboard shouldNotBe null
      toEditor shouldNotBe null
    }
  }

  @Test
  protected fun `Should return unique values on distance`() {
    var (source, duplicates) = findSourceAndDuplicates(generator, generator.uniqueDistance)
    // Сделано намеренно, так как вероятность, что данные будут всегда (100%) уникальными не может быть.
    // Если данные не уникальные при двух попытках подряд - явно что-то пошло не по плану, как раз будет "стрелять" тест
    // Для проверки именно дистанции уникальности есть отдельная группа тестов
    if (duplicates.isNotEmpty()) {
      with(findSourceAndDuplicates(generator, generator.uniqueDistance)) { source = this.first; duplicates = this.second }
    }

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
          source.forEach { appendLine("  $it") }
        }
      )
    }
  }

  /**
   * Тестирование генератора на дистанции. Тестируется значение, которое будет вставляться в буфер обмена.
   *
   * *Обязательно* должна присутствовать аннотация [TestFactory]
   *
   * @param count количество запусков генератора
   * @param test функция проверки полученного результата
   * @return возвращает список динамических тестов
   */
  protected fun testsOnDistanceToClipboard(count: Int = generator.uniqueDistance, test: (T) -> Unit): Iterable<DynamicTest> {
    return generator.repeatsTestsToClipboard(count, test)
  }

  /**
   * Тестирование генератора на дистанции. Тестируется значение, которое будет вставляться в редактор.
   *
   * *Обязательно* должна присутствовать аннотация [TestFactory]
   *
   * @param count количество запусков генератора
   * @param test функция проверки полученного результата
   * @return возвращает список динамических тестов
   */
  protected fun testsOnDistanceToEditor(count: Int = generator.uniqueDistance, test: (String) -> Unit): Iterable<DynamicTest> {
    return generator.repeatsTestsToEditor(count, test)
  }

  private fun findSourceAndDuplicates(generator: Generator<T>, uniqDistance: Int): Pair<List<String>, Map<String, List<String>>> {
    val source = List(uniqDistance) { generator.generate().toEditor }

    val duplicates = source
      .groupBy { it }
      .filter { it.value.size > 1 }

    return (source to duplicates)
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
    protected fun <T : Any> Generator<T>.repeatsTestsToClipboard(count: Int, test: (T) -> Unit): Iterable<DynamicTest> =
      (0..<count).map {
        val testData = generate().toClipboard
        DynamicTest.dynamicTest(testData.toString()) {
          test(testData)
        }
      }

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
    protected fun <T : Any> Generator<T>.repeatsTestsToEditor(count: Int, test: (String) -> Unit): Iterable<DynamicTest> =
      (0..<count).map {
        val testData = generate().toEditor
        DynamicTest.dynamicTest(testData) {
          test(testData)
        }
      }
  }
}