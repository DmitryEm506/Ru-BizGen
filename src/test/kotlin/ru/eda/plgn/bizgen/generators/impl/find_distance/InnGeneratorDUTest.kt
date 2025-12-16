package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.InnIndividualGenerator
import ru.eda.plgn.bizgen.generators.impl.InnLegalGenerator

/**
 * Определение дистанции для генераторов: [InnIndividualGenerator], [InnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class InnGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: InnIndividualGenerator")
  inner class InnIndividualGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = InnIndividualGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)

  @Nested
  @DisplayName("Testing scope: InnLegalGenerator")
  inner class InnLegalGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = InnLegalGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)
}