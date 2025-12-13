package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.OrgEngNameGenerator
import ru.eda.plgn.bizgen.generators.impl.OrgRuNameGenerator

/**
 * Определение дистанции для генераторов: [OrgRuNameGenerator], [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OrgNameGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: OrgRuNameGeneratorCases")
  inner class OrgRuNameGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = OrgRuNameGenerator(), distanceLimit = 20..30, minimalUniqueDistance = 25)

  @Nested
  @DisplayName("Testing scope: OrgEngNameGeneratorCases")
  inner class OrgEngNameGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = OrgEngNameGenerator(), distanceLimit = 20..30, minimalUniqueDistance = 25)
}