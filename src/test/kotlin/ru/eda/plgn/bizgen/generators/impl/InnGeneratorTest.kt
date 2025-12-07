package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов ИНН.
 *
 * @author Dmitry_Emelyanenko
 */
internal class InnGeneratorTest {

  @Nested
  @DisplayName("Testing scope: InnIndividualGeneratorCases")
  inner class InnIndividualGeneratorCases : StrGeneratorTest(InnIndividualGenerator()) {

    @TestFactory
    internal fun `Should generate a valid individual INN format (12 digits)`() = testsOnDistance { inn ->
      inn shouldHaveLength 12
      inn shouldMatch Regex("""^\d{12}$""")
    }

    @TestFactory
    internal fun `Should generate an individual INN with a valid region (first 2 digits)`() = testsOnDistance { inn ->
      val region = inn.take(2).toInt()
      region shouldBeInRange 1..99
    }

    @Test
    internal fun `Should return an editor-escaped individual INN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{12}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: InnLegalGeneratorCases")
  inner class InnLegalGeneratorCases : StrGeneratorTest(InnLegalGenerator()) {

    @TestFactory
    internal fun `Should generate a valid legal entity INN format (10 digits)`() = testsOnDistance { inn ->
      inn shouldHaveLength 10
      inn shouldMatch Regex("""^\d{10}$""")
    }

    @TestFactory
    internal fun `Should generate a legal entity INN with a valid region (first 2 digits)`() = testsOnDistance { inn ->
      val region = inn.take(2).toInt()
      region shouldBeInRange 1..99
    }

    @Test
    internal fun `Should return an editor-escaped legal entity INN`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{10}\"$")
    }
  }
}