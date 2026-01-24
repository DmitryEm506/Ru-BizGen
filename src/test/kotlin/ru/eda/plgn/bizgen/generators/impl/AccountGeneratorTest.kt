package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.GeneratorStrTest

/**
 * Тесты для генераторов расчетных счетов - [AccountRubGenerator], [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AccountGeneratorTest {

  @Nested
  @DisplayName("Testing scope: AccountRubGenerator")
  inner class AccountRubGeneratorCases : GeneratorStrTest(AccountRubGenerator()) {

    @TestFactory
    internal fun `Should generate an account number of length 20`() = testsOnDistanceToClipboard { account ->
      account shouldHaveLength 20
      account shouldMatch Regex("^\\d{20}$")
    }

    @TestFactory
    internal fun `Should generate an account number starting with '407'`() = testsOnDistanceToClipboard { account ->
      account shouldStartWith "407"
    }
  }

  @Nested
  @DisplayName("Testing scope: AccountCnyGenerator")
  inner class AccountCnyGeneratorCases : GeneratorStrTest(AccountCnyGenerator()) {

    @TestFactory
    internal fun `Should generate an account number of length 20`() = testsOnDistanceToClipboard { account ->
      account shouldHaveLength 20
      account shouldMatch Regex("^\\d{20}$")
    }

    @TestFactory
    internal fun `Should generate an account number starting with '407'`() = testsOnDistanceToClipboard { account ->
      account shouldStartWith "407"
    }
  }
}

