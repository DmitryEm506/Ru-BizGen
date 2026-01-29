package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest
import ru.eda.plgn.bizgen.core.utils.LuhnAlgorithm

/**
 * Тесты для генератора "Номер карты" - [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CardNumberGeneratorTest : GeneratorStrTest(CardNumberGenerator()) {

  @TestFactory
  internal fun `Should return valid card numbers`() = testsOnDistanceToClipboard { cardNumber ->
    // expect
    LuhnAlgorithm.isValid(cardNumber) shouldBe true
  }
}