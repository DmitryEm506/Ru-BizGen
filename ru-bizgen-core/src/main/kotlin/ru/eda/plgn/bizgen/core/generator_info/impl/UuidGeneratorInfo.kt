package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.impl.UuidAsStrGenerator
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
}