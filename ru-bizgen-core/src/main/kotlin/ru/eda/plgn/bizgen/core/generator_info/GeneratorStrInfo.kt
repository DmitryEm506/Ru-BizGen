package ru.eda.plgn.bizgen.core.generator_info

import ru.eda.plgn.bizgen.core.generator.Generator

/**
 * Обертка над [GeneratorInfo], которая используется, когда генератор выдаёт строковые данные.
 *
 * @param id идентификатор генератора
 * @param name название генератора
 * @param generator генератор
 */
abstract class GeneratorStrInfo(
  override val id: String,
  override val name: String,
  override val generator: Generator<String>,
) : GeneratorInfo<String>