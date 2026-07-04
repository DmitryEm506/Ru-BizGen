package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

/**
 * Тесты для генераторов ИНН.
 *
 * @author Dmitry_Emelyanenko
 */
internal class InnGeneratorTest {

  @Nested
  @DisplayName("Testing scope: InnIndividualGenerator")
  inner class InnIndividualGeneratorCases : GeneratorStrTest(InnIndividualGenerator()) {

    @TestFactory
    internal fun `Should generate a valid individual INN format (12 digits)`() = testsOnDistanceToClipboard { inn ->
      inn shouldHaveLength 12
      inn shouldMatch Regex("""^\d{12}$""")
    }

    @TestFactory
    internal fun `Should generate an individual INN with a valid region (first 2 digits)`() = testsOnDistanceToClipboard { inn ->
      val region = inn.take(2).toInt()
      region shouldBeInRange 1..99
    }

    @TestFactory
    internal fun `Should contain correct checksum digits (P11 + P12)`() = testsOnDistanceToClipboard { inn ->
      val digits = inn.map { it.digitToInt() }

      val sum1 = digits.take(10).zip(P11).sumOf { (d, w) -> d * w }
      val expectedCheck1 = (sum1 % 11) % 10
      digits[10] shouldBe expectedCheck1

      val sum2 = digits.take(11).zip(P12).sumOf { (d, w) -> d * w }
      val expectedCheck2 = (sum2 % 11) % 10
      digits[11] shouldBe expectedCheck2
    }

    @Test
    internal fun `Should return an editor-escaped individual INN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{12}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: InnLegalGenerator")
  inner class InnLegalGeneratorCases : GeneratorStrTest(InnLegalGenerator()) {

    @TestFactory
    internal fun `Should generate a valid legal entity INN format (10 digits)`() = testsOnDistanceToClipboard { inn ->
      inn shouldHaveLength 10
      inn shouldMatch Regex("""^\d{10}$""")
    }

    @TestFactory
    internal fun `Should generate a legal entity INN with a valid region (first 2 digits)`() = testsOnDistanceToClipboard { inn ->
      val region = inn.take(2).toInt()
      region shouldBeInRange 1..99
    }

    @TestFactory
    internal fun `Should contain correct checksum digit (P10)`() = testsOnDistanceToClipboard { inn ->
      val digits = inn.map { it.digitToInt() }

      val sum = digits.take(9).zip(P10).sumOf { (d, w) -> d * w }
      val expectedCheck = (sum % 11) % 10
      digits[9] shouldBe expectedCheck
    }

    @Test
    internal fun `Should return an editor-escaped legal entity INN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{10}\"$")
    }
  }

  private companion object {
    private val P10 = listOf(2, 4, 10, 3, 5, 9, 4, 6, 8)
    private val P11 = listOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
    private val P12 = listOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
  }
}