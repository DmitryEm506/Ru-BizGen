package ru.eda.plgn.bizgen.actions.impl.swift

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.Swift11RuGenerator

/**
 * Действие, которое использует [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11ActionGenerator : StrActionGenerator(
  id = "SwiftRu11_31ae8329-4566-49d6-ae83-294566e9d6f4",
  name = "SWIFT RU (11)",
  generator = Swift11RuGenerator()
)