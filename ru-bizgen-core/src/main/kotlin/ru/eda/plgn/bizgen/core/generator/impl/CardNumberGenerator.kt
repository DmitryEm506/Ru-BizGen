package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.utils.LuhnAlgorithm
import kotlin.random.Random.Default.nextInt

/**
 * Генератор номеров карт.
 *
 * Общая длина генерируемого номера = 16.
 *
 * Поддерживается генерация следующих систем: VISA, Mastercard, МИР.
 *
 * Структура полученного номера: XYN, где:
 * - X - Начальный BIN
 * - Y - случайный номер
 * - N - проверочная цифра, вычисленная на основе алгоритма Луна [LuhnAlgorithm]
 *
 * @author Dmitry_Emelyanenko
 */
class CardNumberGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateNumber(CardBrand.entries.random()))

  /**
   * Тип карты.
   *
   * @property startBin начальный бин, который уникален для каждого типа
   */
  private enum class CardBrand(val startBin: () -> String) {

    /** Visa. */
    VISA(startBin = { "4070" }),

    /** Mastercard. */
    MASTER(startBin = { "5" + nextInt(1, 5) }),

    /** МИР. */
    MIR(startBin = { "220" }),
  }

  private companion object {
    private const val PAN_LENGTH = 16

    fun generateNumber(cardBrand: CardBrand): String {
      val bin = cardBrand.startBin()
      val randomPartLength = PAN_LENGTH - 1 - bin.length

      val accountPart = randomDigits(randomPartLength)
      val withoutCheckDigit = bin + accountPart
      val checkDigit = LuhnAlgorithm.calculateCheckDigit(withoutCheckDigit)

      return withoutCheckDigit + checkDigit
    }

    fun randomDigits(count: Int): String = (1..count)
      .joinToString("") { ('0'..'9').random().toString() }
  }
}