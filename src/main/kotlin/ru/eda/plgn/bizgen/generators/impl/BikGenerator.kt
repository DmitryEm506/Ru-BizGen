package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import kotlin.random.Random

/**
 * БИК Генератор.
 *
 * *Генерирует случайный БИК (Банковский идентификационный код).*
 *
 * *Формат БИК: 04XXXXXXX*
 * - *04 — код России (для рублёвых счетов)*
 * - *XXXXXXX — 7 цифр (регион + номер банка)*
 *
 * **See Also:**
 * [Банковский идентификационный код](https://ru.wikipedia.org/wiki/%D0%91%D0%B0%D0%BD%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B8%D0%B9_%D0%B8%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BA%D0%BE%D0%B4)
 *
 * @author Dmitry_Emelyanenko
 */
class BikGenerator : Generator<String> {

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomBik())

  companion object {

    /**
     * Генерирует случайный БИК (Банковский идентификационный код).
     *
     * Формат БИК: 04XXXXXXX.
     * - 04 — код России (для рублёвых счетов)
     * - XXXXXXX — 7 цифр (регион + номер банка).
     */
    fun randomBik() = randomBik(61)

    /**
     * Генерирует случайный БИК (Банковский идентификационный код).
     *
     * Формат БИК: 04XXXXXXX
     * - 04 — код России (для рублёвых счетов)
     * - XXXXXXX — 7 цифр (регион + номер банка).
     *
     * @param regionCode код региона (1-99), если не указан — выбирается случайный.
     */
    @Suppress("SameParameterValue")
    private fun randomBik(regionCode: Int? = null): String {
      val actualRegionCode = regionCode ?: Random.nextInt(1, 100)
      require(actualRegionCode in 1..99) { "Код региона должен быть в диапазоне 1-99" }

      // Генерируем 5 случайных цифр для номера банка
      val bankNumber = Random.nextInt(0, 100000).toString().padStart(5, '0')

      return "04${actualRegionCode.toString().padStart(2, '0')}$bankNumber"
    }
  }
}