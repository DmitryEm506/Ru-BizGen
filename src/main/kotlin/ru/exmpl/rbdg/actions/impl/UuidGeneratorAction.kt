package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.UuidGenerator
import java.util.UUID

/**
 * Действие, которое использует [UuidGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidGeneratorAction : BaseGeneratorAction<UUID>(
  id = "ee2bca00-0586-4a7c-869c-bee1285d0732",
  name = "UUID",
  generator = UuidGenerator()
)