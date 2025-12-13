package ru.eda.plgn.bizgen.generators.impl

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.Percentile95DistanceUniqStrTest
import ru.eda.plgn.bizgen.generators.UniqDistanceCategory

/**
 * Тесты для проверок генераторов [Swift8RuGenerator], [Swift11RuGenerator], что они генерируют в заданной дистанции.
 *
 * @author Dmitry_Emelyanenko
 */
internal class SwiftRuGeneratorDistanceUniqueTest {

  @Nested
  @DisplayName("Testing scope: Swift8RuGeneratorCases")
  inner class Swift8RuGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Swift8RuGenerator(), distanceLimit = 1..5, minimalUniqueDistance = 3)

  @Nested
  @DisplayName("Testing scope: Swift8RuGeneratorCases")
  inner class Swift11RuGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = Swift11RuGenerator(), UniqDistanceCategory.NORMAL)
}