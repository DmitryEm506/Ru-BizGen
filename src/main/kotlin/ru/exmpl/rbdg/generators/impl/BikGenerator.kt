package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import kotlin.random.Random

/**
 * БИК Генератор.
 *
 * @author Dmitry_Emelyanenko
 */
class BikGenerator : Generator<String> {

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomBik())

  companion object {

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