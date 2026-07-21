package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.impl.UuidAsStrGenerator
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import java.util.UUID

/**
 * Действие, которое использует [UuidAsStrGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidGeneratorInfo : GeneratorInfo<UUID> {
  override val id: String = "UUID_ee2bca00-0586-4a7c-869c-bee1285d0732"
  override val name: String = "UUID как строка"
  override val generator: Generator<UUID> = UuidAsStrGenerator()
  override val category: GeneratorCategory = GeneratorCategory.TECHNICAL
  override val detailedDescription: String =
    "UUID версии 4 как строка в каноническом формате 8-4-4-4-12 (36 символов). " +
      "Генерируется случайным образом, не относится к реальным субъектам."
  override val example: String = "550e8400-e29b-41d4-a716-446655440000"
}