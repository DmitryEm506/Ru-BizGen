package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

/**
 * Тесты для генераторов паспорта РФ.
 *
 * @author Dmitry_Emelyanenko
 */
internal class PassportRuGeneratorTest {

  @Nested
  @DisplayName("Testing scope: PassportRuSpacedGenerator")
  inner class PassportRuSpacedGeneratorCases : GeneratorStrTest(PassportRuSpacedGenerator()) {

    @TestFactory
    internal fun `Should return in correct format`() = testsOnDistanceToClipboard { passport ->
      passport shouldMatch Regex("^\\d{4} \\d{6}$")
    }

    @Test
    internal fun `Should return an editor-escaped value`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{4} \\d{6}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: PassportRuCompactGenerator")
  inner class PassportRuCompactGeneratorCases : GeneratorStrTest(PassportRuCompactGenerator()) {

    @TestFactory
    internal fun `Should return in correct format`() = testsOnDistanceToClipboard { passport ->
      passport shouldMatch Regex("^\\d{10}$")
    }

    @Test
    internal fun `Should return an editor-escaped value`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{10}\"$")
    }
  }
}
