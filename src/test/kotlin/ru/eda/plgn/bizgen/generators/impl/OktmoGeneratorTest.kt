package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.GeneratorStrTest

/**
 * Тесты для генераторов ОКТМО — [Oktmo8Generator], [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OktmoGeneratorTest {

  @Nested
  @DisplayName("Testing scope: Oktmo8Generator")
  inner class Oktmo8GeneratorCases : GeneratorStrTest(Oktmo8Generator()) {

    @TestFactory
    internal fun `Should generate a valid 8-digit OKTMO`() = testsOnDistanceToClipboard { oktmo ->
      oktmo shouldHaveLength 8
      oktmo shouldMatch Regex("""^\d{8}$""")
    }

    @Test
    internal fun `Should return an editor-escaped OKTMO`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"\\d{8}\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: Oktmo11Generator")
  inner class Oktmo11GeneratorCases : GeneratorStrTest(Oktmo11Generator()) {

    @TestFactory
    internal fun `Should generate a valid 11-digit OKTMO`() = testsOnDistanceToClipboard { oktmo ->
      oktmo shouldHaveLength 11
      oktmo shouldMatch Regex("""^\d{11}$""")
    }
  }
}