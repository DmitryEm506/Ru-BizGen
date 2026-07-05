package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator.impl.PassportRuGeneratorInner.generatePassportNumber
import ru.eda.plgn.bizgen.core.generator.impl.PassportRuGeneratorInner.generatePassportSeries
import kotlin.random.Random

/**
 * Генератор номера паспорта РФ с пробелом.
 *
 * Формат: «XXXX XXXXXX», где XXXX — серия (код региона ОКАТО + год бланка),
 * XXXXXX — номер паспорта.
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuSpacedGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> =
    GeneratorResultWithEscape(data = "${generatePassportSeries()} ${generatePassportNumber()}")
}

/**
 * Генератор номера паспорта РФ без пробела.
 *
 * Формат: «XXXXXXXXXX», где первые 4 цифры — серия (код региона ОКАТО + год бланка),
 * остальные 6 — номер паспорта.
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuCompactGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> =
    GeneratorResultWithEscape(data = "${generatePassportSeries()}${generatePassportNumber()}")
}

private object PassportRuGeneratorInner {

  private val okatoRegionCodes = listOf(
    "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
    "21",
    "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
    "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
    "42", "43", "44", "45", "46", "47", "48", "49", "50", "51",
    "52", "53", "54", "55", "56", "57", "58", "59", "60", "61",
    "62", "63", "64", "65", "66", "67", "68", "69", "70", "71",
    "72", "73", "74", "75", "76", "77", "78", "79", "81", "82",
    "83", "84", "86", "89", "91", "92", "99"
  )

  fun generatePassportSeries(): String {
    val regionCode = okatoRegionCodes.random()
    val yearCode = "%02d".format(Random.nextInt(14, 27))
    return "$regionCode$yearCode"
  }

  fun generatePassportNumber(): String = "%06d".format(Random.nextInt(0, 1_000_000))
}
