package ru.eda.plgn.bizgen.core.generator_info

import ru.eda.plgn.bizgen.core.generator.Generator

/**
 * Описание генератора.
 *
 * @param T тип генерируемого значения
 * @author Dmitry_Emelyanenko
 */
interface GeneratorInfo<T : Any> {

  /** Идентификатор генератора. Должен быть уникальным среди всех описаний. */
  val id: String

  /** Название генератора. */
  val name: String

  /** Генератор. */
  val generator: Generator<T>
}