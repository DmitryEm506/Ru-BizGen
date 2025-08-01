package ru.person.bizgen.generators.impl

import ru.person.bizgen.generators.Generator
import ru.person.bizgen.generators.GeneratorResult
import ru.person.bizgen.generators.GeneratorResultWithEscape
import java.util.UUID

/**
 * Генератор UUID, который обрамлён кавычками.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidAsStrGenerator : Generator<UUID> {
  override fun generate(): GeneratorResult<UUID> = GeneratorResultWithEscape(data = UUID.randomUUID())
}