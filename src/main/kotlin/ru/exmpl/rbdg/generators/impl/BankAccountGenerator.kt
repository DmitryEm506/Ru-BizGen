package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import ru.exmpl.rbdg.generators.impl.BikGenerator.Companion.randomBik
import kotlin.random.Random

/**
 * Корреспондентский счёт Генератор.
 *
 * *Генерирует корреспондентский счет банка на основе БИК.*
 *
 * *Формат: 30101810KXXXXX000NNN (20 цифр)*
 * - *30101810 — префикс коррсчёта*
 * - *K - контрольное число*
 * - *XXXXX — последние 5 цифр БИК (кроме контрольных)*
 * - *000 - фиксированные нули*
 * - *NNN — условный номер (случайные цифры)*
 *
 * **See Also:**
 * [Корреспондентский счёт](https://ru.wikipedia.org/wiki/%D0%9A%D0%BE%D1%80%D1%80%D0%B5%D1%81%D0%BF%D0%BE%D0%BD%D0%B4%D0%B5%D0%BD%D1%82%D1%81%D0%BA%D0%B8%D0%B9_%D1%81%D1%87%D1%91%D1%82)
 *
 * @author Dmitry_Emelyanenko
 */
class BankAccountGenerator : Generator<String> {

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomCorrespondentAccount(randomBik()))

  companion object {

    /**
     * Генерирует корреспондентский счет банка на основе БИК.
     *
     * Формат: 30101810KXXXXX000NNN (20 цифр)
     * - 30101810 — префикс коррсчёта
     * - K - контрольное число
     * - XXXXX — последние 5 цифр БИК (кроме контрольных)
     * - 000 - фиксированные нули
     * - NNN — условный номер (случайные цифры)
     *
     * @param bik банковский идентификационный код (9 цифр)
     * @throws IllegalArgumentException если БИК некорректен
     */
    fun randomCorrespondentAccount(bik: String): String {
      require(bik.length == 9 && bik.all { it.isDigit() }) {
        "БИК должен содержать ровно 9 цифр"
      }

      // 1. Префикс (8 цифр)
      val prefix = "30101810"
      // 2. 5 цифр из БИК (позиции 5-9)
      val bikPart = bik.substring(4, 9)
      // 3. Фиксированные нули (3 цифры)
      val fixedZeros = "000"
      // 4. 3 случайные цифры
      val randomSuffix = "%03d".format(Random.nextInt(1000))

      // 5. Расчет контрольной цифры (на основе "0" + bikPart + fixedZeros + randomSuffix)
      val baseForControl = "0$bikPart$fixedZeros$randomSuffix"
      val controlDigit = calculateControlDigit(baseForControl)

      // 6. Сборка счета (8 + 1 + 5 + 3 + 3 = 20 цифр)
      return "$prefix$controlDigit$bikPart$fixedZeros$randomSuffix".also {
        require(it.length == 20) { "Должно быть 20 цифр, получено: ${it.length}" }
      }
    }

    /** Алгоритм расчета контрольной цифры для коррсчета. */
    private fun calculateControlDigit(accountNumber: String): Int {
      val weights = intArrayOf(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1)
      var sum = 0

      accountNumber.forEachIndexed { index, char ->
        sum += char.digitToInt() * weights[index]
      }

      return (sum % 10) * 3 % 10
    }
  }
}