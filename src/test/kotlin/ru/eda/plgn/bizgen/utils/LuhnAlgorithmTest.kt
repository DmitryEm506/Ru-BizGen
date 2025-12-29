package ru.eda.plgn.bizgen.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseTest
import ru.eda.plgn.bizgen.utils.LuhnAlgorithm.calculateCheckDigit
import ru.eda.plgn.bizgen.utils.LuhnAlgorithm.isValid

/**
 * Модульные тесты для [LuhnAlgorithm].
 *
 * @author Dmitry_Emelyanenko
 */
internal class LuhnAlgorithmTest : BaseTest() {

  @Nested
  @DisplayName("Testing scope: calculateCheckDigit")
  inner class CalculateCheckDigitCases {

    @TestFactory
    internal fun `Should return correct check digit`() = tests(VALID_NUMBERS, { "Card number: $it" }) { cardNumber ->
      // given
      val sourcePayload = cardNumber.dropLast(1)
      val sourceCheckDigit = cardNumber.last().digitToInt()

      // when
      val calculatedCheckDigit = calculateCheckDigit(sourcePayload)

      // then
      calculatedCheckDigit shouldBe sourceCheckDigit
    }

    @Test
    internal fun `Should return exception when length of source is not odd`() {
      // expect
      shouldThrow<IllegalArgumentException> { calculateCheckDigit("1111111111111111") } shouldHaveMessage "The source: 1111111111111111 (length = 16) must have an odd length"
    }
  }

  @Nested
  @DisplayName("Testing scope: isValid")
  inner class IsValidCases {

    @TestFactory
    internal fun `Should return card number is valid`() = tests(VALID_NUMBERS, { "Card number: $it is valid" }) { cardNumber ->
      // expect
      isValid(cardNumber) shouldBe true
    }

    @TestFactory
    internal fun `Should return card number is invalid`() = tests(INVALID_NUMBERS, { "Card number: $it is invalid" }) { cardNumber ->
      // expect
      isValid(cardNumber) shouldBe false
    }

    @Test
    internal fun `Should return exception when length of source is not even`() {
      // expect
      shouldThrow<IllegalArgumentException> { isValid("111111111111111") } shouldHaveMessage "The source: 111111111111111 (length = 15) must have an even length"
    }
  }

  private companion object {
    val VALID_NUMBERS = setOf(

      // Visa
      "4111111111111111",
      "4750657776370372",
      "4486441729154030",
      "4024007123874108",
      "4627100101654724",
      "4012888888881881",
      "4556737586899855",

      // Mastercard
      "5555555555554444",
      "5124585563456201",
      "5569191777864116",
      "5538300838605560",
      "5529263272356119",
      "5467929858074128",
      "5105105105105100",
      "5200828282828210",
      "5431111111111111",
      "5454545454545454",

      // Mir
      "2200000000000004",
      "2204290100000006",
      "2201382000000013",
      "2201382000000104",
      "2201382000000021",
      "2203987654321016",
    )

    val INVALID_NUMBERS = VALID_NUMBERS.map { validNumber ->
      val validCheckDigit = validNumber.last().digitToInt()
      val invalidCheckDigit = (validCheckDigit + (1..9).random()) % 10

      validNumber.dropLast(1) + invalidCheckDigit
    }
  }
}