package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import java.util.UUID

/**
 * Генератор UUID, который обрамлён кавычками.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidAsStrGenerator : Generator<UUID> {
  override fun generate(): GeneratorResult<UUID> = GeneratorResultWithEscape(data = UUID.randomUUID())
}