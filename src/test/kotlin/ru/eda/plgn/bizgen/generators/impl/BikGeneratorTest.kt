package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора БИК - [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BikGeneratorTest : StrGeneratorTest(BikGenerator()) {

  @TestFactory
  internal fun `Should generate a BIK of length 9`() = testsOnDistance { bik ->
    bik shouldHaveLength 9
    bik shouldMatch Regex("^\\d{9}$")
  }

  @TestFactory
  internal fun `Should generate a BIK starting with 04`() = testsOnDistance { bik ->
    bik shouldStartWith "04"
  }

  @Test
  internal fun `Should return a result escaped for the editor`() {
    val result = generator.generate()

    result.toEditor shouldMatch Regex("^\"\\d{9}\"$")
  }
}