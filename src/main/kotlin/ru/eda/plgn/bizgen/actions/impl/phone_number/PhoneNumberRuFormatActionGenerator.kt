package ru.eda.plgn.bizgen.actions.impl.phone_number

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.PhoneNumberRuFormatGenerator

/**
 * Действие, которое использует [PhoneNumberRuFormatGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuFormatActionGenerator : StrActionGenerator(
  id = "PhoneNumberRuFormat_4850d007-4b10-4027-a0f7-55f9f75c3c72",
  name = "Номер телефона: +7 (XXX) NNN-NN-NN",
  generator = PhoneNumberRuFormatGenerator()
)