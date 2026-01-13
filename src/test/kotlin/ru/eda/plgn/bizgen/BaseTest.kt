package ru.eda.plgn.bizgen

import org.junit.jupiter.api.DynamicTest

/**
 * Базовый тестовый класс.
 *
 * @author Dmitry_Emelyanenko
 */
internal abstract class BaseTest {

  /**
   * Метод генерации динамических тестов [DynamicTest] на основе списка данных.
   *
   * @param T тип данных в списке
   * @param cases список текст-кейсов
   * @param testName наименование создаваемого теста
   * @param test метод тестирования
   * @return список созданных тестов
   */
  protected fun <T : Any> tests(cases: Iterable<T>, testName: (T) -> String, test: (T) -> Unit): Iterable<DynamicTest> =
    cases.map { testCase ->
      DynamicTest.dynamicTest(testName(testCase)) {
        test(testCase)
      }
    }

  /**
   * Проверка на уникальность элементов в коллекции.
   *
   * @param T тип элементов в коллекции
   */
  protected fun <T> Collection<T>.shouldBeUnique() {
    val source = this

    val duplicates = source
      .groupBy { it }
      .filter { it.value.size > 1 }

    if (duplicates.isNotEmpty()) {
      error(
        buildString {
          appendLine("Found duplicates in source collection!")
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
}