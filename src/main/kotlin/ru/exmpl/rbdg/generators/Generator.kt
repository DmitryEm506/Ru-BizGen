package ru.exmpl.rbdg.generators

/**
 * Описание генератора.
 *
 * @param T тип генерируемых данных
 * @author Dmitry_Emelyanenko
 */
interface Generator<T : Any> {

  /**
   * Генерация данных.
   *
   * @return сгенерированные данные
   */
  fun generate(): GeneratorResult<T>
}