package ru.eda.plgn.bizgen.core.generator_info

import ru.eda.plgn.bizgen.core.generator.Generator

/**
 * Описание генератора.
 *
 * Поля [id] и [name] стабильны и НЕ изменяются между версиями — на них включён персистентный слой плагина.
 *
 * Поля [category], [detailedDescription] и [example] являются источником данных для MCP enum-описаний и будущей группировки в плагине.
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

  /** Категория генератора. */
  val category: GeneratorCategory

  /** Расширенное описание генератора для MCP enum и tooltip плагина. */
  val detailedDescription: String

  /** Стабильный литерал-пример. */
  val example: String
}