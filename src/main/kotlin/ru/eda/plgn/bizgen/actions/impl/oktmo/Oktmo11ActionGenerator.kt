package ru.eda.plgn.bizgen.actions.impl.oktmo

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.Oktmo11Generator

/**
 * Действие, которое использует [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo11ActionGenerator : StrActionGenerator(
  id = "Oktmo11_3ea1bc6b-011c-4bdc-a1bc-6b011c1bdc7c",
  name = "ОКТМО (11)",
  generator = Oktmo11Generator()
)