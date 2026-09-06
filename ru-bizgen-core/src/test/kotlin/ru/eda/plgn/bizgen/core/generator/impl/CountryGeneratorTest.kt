package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

/**
 * Тесты для генератора стран - [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CountryGeneratorTest : GeneratorStrTest(CountryGenerator()) {

  @TestFactory
  internal fun `Should return a result containing editor annotations`() = testsOnDistanceToClipboard(500) { country ->
    country shouldMatch Regex(
      """^Country\(code=\d{3}, alpha2Code=[A-Z]{2}, alpha3Code=[A-Z]{3}, name=[А-ЯЁ \-().,']+, engName=[A-Z \-.,()']+\)$"""
    )
  }
}