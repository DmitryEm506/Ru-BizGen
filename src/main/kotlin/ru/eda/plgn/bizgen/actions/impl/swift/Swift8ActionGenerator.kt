package ru.eda.plgn.bizgen.actions.impl.swift

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.Swift8RuGenerator

/**
 * Действие, которое использует [Swift8RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8ActionGenerator : BaseGeneratorAction<String>(
  id = "SwiftRu8_59ed6b1a-57cf-4e61-ad6b-1a57cf6e616d",
  name = "SWIFT RU (8)",
  generator = Swift8RuGenerator()
)