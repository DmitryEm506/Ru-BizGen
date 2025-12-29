package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.utils.LuhnAlgorithm.calculateCheckDigit
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
 * - N - проверочная цифра, вычисленная на основе алгоритма Луна [ru.eda.plgn.bizgen.utils.LuhnAlgorithm]
 *
 * @author Dmitry_Emelyanenko
 */
class CardNumberGenerator : Generator<String> {
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
      val checkDigit = calculateCheckDigit(withoutCheckDigit)

      return withoutCheckDigit + checkDigit
    }

    fun randomDigits(count: Int): String = (1..count)
      .joinToString("") { ('0'..'9').random().toString() }
  }
}