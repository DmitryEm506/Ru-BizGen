package ru.person.bizgen.actions.impl.swift

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.Swift8Generator

/**
 * Действие, которое использует [Swift8Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8ActionGenerator : BaseGeneratorAction<String>(
  id = "Swift8_17e65ece-fb80-4a2d-a65e-cefb802a2d35",
  name = "SWIFT (8)",
  generator = Swift8Generator()
)