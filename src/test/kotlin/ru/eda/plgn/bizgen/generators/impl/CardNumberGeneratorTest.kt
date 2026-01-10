package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest
import ru.eda.plgn.bizgen.utils.LuhnAlgorithm

/**
 * Тесты для генератора "Номер карты" - [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CardNumberGeneratorTest : StrGeneratorTest(CardNumberGenerator()) {

  @TestFactory
  internal fun `Should return valid card numbers`() = testsOnDistance { cardNumber ->
    // expect
    LuhnAlgorithm.isValid(cardNumber) shouldBe true
  }
}