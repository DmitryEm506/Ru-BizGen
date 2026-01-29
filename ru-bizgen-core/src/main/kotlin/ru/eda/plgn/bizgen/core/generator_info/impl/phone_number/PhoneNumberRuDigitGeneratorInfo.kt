package ru.eda.plgn.bizgen.core.generator_info.impl.phone_number

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuDigitGenerator

/**
 * Действие, которое использует [PhoneNumberRuDigitGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuDigitGeneratorInfo : GeneratorStrInfo(
  id = "PhoneNumberRuDigit_8da95b0a-e39d-425b-a40e-463c51fd4290",
  name = "Номер телефона: 7XXXNNNNNNN",
  generator = PhoneNumberRuDigitGenerator()
)