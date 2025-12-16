package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.FIOFullGenerator
import ru.eda.plgn.bizgen.generators.impl.FIOInitialsGenerator
import ru.eda.plgn.bizgen.generators.impl.FIOShortGenerator

/**
 * Определение дистанции для генераторов: [FIOFullGenerator], [FIOShortGenerator], [FIOInitialsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class FIOGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: FIOFullGeneratorCases")
  inner class FIOFullGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = FIOFullGenerator(), distanceLimit = 110..130, minimalUniqueDistance = 120)

  @Nested
  @DisplayName("Testing scope: FIOShortGenerator")
  inner class FIOShortGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = FIOShortGenerator(), distanceLimit = 20..40, minimalUniqueDistance = 30)

  @Nested
  @DisplayName("Testing scope: FIOInitialsGenerator")
  inner class FIOInitialsGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = FIOInitialsGenerator(), distanceLimit = 20..40, minimalUniqueDistance = 30)
}