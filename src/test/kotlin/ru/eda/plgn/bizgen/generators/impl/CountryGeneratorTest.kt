package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора стран - [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CountryGeneratorTest : StrGeneratorTest(CountryGenerator()) {

  @TestFactory
  internal fun `Should return a result containing editor annotations`() = testsOnDistance(500) { country ->
    country shouldMatch Regex(
      """^Country\(code=\d{3}, alpha2Code=[A-Z]{2}, alpha3Code=[A-Z]{3}, name=[А-ЯЁ \-().,']+, engName=[A-Z \-.,()']+\)$"""
    )
  }
}