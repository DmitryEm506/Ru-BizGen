package ru.eda.plgn.bizgen.generators.impl.find_distance

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.impl.AccountCnyGenerator
import ru.eda.plgn.bizgen.generators.impl.AccountRubGenerator

/**
 * Определение дистанции для генераторов: [AccountRubGeneratorCases] и [AccountCnyGeneratorCases].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AccountGeneratorDUTest {

  @Nested
  @DisplayName("Testing scope: AccountRubGenerator")
  inner class AccountRubGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = AccountRubGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)

  @Nested
  @DisplayName("Testing scope: AccountCnyGenerator")
  inner class AccountCnyGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = AccountCnyGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)
}