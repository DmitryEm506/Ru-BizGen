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
}