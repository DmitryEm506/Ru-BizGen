package ru.person.bizgen.actions.impl.oktmo

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.Oktmo11Generator

/**
 * Действие, которое использует [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo11ActionGenerator : BaseGeneratorAction<String>(
  id = "Oktmo11_3ea1bc6b-011c-4bdc-a1bc-6b011c1bdc7c",
  name = "ОКТМО (11)",
  generator = Oktmo11Generator()
)