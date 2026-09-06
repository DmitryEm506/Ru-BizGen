package ru.eda.plgn.bizgen.core.generator_info.impl.iban

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.IbanRuGenerator

/**
 * Действие, которое использует [IbanRuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanRuGeneratorInfo : GeneratorStrInfo(
  id = "IbanRu_13cbe98c-fc00-4a93-8be9-8cfc00ca93d2",
  name = "IBAN RU (33)",
  generator = IbanRuGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "IBAN российского банка (33 символа). Формат: RU + 2 контрольные цифры (mod-97) + 4 цифры БИК + 1 ноль + 20 цифр счёта.",
  example = "RU6764808000030101810764808000298",
)