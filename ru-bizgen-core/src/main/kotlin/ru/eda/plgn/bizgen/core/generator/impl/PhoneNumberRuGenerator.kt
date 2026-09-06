package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuAlgorithm.generateWithFormatting
import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuAlgorithm.generateWithoutFormatting
import kotlin.random.Random

/**
 * Генератор российского номера телефона.
 *
 * Формат: +7 (XXX) NNN-NN-NN, где XXX - трехзначный код оператора, N - набор случайных чисел от 0-9.
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuFormatGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateWithFormatting())
}

/**
 * Генератор российского номера телефона.
 *
 * Формат: 7XXXNNNNNNN, где XXX - трехзначный код оператора, N - набор случайных чисел от 0-9.
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuDigitGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateWithoutFormatting())
}

private object PhoneNumberRuAlgorithm {
  private const val RU_CODE = "7"

  private enum class Operators(val numbers: Collection<Int>) {
    BEELINE((900..909).toSet() + (960..969).toSet()),
    MTS((910..919).toSet() + (980..989).toSet()),
    MEGA_FON((920..939).toSet()),
    TELE2((950..959).toSet() + (970..979).toSet()),
    ;

    fun getNumber(): Int = numbers.random()
  }

  fun generateWithoutFormatting(): String {
    return generatePayloadNumbers()
      .let { "$RU_CODE$it" }
  }

  fun generateWithFormatting(): String {
    return generatePayloadNumbers()
      .let { "+$RU_CODE (${it.substring(0, 3)}) ${it.substring(3, 6)}-${it.substring(6, 8)}-${it.substring(8, 10)}" }
  }

  private fun generatePayloadNumbers(): String {
    val prefix = Operators.entries.random().getNumber().toString()
    val number = List(7) { Random.nextInt(0, 10) }.joinToString("")

    return prefix + number
  }
}

