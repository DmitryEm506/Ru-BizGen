package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов ОГРН — [OgrnLegalGenerator], [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OgrnGeneratorTest {

  @Nested
  @DisplayName("Testing scope: OgrnLegalGeneratorCases")
  inner class OgrnLegalGeneratorCases : StrGeneratorTest(OgrnLegalGenerator()) {

    @TestFactory
    internal fun `Should generate a valid OGRN of length 13`() = testsOnDistance { ogrn ->
      ogrn shouldHaveLength 13
      ogrn shouldMatch Regex("""^\d{13}$""")
    }

    @TestFactory
    internal fun `Should generate an OGRN starting with 1 or 5`() = testsOnDistance { ogrn ->
      ogrn.first() shouldBeIn listOf('1', '5')
    }
  }

  @Nested
  @DisplayName("Testing scope: OgrnIpGeneratorCases")
  inner class OgrnIpGeneratorCases : StrGeneratorTest(OgrnIpGenerator()) {

    @TestFactory
    internal fun `Should generate a valid OGRNIP of length 15`() = testsOnDistance { ogrnip ->
      ogrnip shouldHaveLength 15
      ogrnip shouldMatch Regex("""^\d{15}$""")
    }

    @TestFactory
    internal fun `Should generate an OGRNIP starting with 3`() = testsOnDistance { ogrnip ->
      ogrnip.first() shouldBe '3'
    }
  }
}