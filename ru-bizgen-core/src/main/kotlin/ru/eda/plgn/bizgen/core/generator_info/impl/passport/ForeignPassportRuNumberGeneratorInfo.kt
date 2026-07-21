package ru.eda.plgn.bizgen.core.generator_info.impl.passport

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuNumberGenerator

/**
 * Действие, которое использует [ForeignPassportRuNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuNumberGeneratorInfo : GeneratorStrInfo(
  id = "ForeignPassportRuNumber_c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
  name = "Загранпаспорт РФ (номер)",
  generator = ForeignPassportRuNumberGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "Номер загранпаспорта РФ (9 цифр). Серия и номер без пробелов.",
  example = "702964958",
)
