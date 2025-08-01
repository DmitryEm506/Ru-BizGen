package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.Oktmo8Generator

/**
 * Действие, которое использует [Oktmo8Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo8ActionGenerator : BaseGeneratorAction<String>(
  id = "Oktmo8_d683fd6c-23bb-4ccb-83fd-6c23bbaccb89",
  name = "ОКТМО (8)",
  generator = Oktmo8Generator()
)