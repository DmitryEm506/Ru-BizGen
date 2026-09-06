package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import java.util.UUID

/**
 * Генератор UUID, который обрамлён кавычками.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidAsStrGenerator : Generator<UUID> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<UUID> = GeneratorResultWithEscape(data = UUID.randomUUID())
}