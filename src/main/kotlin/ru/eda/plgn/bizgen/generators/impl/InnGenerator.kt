package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import kotlin.random.Random

/**
 * ИНН Генератор для ФЛ.
 *
 * **See Also:**
 * [Идентификационный номер налогоплательщика](https://ru.wikipedia.org/wiki/%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%89%D0%B8%D0%BA%D0%B0)
 */
internal class InnIndividualGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = InnGeneratorBase.randomIndividualInn())
}

/**
 * ИНН Генератор для ЮЛ.
 *
 * **See Also:**
 * [Идентификационный номер налогоплательщика](https://ru.wikipedia.org/wiki/%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%89%D0%B8%D0%BA%D0%B0)
 */
internal class InnLegalGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = InnGeneratorBase.randomLegalInn())
}

/**
 * Базовая реализация ИНН Генератора.
 *
 * @author Dmitry_Emelyanenko
 */
private object InnGeneratorBase {
  private const val MIN_REGION = 1
  private const val MAX_REGION = 99

  private val P10 = intArrayOf(2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P11 = intArrayOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P12 = intArrayOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)

  private enum class InnType {
    LEGAL,
    INDIVIDUAL
  }

  /** ИНН ЮЛ. */
  fun randomLegalInn(): String = generateInn(InnType.LEGAL)

  /** ИНН ФЛ. */
  fun randomIndividualInn(): String = generateInn(InnType.INDIVIDUAL)

  private fun generateInn(type: InnType): String {
    val random = Random.Default
    val region = generateRegion(random)
    val sequence = generateSequence(type, random)

    val baseInn = region + sequence
    return baseInn + calculateCheckDigits(baseInn)
  }

  private fun generateRegion(random: Random): String {
    return random.nextInt(MIN_REGION, MAX_REGION).toString().padStart(2, '0')
  }

  private fun generateSequence(type: InnType, random: Random): String {
    return when (type) {
      InnType.LEGAL -> random.nextInt(1_000_000, 10_000_000).toString()
      InnType.INDIVIDUAL -> random.nextInt(10_000_000, 100_000_000).toString()
    }
  }

  private fun calculateCheckDigits(baseInn: String): String {
    return when (baseInn.length) {
      9 -> calculateCheckDigit(P10, baseInn)
      10 -> {
        val firstDigit = calculateCheckDigit(P11, baseInn)
        val secondDigit = calculateCheckDigit(P12, baseInn + firstDigit)
        firstDigit + secondDigit
      }

      else -> throw IllegalArgumentException("Invalid base INN length: ${baseInn.length}. Available values: 9 or 10")
    }
  }

  private fun calculateCheckDigit(weights: IntArray, digits: String): String {
    require(digits.length == weights.size) { "Digits length must match weights size" }

    val sum = digits.foldIndexed(0) { index, acc, c ->
      acc + weights[index] * (c - '0')
    }

    return ((sum % 11) % 10).toString()
  }
}