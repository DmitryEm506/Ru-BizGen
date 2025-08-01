package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.Swift11Generator

/**
 * Действие, которое использует [Swift11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11ActionGenerator : BaseGeneratorAction<String>(
  id = "Swift11_c1524ffd-c9d9-465b-924f-fdc9d9565b00",
  name = "SWIFT (11)",
  generator = Swift11Generator()
)