package ru.eda.plgn.bizgen.generators

/**
 * Описание генератора.
 *
 * @param T тип генерируемых данных
 * @author Dmitry_Emelyanenko
 */
fun interface Generator<T : Any> {

  /**
   * Генерация данных.
   *
   * @return сгенерированные данные
   */
  fun generate(): GeneratorResult<T>
}