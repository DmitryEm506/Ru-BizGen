package ru.eda.plgn.bizgen.actions.impl.oktmo

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.Oktmo8Generator

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