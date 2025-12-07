package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов расчетных счетов - [AccountRubGenerator], [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AccountGeneratorTest {

  @Nested
  @DisplayName("Testing scope: AccountRubGeneratorCases")
  inner class AccountRubGeneratorCases : StrGeneratorTest(AccountRubGenerator(), uniqDistance = 100) {

    @TestFactory
    internal fun `Should generate an account number of length 20`() = testsOnDistance { account ->
      account shouldHaveLength 20
      account shouldMatch Regex("^\\d{20}$")
    }

    @TestFactory
    internal fun `Should generate an account number starting with '407'`() = testsOnDistance { account ->
      account shouldStartWith "407"
    }
  }

  @Nested
  @DisplayName("Testing scope: AccountCnyGenerator")
  inner class AccountCnyGeneratorCases : StrGeneratorTest(AccountCnyGenerator(), uniqDistance = 100) {

    @TestFactory
    internal fun `Should generate an account number of length 20`() = testsOnDistance { account ->
      account shouldHaveLength 20
      account shouldMatch Regex("^\\d{20}$")
    }

    @TestFactory
    internal fun `Should generate an account number starting with '407'`() = testsOnDistance { account ->
      account shouldStartWith "407"
    }
  }
}

