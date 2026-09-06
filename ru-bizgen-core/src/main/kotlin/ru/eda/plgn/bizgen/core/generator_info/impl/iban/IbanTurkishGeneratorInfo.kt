package ru.eda.plgn.bizgen.core.generator_info.impl.iban

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.IbanTurkishGenerator

/**
 * Действие, которое использует [IbanTurkishGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanTurkishGeneratorInfo : GeneratorStrInfo(
  id = "IbanTurkish_cdb0d09c-2263-407b-b0d0-9c2263007b43",
  name = "IBAN TR (26)",
  generator = IbanTurkishGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "IBAN турецкого банка (26 символов). Формат: TR + 2 контрольные цифры (mod-97) + 5 цифр банка + 1 ноль + 16 цифр счёта.",
  example = "TR620006206252377675806479",
)