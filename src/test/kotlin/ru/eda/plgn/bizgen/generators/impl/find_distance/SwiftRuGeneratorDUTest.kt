package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.Swift11RuGenerator
import ru.eda.plgn.bizgen.generators.impl.Swift8RuGenerator

/**
 * Определение дистанции для генераторов: [Swift8RuGenerator], [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class SwiftRuGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: Swift8RuGeneratorCases")
  inner class Swift8RuGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Swift8RuGenerator(), distanceLimit = 1..5, minimalUniqueDistance = 3)

  @Nested
  @DisplayName("Testing scope: Swift8RuGeneratorCases")
  inner class Swift11RuGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Swift11RuGenerator(), distanceLimit = 40..50, minimalUniqueDistance = 50)
}