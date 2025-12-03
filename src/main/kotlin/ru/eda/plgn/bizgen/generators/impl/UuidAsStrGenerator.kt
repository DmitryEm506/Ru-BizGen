package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import java.util.UUID

/**
 * Генератор UUID, который обрамлён кавычками.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidAsStrGenerator : Generator<UUID> {
  override fun generate(): GeneratorResult<UUID> = GeneratorResultWithEscape(data = UUID.randomUUID())
}