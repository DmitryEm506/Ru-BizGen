package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import kotlin.random.Random

/**
 * КПП (Код причины постановки на учет).
 *
 * **See Also:**
 * [Код причины постановки на учёт](https://ru.wikipedia.org/wiki/%D0%98%D0%B4%D0%B5%D0%BD%D1%82%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%89%D0%B8%D0%BA%D0%B0#%D0%9A%D0%BE%D0%B4_%D0%BF%D1%80%D0%B8%D1%87%D0%B8%D0%BD%D1%8B_%D0%BF%D0%BE%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B8_%D0%BD%D0%B0_%D1%83%D1%87%D1%91%D1%82)
 *
 * @author Dmitry_Emelyanenko
 */
class KppGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(randomKpp())

  private companion object {

    fun randomKpp(): String {
      return generateKpp(null)
    }

    /**
     * Генерирует случайный КПП (9 цифр) по правилам ФНС. Формат: XXXXYYZZZ, где:
     * - XXXX (4 цифры) - код налогового органа
     * - YY (2 цифры) - причина постановки (01-50)
     * - ZZZ (3 цифры) - порядковый номер
     */
    private fun generateKpp(regionCode: Int? = null, taxOfficeCode: Int? = null): String {
      // Генерация кода региона и налоговой (первые 4 цифры)
      val taxCode = if (taxOfficeCode != null) {
        "%04d".format(taxOfficeCode)
      } else {
        val rc = regionCode ?: Random.nextInt(1, 100)
        "%02d".format(rc) + "%02d".format(Random.nextInt(1, 100))
      }

      // Причина постановки (01-50)
      val reasonCode = "%02d".format(Random.nextInt(1, 51))

      // Порядковый номер (000-999)
      val serialNumber = "%03d".format(Random.nextInt(0, 1000))

      return taxCode + reasonCode + serialNumber
    }
  }
}