package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest
import java.math.BigInteger

/**
 * Тесты для генераторов ОГРН — [OgrnLegalGenerator], [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OgrnGeneratorTest {

  @Nested
  @DisplayName("Testing scope: OgrnLegalGenerator")
  inner class OgrnLegalGeneratorCases : GeneratorStrTest(OgrnLegalGenerator()) {

    @TestFactory
    internal fun `Should generate a valid OGRN of length 13`() = testsOnDistanceToClipboard { ogrn ->
      ogrn shouldHaveLength 13
      ogrn shouldMatch Regex("""^\d{13}$""")
    }

    @TestFactory
    internal fun `Should generate an OGRN starting with 1 or 5`() = testsOnDistanceToClipboard { ogrn ->
      ogrn.first() shouldBeIn listOf('1', '5')
    }

    @TestFactory
    internal fun `Should contain correct checksum digit (mod 11)`() = testsOnDistanceToClipboard { ogrn ->
      val base = ogrn.take(12)
      val actualChecksum = ogrn.last()
      val expectedChecksum = BigInteger(base)
        .mod(BigInteger.valueOf(11))
        .toString()
        .last()

      actualChecksum shouldBe expectedChecksum
    }
  }

  @Nested
  @DisplayName("Testing scope: OgrnIpGenerator")
  inner class OgrnIpGeneratorCases : GeneratorStrTest(OgrnIpGenerator()) {

    @TestFactory
    internal fun `Should generate a valid OGRNIP of length 15`() = testsOnDistanceToClipboard { ogrnip ->
      ogrnip shouldHaveLength 15
      ogrnip shouldMatch Regex("""^\d{15}$""")
    }

    @TestFactory
    internal fun `Should generate an OGRNIP starting with 3`() = testsOnDistanceToClipboard { ogrnip ->
      ogrnip.first() shouldBe '3'
    }

    @TestFactory
    internal fun `Should contain correct checksum digit (mod 13)`() = testsOnDistanceToClipboard { ogrnip ->
      val base = ogrnip.take(14)
      val actualChecksum = ogrnip.last()
      val expectedChecksum = BigInteger(base)
        .mod(BigInteger.valueOf(13))
        .toString()
        .last()

      actualChecksum shouldBe expectedChecksum
    }
  }
}