package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.core.generator.impl.IbanRuGenerator
import ru.eda.plgn.bizgen.core.generator.impl.IbanTurkishGenerator

/**
 * Определение дистанции для генераторов: [IbanRuGenerator], [IbanTurkishGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class IbanGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: IbanRuGeneratorCases")
  inner class IbanRuGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = IbanRuGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)

  @Nested
  @DisplayName("Testing scope: IbanTurkishGeneratorCases")
  inner class IbanTurkishGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = IbanTurkishGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)
}