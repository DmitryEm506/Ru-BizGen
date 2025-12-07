package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора корреспондентских счетов - [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BankAccountGeneratorTest : StrGeneratorTest(BankAccountGenerator(), uniqDistance = 100) {

  @TestFactory
  internal fun `Should generate a valid correspondent account format of 20 digits`() = testsOnDistance { account ->
    account shouldHaveLength 20
    account shouldMatch Regex("^\\d{20}$")
  }

  @TestFactory
  internal fun `Should generate an account starting with 30101810`() = testsOnDistance { account ->
    account shouldMatch Regex("^30101810\\d{12}$")
  }

  @Test
  internal fun `Should return a result escaped for the editor`() {
    val result = generator.generate()

    result.toEditor shouldMatch Regex("^\"\\d{20}\"$")
  }
}