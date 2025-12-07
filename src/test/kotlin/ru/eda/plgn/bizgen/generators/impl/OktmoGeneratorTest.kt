package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов ОКТМО — [Oktmo8Generator], [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OktmoGeneratorTest {

  @Nested
  @DisplayName("Testing scope: Oktmo8GeneratorCases")
  inner class Oktmo8GeneratorCases : StrGeneratorTest(Oktmo8Generator(), uniqDistance = 4) {

    @TestFactory
    internal fun `Should generate a valid 8-digit OKTMO`() = testsOnDistance { oktmo ->
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
  @DisplayName("Testing scope: Oktmo11GeneratorCases")
  inner class Oktmo11GeneratorCases : StrGeneratorTest(Oktmo11Generator(), uniqDistance = 4) {

    @TestFactory
    internal fun `Should generate a valid 11-digit OKTMO`() = testsOnDistance { oktmo ->
      oktmo shouldHaveLength 11
      oktmo shouldMatch Regex("""^\d{11}$""")
    }
  }
}