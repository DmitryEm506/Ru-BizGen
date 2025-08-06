package ru.person.bizgen.actions.impl.swift

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.Swift11RuGenerator

/**
 * Действие, которое использует [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11ActionGenerator : BaseGeneratorAction<String>(
  id = "SwiftRu11_31ae8329-4566-49d6-ae83-294566e9d6f4",
  name = "SWIFT RU (11)",
  generator = Swift11RuGenerator()
)