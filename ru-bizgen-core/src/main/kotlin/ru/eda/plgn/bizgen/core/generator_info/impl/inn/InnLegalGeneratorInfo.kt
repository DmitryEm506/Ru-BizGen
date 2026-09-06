package ru.eda.plgn.bizgen.core.generator_info.impl.inn

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.InnLegalGenerator

/**
 * Действие, которое использует [InnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnLegalGeneratorInfo : GeneratorStrInfo(
  id = "InnLegal_f5e5e2b3-2d14-4e83-a5e2-b32d140e831a",
  name = "ИНН ЮЛ (10)",
  generator = InnLegalGenerator(),
  category = GeneratorCategory.LEGAL,
  detailedDescription = "ИНН юридического лица (10 цифр). Контрольная сумма: 10-я цифра рассчитывается по весам P10.",
  example = "9589369921",
)
