package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import ru.exmpl.rbdg.generators.impl.InnGeneratorBase.randomIndividualInn
import ru.exmpl.rbdg.generators.impl.InnGeneratorBase.randomLegalInn
import kotlin.random.Random

/**
 * ИНН Генератор для ФЛ.
 *
 * **See Also:**
 * [Идентификационный номер налогоплательщика](https://ru.wikipedia.org/wiki/%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%89%D0%B8%D0%BA%D0%B0)
 */
internal class InnIndividualGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomIndividualInn())
}

/**
 * ИНН Генератор для ЮЛ.
 *
 * **See Also:**
 * [Идентификационный номер налогоплательщика](https://ru.wikipedia.org/wiki/%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%89%D0%B8%D0%BA%D0%B0)
 */
internal class InnLegalGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomLegalInn())
}

/**
 * Базовая реализация ИНН Генератора.
 *
 * @author Dmitry_Emelyanenko
 */
private object InnGeneratorBase {
  private const val SEQUENCE_NUMBER = 7
  private val P10 = intArrayOf(2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P11 = intArrayOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P12 = intArrayOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)

  /** ИНН ЮЛ. */
  fun randomLegalInn() = inn("LEGAL")

  /** ИНН ФЛ. */
  fun randomIndividualInn() = inn("INDIVIDUAL")

  fun inn(kind: String): String {
    val sequence = Random(SEQUENCE_NUMBER)
    val region = findRegionNumber(null, sequence)
    val sequenceDigits = findDigits(kind, sequence)

    val innWithoutCheckDigit = region + sequenceDigits
    return innWithoutCheckDigit + checkDigit(innWithoutCheckDigit)
  }

  private fun findDigits(kind: String, sequence: Random): String {
    return when (kind) {
      "LEGAL" -> (sequence.nextInt(1_000_000, 10_000_000)).toString()
      "INDIVIDUAL" -> (sequence.nextInt(10_000_000, 100_000_000)).toString()
      else -> throw IllegalArgumentException("Unknown kind: $kind. Available kinds: LEGAL, INDIVIDUAL")
    }
  }

  private fun checkDigit(digits: String): String {
    return if (digits.length == 9) {
      calcInn(P10, digits)
    } else {
      val p11Digit = calcInn(P11, digits)
      p11Digit + calcInn(P12, digits + p11Digit)
    }
  }

  private fun calcInn(p: IntArray, inn: String): String {
    val sum = p.mapIndexed { index, value -> value * (inn[index] - '0') }.sum()
    return ((sum % 11) % 10).toString()
  }

  @Suppress("SameParameterValue")
  private fun findRegionNumber(regionNumber: Int?, sequence: Random): String {
    return regionNumber?.let { String.format("%02d", it) }
      ?: String.format("%02d", sequence.nextInt(1, 100))
  }
}