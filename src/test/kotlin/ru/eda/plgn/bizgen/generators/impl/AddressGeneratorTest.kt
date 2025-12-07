package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора адресов - [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AddressGeneratorTest : StrGeneratorTest(AddressGenerator(), uniqDistance = 100) {

  @TestFactory
  internal fun `Should generate an address containing a postal code`() = testsOnDistance { address ->
    address shouldContain Regex("\\d{6}")
  }

  @Test
  internal fun `Should return a result containing editor annotations`() {
    val result = generator.generate()

    result.toEditor shouldContain "zipCode"
    result.toEditor shouldContain "region"
    result.toEditor shouldContain "city"
    result.toEditor shouldContain "street"
  }
}