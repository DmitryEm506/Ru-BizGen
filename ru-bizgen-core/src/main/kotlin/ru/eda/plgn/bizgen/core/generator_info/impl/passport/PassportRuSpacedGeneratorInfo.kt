package ru.eda.plgn.bizgen.core.generator_info.impl.passport

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.PassportRuSpacedGenerator

/**
 * Действие, которое использует [PassportRuSpacedGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuSpacedGeneratorInfo : GeneratorStrInfo(
  id = "PassportRuSpaced_a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  name = "Паспорт РФ (с пробелом)",
  generator = PassportRuSpacedGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "Серия и номер паспорта РФ (10 символов) в формате NNNN NNNNNN.",
  example = "1724 442839",
)
