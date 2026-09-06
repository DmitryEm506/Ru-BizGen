package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

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

    @Test
    internal fun `Should generate account with valid control key (Положение 515)`() {
      val bik = BikGenerator.randomBik()
      val account = AccountGenerator.randomAccount("RUB", bik)

      val actualK = account[8].digitToInt()
      val expectedK = calculateAccountControlKey(bik, account, isRkc = false)

      actualK shouldBe expectedK
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

    @Test
    internal fun `Should generate account with valid control key (Положение 515)`() {
      val bik = BikGenerator.randomBik()
      val account = AccountGenerator.randomAccount("CNY", bik)

      val actualK = account[8].digitToInt()
      val expectedK = calculateAccountControlKey(bik, account, isRkc = false)

      actualK shouldBe expectedK
    }
  }

  private companion object {
    private val WEIGHTS = intArrayOf(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1)

    private fun calculateAccountControlKey(bik: String, account: String, isRkc: Boolean): Int {
        val conditionalCode = if (isRkc) {
          "0" + bik.substring(4, 6)
        } else {
          bik.substring(6, 9)
        }
        val accountWithK0 = account.substring(0, 8) + "0" + account.substring(9)
        val base = conditionalCode + accountWithK0
        var sum = 0
        base.forEachIndexed { i, c -> sum += c.digitToInt() * WEIGHTS[i] }
        return (sum % 10) * 3 % 10
    }
  }
}

