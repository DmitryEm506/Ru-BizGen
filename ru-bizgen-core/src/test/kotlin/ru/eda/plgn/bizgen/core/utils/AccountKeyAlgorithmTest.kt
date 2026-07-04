package ru.eda.plgn.bizgen.core.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.BaseTest

/**
 * Модульные тесты для [AccountKeyAlgorithm].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AccountKeyAlgorithmTest : BaseTest() {

  @Nested
  @DisplayName("Testing scope: calculateControlKey")
  inner class CalculateControlKeyCases {

    @Test
    internal fun `Should return correct key for known RKC example (Положение 515)`() {
      val bik = "049805000"
      val accountWithK0 = "30101810000000000746"

      val key = AccountKeyAlgorithm.calculateControlKey(bik, accountWithK0, isRkc = true)

      key shouldBe 8
    }

    @Test
    internal fun `Should return 0 when all digits are 0`() {
      val bik = "040000000"
      val account = "00000000000000000000"

      val key = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = false)

      key shouldBe 0
    }

    @TestFactory
    internal fun `Should produce key that passes round-trip verification (RKC)`() =
      tests(ROUND_TRIP_CASES_RKC, { "RKC round-trip: bik=${it.first}" }) { (bik, account) ->
        val key = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = true)
        val accountWithKey = account.substring(0, 8) + key + account.substring(9)

        val verificationSum = verificationSumRkc(bik, accountWithKey)

        (verificationSum % 10) shouldBe 0
      }

    @TestFactory
    internal fun `Should produce key that passes round-trip verification (credit org)`() =
      tests(ROUND_TRIP_CASES_CO, { "CO round-trip: bik=${it.first}" }) { (bik, account) ->
        val key = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = false)
        val accountWithKey = account.substring(0, 8) + key + account.substring(9)

        val verificationSum = verificationSumCo(bik, accountWithKey)

        (verificationSum % 10) shouldBe 0
      }

    @Test
    internal fun `Should use RKC conditional code (positions 5-6 of BIK prefixed with 0)`() {
      val bik = "049805000"
      val account = "30101810000000000746"

      val keyRkc = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = true)

      val bikSameRkc = "049805999"
      val keySameRkc = AccountKeyAlgorithm.calculateControlKey(bikSameRkc, account, isRkc = true)

      keyRkc shouldBe keySameRkc
    }

    @Test
    internal fun `Should use credit-org conditional code (positions 7-9 of BIK)`() {
      val bik = "044525225"
      val account = "40701810000000000001"

      val key = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = false)
      val keySameCo = AccountKeyAlgorithm.calculateControlKey("049999225", account, isRkc = false)

      key shouldBe keySameCo
    }

    @Test
    internal fun `Should produce different keys for RKC vs credit-org with same account`() {
      val bik = "044525225"
      val account = "30101810000000000746"

      val keyRkc = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = true)
      val keyCo = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = false)

      keyRkc shouldNotBe keyCo
    }
  }

  @Nested
  @DisplayName("Testing scope: validation errors")
  inner class ValidationCases {

    @Test
    internal fun `Should throw when BIK length is not 9`() {
      shouldThrow<IllegalArgumentException> {
        AccountKeyAlgorithm.calculateControlKey("04452522", "00000000000000000000", isRkc = false)
      } shouldHaveMessage "БИК должен содержать 9 цифр"
    }

    @Test
    internal fun `Should throw when BIK contains non-digits`() {
      shouldThrow<IllegalArgumentException> {
        AccountKeyAlgorithm.calculateControlKey("04452522A", "00000000000000000000", isRkc = false)
      } shouldHaveMessage "БИК должен содержать 9 цифр"
    }

    @Test
    internal fun `Should throw when account length is not 20`() {
      shouldThrow<IllegalArgumentException> {
        AccountKeyAlgorithm.calculateControlKey("044525225", "0000000000000000000", isRkc = false)
      } shouldHaveMessage "Номер счёта должен содержать 20 цифр"
    }

    @Test
    internal fun `Should throw when account contains non-digits`() {
      shouldThrow<IllegalArgumentException> {
        AccountKeyAlgorithm.calculateControlKey("044525225", "0000000000000000000A", isRkc = false)
      } shouldHaveMessage "Номер счёта должен содержать 20 цифр"
    }
  }

  private companion object {
    private val WEIGHTS = intArrayOf(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1)

    private val ROUND_TRIP_CASES_RKC: List<Pair<String, String>> = listOf(
      "049805000" to "30101810000000000746",
      "044525001" to "30101810000000000123",
      "040000000" to "30101810999999999999",
    )

    private val ROUND_TRIP_CASES_CO: List<Pair<String, String>> = listOf(
      "044525225" to "40701810000000000001",
      "044525593" to "40701810234567890123",
      "044525000" to "40702810000000000999",
    )

    private fun verificationSumRkc(bik: String, account: String): Int {
      val conditionalCode = "0" + bik.substring(4, 6)
      return sumWithWeights(conditionalCode + account)
    }

    private fun verificationSumCo(bik: String, account: String): Int {
      val conditionalCode = bik.substring(6, 9)
      return sumWithWeights(conditionalCode + account)
    }

    private fun sumWithWeights(base: String): Int {
      var sum = 0
      base.forEachIndexed { i, c -> sum += c.digitToInt() * WEIGHTS[i] }
      return sum
    }
  }
}
