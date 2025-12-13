package ru.eda.plgn.bizgen.generators.impl

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import ru.eda.plgn.bizgen.generators.Percentile95DistanceUniqStrTest
import ru.eda.plgn.bizgen.generators.UniqDistanceCategory

/**
 * Определение дистанции для генераторов: [AccountRubGeneratorCases] и [AccountCnyGeneratorCases].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AccountGeneratorDistanceUniqueTest {

  @Nested
  @DisplayName("Testing scope: AccountRubGenerator")
  inner class AccountRubGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = AccountRubGenerator(), category = UniqDistanceCategory.HIGH)

  @Nested
  @DisplayName("Testing scope: AccountCnyGenerator")
  inner class AccountCnyGeneratorCases :
    Percentile95DistanceUniqStrTest(generator = AccountCnyGenerator(), category = UniqDistanceCategory.HIGH)
}