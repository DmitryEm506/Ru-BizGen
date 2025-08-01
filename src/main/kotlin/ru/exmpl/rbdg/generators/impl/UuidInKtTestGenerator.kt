package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import java.util.UUID

/**
 * Специфичный генератор UUID, применяемый в тестах, так как вставляется значение, которое содержит вызов его преобразования в UUID.
 *
 * @author Dmitry_Emelyanenko
 */
class UuidInKtTestGenerator : Generator<UUID> {
  override fun generate(): GeneratorResult<UUID> {
    val uuid = UUID.randomUUID()
    val uuidStr = """
      "$uuid".toUUID()
      """.trimIndent()

    return GeneratorResult(toClipboard = uuid, toEditor = uuidStr)
  }
}