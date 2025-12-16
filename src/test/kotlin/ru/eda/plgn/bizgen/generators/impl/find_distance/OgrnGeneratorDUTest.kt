package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.OgrnIpGenerator
import ru.eda.plgn.bizgen.generators.impl.OgrnLegalGenerator

/**
 * Определение дистанции для генераторов: [OgrnLegalGenerator], [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OgrnGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: OgrnLegalGeneratorCases")
  inner class OgrnLegalGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = OgrnLegalGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)

  @Nested
  @DisplayName("Testing scope: OgrnIpGeneratorCases")
  inner class OgrnIpGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = OgrnIpGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)
}