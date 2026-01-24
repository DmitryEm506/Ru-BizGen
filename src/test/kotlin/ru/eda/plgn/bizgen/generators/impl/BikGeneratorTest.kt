package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.GeneratorStrTest

/**
 * Тесты для генератора БИК - [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BikGeneratorTest : GeneratorStrTest(BikGenerator()) {

  @TestFactory
  internal fun `Should generate a BIK of length 9`() = testsOnDistanceToClipboard { bik ->
    bik shouldHaveLength 9
    bik shouldMatch Regex("^\\d{9}$")
  }

  @TestFactory
  internal fun `Should generate a BIK starting with 04`() = testsOnDistanceToClipboard { bik ->
    bik shouldStartWith "04"
  }

  @Test
  internal fun `Should return a result escaped for the editor`() {
    val result = generator.generate()

    result.toEditor shouldMatch Regex("^\"\\d{9}\"$")
  }
}