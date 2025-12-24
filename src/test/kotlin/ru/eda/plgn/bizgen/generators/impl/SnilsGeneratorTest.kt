package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора СНИЛС — [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class SnilsGeneratorTest : StrGeneratorTest(SnilsGenerator()) {

  @TestFactory
  internal fun `Should return in correct format`() = testsOnDistance { snils ->
    snils shouldMatch Regex("^[1-9]{3}-[1-9]{3}-[1-9]{3} [0-9]{2}$")
  }

  @TestFactory
  internal fun `Should contains correct checkDigits`() = testsOnDistance { snils ->
    val parts = snils.split("-"," ")
    val payload = parts.dropLast(1)
      .joinToString("")
      .map(Char::digitToInt)

    val sourceCheckDigits = parts.last()
    val calculatedCheckDigits = payload.zip(REVERSED_COEFFICIENTS)
      .sumOf { (p, c) -> p * c }
      .mod(101)
      .let { if (it == 100) 0 else it }
      .let { "%02d".format(it) }

    sourceCheckDigits shouldBe calculatedCheckDigits
  }

  private companion object {
    val REVERSED_COEFFICIENTS = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).reversed()
  }
}