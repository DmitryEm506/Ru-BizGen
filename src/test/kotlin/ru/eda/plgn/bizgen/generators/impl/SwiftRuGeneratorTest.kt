package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Tests for Russian SWIFT code generators — [Swift8RuGenerator], [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class SwiftRuGeneratorTest {

  @Nested
  @DisplayName("Testing scope: Swift8RuGeneratorCases")
  inner class Swift8RuGeneratorCases : StrGeneratorTest(Swift8RuGenerator()) {

    @TestFactory
    internal fun `Should generate a valid SWIFT code of length 8`() = testsOnDistance(50) { swift ->
      swift shouldMatch Regex("^[A-Z0-9]{8}$")
    }

    @TestFactory
    internal fun `Should generate a Russian SWIFT code (positions 4-6 = RU)`() = testsOnDistance(50) { swift ->
      swift.substring(4, 6) shouldBe "RU"
    }

    @Test
    internal fun `Should return an editor-escaped SWIFT code`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"[A-Z0-9]{8}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: Swift11RuGeneratorCases")
  inner class Swift11RuGeneratorCases : StrGeneratorTest(Swift11RuGenerator()) {

    @TestFactory
    internal fun `Should generate a valid SWIFT code of length 11`() = testsOnDistance { swift ->
      swift shouldMatch Regex("^[A-Z0-9]{8}\\d{3}$")
    }

    @TestFactory
    internal fun `Should generate a Russian SWIFT code (positions 4-6 = RU)`() = testsOnDistance { swift ->
      swift.substring(4, 6) shouldBe "RU"
    }

    @TestFactory
    internal fun `Should generate a SWIFT branch code (last 3 digits)`() = testsOnDistance(50) { swift ->
      swift.substring(8, 11) shouldMatch Regex("^\\d{3}$")
    }

    @Test
    internal fun `Should return an editor-escaped SWIFT code`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"[A-Z0-9]{8}\\d{3}\"$")
    }
  }
}