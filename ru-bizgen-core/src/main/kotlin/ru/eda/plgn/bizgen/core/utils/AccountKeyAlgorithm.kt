package ru.eda.plgn.bizgen.core.utils

/**
 * Алгоритм расчёта контрольного ключа лицевого счёта по Положению ЦБ РФ № 515 от 08.09.1997.
 *
 * Контрольный ключ рассчитывается с использованием весовых коэффициентов,
 * устанавливаемых каждому разряду из совокупности реквизитов:
 * - условный номер РКЦ (если счёт открыт в РКЦ) или кредитной организации (если в КО)
 * - номер лицевого счёта
 *
 * **See Also:** [Положение ЦБ РФ № 515](https://www.consultant.ru/document/cons_doc_LAW_16053/)
 *
 * @author Dmitry_Emelyanenko
 */
object AccountKeyAlgorithm {

  private val WEIGHTS = intArrayOf(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1)

  /**
   * Рассчитывает контрольный ключ для лицевого счёта по Положению 515.
   *
   * @param bik банковский идентификационный код (9 цифр)
   * @param accountNumber номер лицевого счёта (20 цифр, контрольный ключ игнорируется — приравнивается 0)
   * @param isRkc `true` для счетов в РКЦ (корреспондентские счета), `false` для счетов в кредитной организации
   * @return контрольный ключ (0–9)
   */
  fun calculateControlKey(bik: String, accountNumber: String, isRkc: Boolean): Int {
    require(bik.length == 9 && bik.all { it.isDigit() }) { "БИК должен содержать 9 цифр" }
    require(accountNumber.length == 20 && accountNumber.all { it.isDigit() }) {
      "Номер счёта должен содержать 20 цифр"
    }

    val conditionalCode = if (isRkc) {
      "0" + bik.substring(4, 6)
    } else {
      bik.substring(6, 9)
    }

    val accountWithK0 = accountNumber.substring(0, 8) + "0" + accountNumber.substring(9)

    val base = conditionalCode + accountWithK0

    var sum = 0
    base.forEachIndexed { index, char ->
      sum += char.digitToInt() * WEIGHTS[index]
    }

    return (sum % 10) * 3 % 10
  }
}
