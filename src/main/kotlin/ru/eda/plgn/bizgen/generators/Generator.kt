package ru.eda.plgn.bizgen.generators

/**
 * Описание генератора.
 *
 * @param T тип генерируемых данных
 * @author Dmitry_Emelyanenko
 */
interface Generator<T : Any> {

  /** Количество вызовов, при котором с вероятностью 95% будет уникальное значение. */
  val uniqueDistance: Int

  /**
   * Генерация данных.
   *
   * @return сгенерированные данные
   */
  fun generate(): GeneratorResult<T>
}