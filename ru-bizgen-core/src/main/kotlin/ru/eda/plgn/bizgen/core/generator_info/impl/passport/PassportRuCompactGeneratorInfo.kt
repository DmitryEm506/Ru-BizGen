package ru.eda.plgn.bizgen.core.generator_info.impl.passport

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.PassportRuCompactGenerator

/**
 * Действие, которое использует [PassportRuCompactGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuCompactGeneratorInfo : GeneratorStrInfo(
  id = "PassportRuCompact_b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
  name = "Паспорт РФ (без пробела)",
  generator = PassportRuCompactGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "Серия и номер паспорта РФ (10 цифр без разделителей).",
  example = "2625541006",
)
