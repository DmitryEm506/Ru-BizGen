package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultAsIs
import java.util.UUID

/**
 * Генератор UUID.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidGenerator : Generator<UUID> {
  override fun generate(): GeneratorResult<UUID> = GeneratorResultAsIs(data = UUID.randomUUID())
}