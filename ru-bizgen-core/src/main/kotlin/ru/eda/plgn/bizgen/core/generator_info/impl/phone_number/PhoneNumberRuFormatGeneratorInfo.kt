package ru.eda.plgn.bizgen.core.generator_info.impl.phone_number

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuFormatGenerator

/**
 * Действие, которое использует [PhoneNumberRuFormatGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuFormatGeneratorInfo : GeneratorStrInfo(
  id = "PhoneNumberRuFormat_4850d007-4b10-4027-a0f7-55f9f75c3c72",
  name = "Номер телефона: +7 (XXX) NNN-NN-NN",
  generator = PhoneNumberRuFormatGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "Номер телефона в формате +7 (XXX) NNN-NN-NN. Код оператора выбирается случайно из диапазонов мобильных номеров РФ.",
  example = "+7 (912) 896-75-02",
)