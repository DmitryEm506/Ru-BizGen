package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.UuidAsStrGenerator
import java.util.UUID

/**
 * Действие, которое использует [UuidAsStrGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidGeneratorAction : BaseGeneratorAction<UUID>(
  id = "UUID_ee2bca00-0586-4a7c-869c-bee1285d0732",
  name = "UUID как строка",
  generator = UuidAsStrGenerator()
)