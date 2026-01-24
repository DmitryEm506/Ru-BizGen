package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.PhoneNumberRuDigitGenerator
import ru.eda.plgn.bizgen.generators.impl.PhoneNumberRuFormatGenerator

/**
 * Определение дистанции для генераторов:
 * - [PhoneNumberRuDigitGenerator]
 * - [PhoneNumberRuFormatGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class PhoneNumberRuDUTest {

  @Nested
  @DisplayName("Testing scope: PhoneNumberRuFormatGenerator")
  inner class PhoneNumberRuFormatGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = PhoneNumberRuFormatGenerator(), distanceLimit = 129..131, minimalUniqueDistance = 130)

  @Nested
  @DisplayName("Testing scope: PhoneNumberRuDigitGenerator")
  inner class PhoneNumberRuDigitGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = PhoneNumberRuDigitGenerator(), distanceLimit = 129..130, minimalUniqueDistance = 130)
}