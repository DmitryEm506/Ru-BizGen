package ru.eda.plgn.bizgen.core.generator_info.impl.passport

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuMrzGenerator

/**
 * Действие, которое использует [ForeignPassportRuMrzGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuMrzGeneratorInfo : GeneratorStrInfo(
  id = "ForeignPassportRuMrz_d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a",
  name = "Загранпаспорт РФ (MRZ)",
  generator = ForeignPassportRuMrzGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "MRZ-строка загранпаспорта РФ (44 символа в нижней строке машиночитаемой зоны). Формат соответствует ICAO 9303.",
  example = "P<RUSFEDOROV<<MARIYA<<<<<<<<<<<<<<<<<<<<<<<<7155076676RUS0307226M33072502208502684946444",
)
