package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuGeneratorInner.generateForeignPassportNumber
import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuGeneratorInner.generateMrz
import kotlin.random.Random

/**
 * Генератор номера загранпаспорта РФ.
 *
 * Формат: 9 цифр, первые 2 цифры — 70 или 71 (серия биометрического загранпаспорта РФ),
 * остальные 7 — случайные цифры.
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuNumberGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> =
    GeneratorResultWithEscape(data = generateForeignPassportNumber())
}

/**
 * Генератор MRZ-строки загранпаспорта РФ (ICAO 9303, формат TD-3).
 *
 * Возвращает строку длиной 88 символов (две строки по 44 символа, склеенные без разделителя).
 *
 * **Часть 1 (символы 0–43):** `P<RUS` + ФАМИЛИЯ + `<<` + ИМЯ + заполнение `<` до 44 символов.
 *
 * **Часть 2 (символы 44–87):** номер паспорта (9) + контрольная цифра (1) + `RUS` (3) +
 * дата рождения YYMMDD (6) + контрольная цифра (1) + пол (1) +
 * срок действия YYMMDD (6) + контрольная цифра (1) +
 * личный номер (14) + контрольная цифра (1) + составная контрольная цифра (1).
 *
 * Контрольные цифры рассчитываются по стандарту ICAO 9303:
 * веса циклически повторяются 7, 3, 1; значения символов: 0–9 → 0–9, A–Z → 10–35, `<` → 0.
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuMrzGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> =
    GeneratorResultWithEscape(data = generateMrz())
}

private object ForeignPassportRuGeneratorInner {

  private val surnames = listOf(
    "IVANOV", "PETROV", "SIDOROV", "SMIRNOV", "KUZNETSOV",
    "POPOV", "VASILYEV", "PAVLOV", "SEMENOV", "GOLUBEV",
    "BOGDANOV", "VOROBYEV", "FEDOROV", "MIKHAILOV", "BELYAEV",
    "TARASOV", "BELOV", "KOMAROV", "ORLOV", "KISELEV",
    "MAKAROV", "ANDREEV", "KOVALEV", "ILIN", "GUSEV"
  )

  private val givenNames = listOf(
    "ALEKSANDR", "DMITRIY", "MAKSIM", "SERGEY", "ANDREY",
    "ALEKSEY", "IVAN", "MIKHAIL", "ARTEM", "ILYA",
    "ELENA", "OLGA", "NATALYA", "ANNA", "MARIYA",
    "IRINA", "EKATERINA", "SVETLANA", "TATYANA", "YULIYA"
  )

  fun generateForeignPassportNumber(): String {
    val prefix = listOf("70", "71").random()
    val rest = "%07d".format(Random.nextInt(0, 10_000_000))
    return "$prefix$rest"
  }

  fun generateMrz(): String {
    val passportNumber = generateForeignPassportNumber()
    val surname = surnames.random()
    val givenName = givenNames.random()

    val line1 = buildLine1(surname, givenName)
    val line2 = buildLine2(passportNumber)

    return "$line1$line2"
  }

  private fun buildLine1(surname: String, givenName: String): String =
    "P<RUS$surname<<$givenName".padEnd(44, '<')

  private fun buildLine2(passportNumber: String): String {
    val passportCheckDigit = mrzCheckDigit(passportNumber)
    val nationality = "RUS"
    val dob = generateRandomDob()
    val dobCheckDigit = mrzCheckDigit(dob)
    val sex = if (Random.nextBoolean()) "M" else "F"
    val expiry = generateRandomExpiry()
    val expiryCheckDigit = mrzCheckDigit(expiry)
    val personalNumber = generatePersonalNumber()
    val personalNumberCheckDigit = mrzCheckDigit(personalNumber)

    val lineWithoutComposite = passportNumber + passportCheckDigit + nationality +
      dob + dobCheckDigit + sex + expiry + expiryCheckDigit +
      personalNumber + personalNumberCheckDigit

    val compositeInput = lineWithoutComposite.substring(0, 10) +
      lineWithoutComposite.substring(13, 20) +
      lineWithoutComposite.substring(21, 28) +
      lineWithoutComposite.substring(28, 43)

    val compositeCheckDigit = mrzCheckDigit(compositeInput)

    return lineWithoutComposite + compositeCheckDigit
  }

  private fun generateRandomDob(): String {
    val year = if (Random.nextBoolean()) Random.nextInt(60, 100) else Random.nextInt(0, 6)
    val month = Random.nextInt(1, 13)
    val day = Random.nextInt(1, 29)
    return "%02d%02d%02d".format(year, month, day)
  }

  private fun generateRandomExpiry(): String {
    val year = Random.nextInt(26, 36)
    val month = Random.nextInt(1, 13)
    val day = Random.nextInt(1, 29)
    return "%02d%02d%02d".format(year, month, day)
  }

  private fun generatePersonalNumber(): String =
    List(14) { Random.nextInt(0, 10) }.joinToString("")

  private val weights = intArrayOf(7, 3, 1)

  private fun mrzCheckDigit(input: String): Char {
    val sum = input.foldIndexed(0) { index, acc, char ->
      val value = when {
        char.isDigit() -> char - '0'
        char in 'A'..'Z' -> char - 'A' + 10
        else -> 0
      }
      acc + value * weights[index % 3]
    }
    return ('0'.code + (sum % 10)).toChar()
  }
}
