package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.Oktmo11Generator
import ru.eda.plgn.bizgen.generators.impl.Oktmo8Generator

/**
 * Определение дистанции для генераторов: [Oktmo8Generator], [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OktmoGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: Oktmo8GeneratorCases")
  inner class Oktmo8GeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Oktmo8Generator(), distanceLimit = 1..5, minimalUniqueDistance = 4)

  @Nested
  @DisplayName("Testing scope: Oktmo11GeneratorCases")
  inner class Oktmo11GeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Oktmo11Generator(), distanceLimit = 1..5, minimalUniqueDistance = 5)
}