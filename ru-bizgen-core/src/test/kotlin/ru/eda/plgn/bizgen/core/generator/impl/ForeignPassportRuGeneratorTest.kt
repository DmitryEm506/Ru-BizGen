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
 * Тесты для генераторов загранпаспорта РФ.
 *
 * @author Dmitry_Emelyanenko
 */
internal class ForeignPassportRuGeneratorTest {

  @Nested
  @DisplayName("Testing scope: ForeignPassportRuNumberGenerator")
  inner class ForeignPassportRuNumberGeneratorCases : GeneratorStrTest(ForeignPassportRuNumberGenerator()) {

    @TestFactory
    internal fun `Should return in correct format`() = testsOnDistanceToClipboard { passport ->
      passport shouldMatch Regex("^7[01]\\d{7}$")
    }

    @Test
    internal fun `Should return an editor-escaped value`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"7[01]\\d{7}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: ForeignPassportRuMrzGenerator")
  inner class ForeignPassportRuMrzGeneratorCases : GeneratorStrTest(ForeignPassportRuMrzGenerator()) {

    @TestFactory
    internal fun `Should return valid MRZ of 88 characters`() = testsOnDistanceToClipboard { mrz ->
      mrz shouldHaveLength 88
    }

    @TestFactory
    internal fun `Should have correct line 1 starting with P less-than RUS`() = testsOnDistanceToClipboard { mrz ->
      val line1 = mrz.substring(0, 44)
      line1 shouldStartWith "P<RUS"
      line1 shouldMatch Regex("^P<RUS[A-Z<]+<<[A-Z<]+<*<*$")
    }

    @TestFactory
    internal fun `Should have correct line 2 structure`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      line2 shouldMatch Regex("^\\d{9}\\dRUS\\d{6}\\d[MF]\\d{6}\\d[A-Z0-9<]{14}\\d\\d$")
    }

    @TestFactory
    internal fun `Should have correct passport number check digit`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      val passportNumber = line2.substring(0, 9)
      line2[9] shouldBe mrzCheckDigit(passportNumber)
    }

    @TestFactory
    internal fun `Should have correct date of birth check digit`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      val dob = line2.substring(13, 19)
      line2[19] shouldBe mrzCheckDigit(dob)
    }

    @TestFactory
    internal fun `Should have correct expiry check digit`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      val expiry = line2.substring(21, 27)
      line2[27] shouldBe mrzCheckDigit(expiry)
    }

    @TestFactory
    internal fun `Should have correct personal number check digit`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      val personalNumber = line2.substring(28, 42)
      line2[42] shouldBe mrzCheckDigit(personalNumber)
    }

    @TestFactory
    internal fun `Should have correct composite check digit`() = testsOnDistanceToClipboard { mrz ->
      val line2 = mrz.substring(44, 88)
      val compositeInput = line2.substring(0, 10) +
        line2.substring(13, 20) +
        line2.substring(21, 28) +
        line2.substring(28, 43)
      line2[43] shouldBe mrzCheckDigit(compositeInput)
    }

    @Test
    internal fun `Should return an editor-escaped MRZ`() {
      val result = generator.generate()
      result.toEditor shouldStartWith "\"P<RUS"
    }
  }

  private companion object {
    private val weights = intArrayOf(7, 3, 1)

    private fun mrzCheckDigit(input: String): Char {
      val sum = input.foldIndexed(0) { index, acc, char ->
        val value = when {
          char.isDigit() -> char - '0'
          char in 'A'..'Z' -> char - 'A' + 10
          else -> 0
        }
        acc + value * weights[index % 3]
      }
      return ('0'.code + (sum % 10)).toChar()
    }
  }
}
