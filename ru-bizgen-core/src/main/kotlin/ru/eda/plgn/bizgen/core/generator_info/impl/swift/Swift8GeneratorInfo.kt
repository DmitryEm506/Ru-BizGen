package ru.eda.plgn.bizgen.core.generator_info.impl.swift

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.Swift8RuGenerator

/**
 * Действие, которое использует [Swift8RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8GeneratorInfo : GeneratorStrInfo(
  id = "SwiftRu8_59ed6b1a-57cf-4e61-ad6b-1a57cf6e616d",
  name = "SWIFT RU (8)",
  generator = Swift8RuGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "SWIFT-код банка (8 символов) — код банка без филиала. Формат: 4 буквы банка + 2 буквы страны (RU) + 2 символа локации.",
  example = "NSVZRUMM",
)