package ru.eda.plgn.bizgen.actions.impl.phone_number

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.PhoneNumberRuDigitGenerator

/**
 * Действие, которое использует [PhoneNumberRuDigitGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuDigitActionGenerator : StrActionGenerator(
  id = "PhoneNumberRuDigit_8da95b0a-e39d-425b-a40e-463c51fd4290",
  name = "Номер телефона: 7XXXNNNNNNN",
  generator = PhoneNumberRuDigitGenerator()
)