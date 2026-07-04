package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator.impl.BikGenerator.Companion.randomBik
import ru.eda.plgn.bizgen.core.utils.AccountKeyAlgorithm
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
class BankAccountGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomCorrespondentAccount(randomBik()))

  /** Логика формирования корреспондентского счета. */
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

      // 5. Собираем счёт с K=0 (20 цифр)
      val accountWithK0 = "$prefix${0}$bikPart$fixedZeros$randomSuffix"

      // 6. Рассчитываем контрольный ключ по Положению 515 (23-значная база: РКЦ + счёт)
      val controlDigit = AccountKeyAlgorithm.calculateControlKey(bik, accountWithK0, isRkc = true)

      // 7. Сборка счета (8 + 1 + 5 + 3 + 3 = 20 цифр)
      return "$prefix$controlDigit$bikPart$fixedZeros$randomSuffix".also {
        require(it.length == 20) { "Должно быть 20 цифр, получено: ${it.length}" }
      }
    }

  }
}