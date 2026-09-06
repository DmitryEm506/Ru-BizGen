package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest
import ru.eda.plgn.bizgen.core.utils.AccountKeyAlgorithm

/**
 * Тесты для генератора корреспондентских счетов - [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BankAccountGeneratorTest : GeneratorStrTest(BankAccountGenerator()) {

  @TestFactory
  internal fun `Should generate a valid correspondent account format of 20 digits`() = testsOnDistanceToClipboard { account ->
    account shouldHaveLength 20
    account shouldMatch Regex("^\\d{20}$")
  }

  @TestFactory
  internal fun `Should generate an account starting with 30101810`() = testsOnDistanceToClipboard { account ->
    account shouldMatch Regex("^30101810\\d{12}$")
  }

  @Test
  internal fun `Should return a result escaped for the editor`() {
    val result = generator.generate()

    result.toEditor shouldMatch Regex("^\"\\d{20}\"$")
  }

  @Test
  internal fun `Should generate account with valid control key (Положение 515)`() {
    val bik = BikGenerator.randomBik()
    val account = BankAccountGenerator.randomCorrespondentAccount(bik)

    val actualK = account[8].digitToInt()
    val expectedK = AccountKeyAlgorithm.calculateControlKey(bik, account, isRkc = true)

    actualK shouldBe expectedK
  }
}