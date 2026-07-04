package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest
import java.math.BigInteger

/**
 * Тесты для генераторов IBAN.
 *
 * @author Dmitry_Emelyanenko
 */
internal class IbanGeneratorTest {

  @Nested
  @DisplayName("Testing scope: IbanRuGenerator")
  inner class IbanRuGeneratorCases : GeneratorStrTest(IbanRuGenerator()) {

    @TestFactory
    internal fun `Should generate a valid Russian IBAN format (33 chars)`() = testsOnDistanceToClipboard { iban ->
      iban shouldHaveLength 33
      iban shouldMatch Regex("^RU\\d{31}$")
    }

    @Test
    internal fun `Should return an editor-escaped Russian IBAN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"RU\\d{31}\"$")
    }

    @TestFactory
    internal fun `Should pass mod-97 validation`() = testsOnDistanceToClipboard { iban ->
      validateIbanMod97(iban) shouldBe true
    }

    @Test
    internal fun `Should preserve full account number in BBAN without truncation`() {
      val accountNumber = "30101810500000000456"
      val iban = IbanRuGenerator.IbanGenerator.generateRussianIBAN(accountNumber = accountNumber)
      val bban = iban.substring(4)

      bban shouldHaveLength 29
      bban.endsWith(accountNumber) shouldBe true
    }
  }

  @Nested
  @DisplayName("Testing scope: IbanTurkishGenerator")
  inner class IbanTurkishGeneratorCases : GeneratorStrTest(IbanTurkishGenerator()) {

    @TestFactory
    internal fun `Should generate a valid Turkish IBAN format (26 chars)`() = testsOnDistanceToClipboard { iban ->
      iban shouldHaveLength 26
      iban shouldMatch Regex("^TR\\d{24}$")
    }

    @Test
    internal fun `Should return an editor-escaped Turkish IBAN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"TR\\d{24}\"$")
    }

    @TestFactory
    internal fun `Should pass mod-97 validation`() = testsOnDistanceToClipboard { iban ->
      validateIbanMod97(iban) shouldBe true
    }
  }

  private companion object {
    private fun validateIbanMod97(iban: String): Boolean {
      val moved = iban.substring(4) + iban.substring(0, 4)
      val numeric = moved.map { char ->
        if (char.isLetter()) (char.uppercaseChar() - 'A' + 10).toString()
        else char.toString()
      }.joinToString("")
      return BigInteger(numeric).mod(BigInteger("97")).toInt() == 1
    }
  }
}
