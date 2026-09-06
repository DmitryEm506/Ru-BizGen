package ru.eda.plgn.bizgen.core.generator_info.impl.swift

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.Swift11RuGenerator

/**
 * Действие, которое использует [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11GeneratorInfo : GeneratorStrInfo(
  id = "SwiftRu11_31ae8329-4566-49d6-ae83-294566e9d6f4",
  name = "SWIFT RU (11)",
  generator = Swift11RuGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "SWIFT-код банка (11 символов) — код банка с филиалом. Формат: 8-символьный код + 3-символьный код филиала.",
  example = "NORDRUMM546",
)