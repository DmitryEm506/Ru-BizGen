package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов IBAN.
 *
 * @author Dmitry_Emelyanenko
 */
internal class IbanGeneratorTest {

  @Nested
  @DisplayName("Testing scope: IbanRuGeneratorCases")
  inner class IbanRuGeneratorCases : StrGeneratorTest(IbanRuGenerator()) {

    @TestFactory
    internal fun `Should generate a valid Russian IBAN format (33 chars)`() = testsOnDistance { iban ->
      iban shouldHaveLength 33
      iban shouldMatch Regex("^RU\\d{31}$")
    }

    @Test
    internal fun `Should return an editor-escaped Russian IBAN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"RU\\d{31}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: IbanTurkishGeneratorCases")
  inner class IbanTurkishGeneratorCases : StrGeneratorTest(IbanTurkishGenerator()) {

    @TestFactory
    internal fun `Should generate a valid Turkish IBAN format (26 chars)`() = testsOnDistance { iban ->
      iban shouldHaveLength 26
      iban shouldMatch Regex("^TR\\d{24}$")
    }

    @Test
    internal fun `Should return an editor-escaped Turkish IBAN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"TR\\d{24}\"$")
    }
  }
}
