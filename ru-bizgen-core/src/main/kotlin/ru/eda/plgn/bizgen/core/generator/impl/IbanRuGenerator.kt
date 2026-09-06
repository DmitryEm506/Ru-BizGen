package ru.eda.plgn.bizgen.core.generator.impl

import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator.impl.IbanRuGenerator.IbanGenerator.generateRussianIBAN
import java.math.BigInteger

/**
 * IBAN (International Bank Account Number) - международный номер банковского счёта, используемый для международных переводов. Формат
 * регулируется стандартом ISO 13616.
 *
 * **Формат российского IBAN (33 символа):** *RU КК XXXX XXXX XXXX XXXX XXXX XXXX X*
 * - *2 буквы — код страны (RU)*
 * - *2 цифры — контрольное число (рассчитывается)*
 * - *29 цифр — BBAN (базовый номер счёта)*
 *
 * **Структура BBAN (29 цифр):** *BBBBB SSSS NNNNNNNNNNNNNNNNNNNN*
 * - *BBBBB — код банка из БИК (5 цифр)*
 * - *SSSS — код филиала (4 цифры, часто 0000)*
 * - *NN...N — номер счёта (20 цифр)*
 *
 * **See Also:** [IBAN](https://ru.wikipedia.org/wiki/IBAN)
 *
 * @author Dmitry_Emelyanenko
 */
class IbanRuGenerator : GeneratorStr {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> {
    val bik = BikGenerator.randomBik()
    val accountNumber = BankAccountGenerator.randomCorrespondentAccount(bik)
    return GeneratorResultWithEscape(data = generateRussianIBAN(accountNumber, bik))
  }

  /**
   * IBAN (International Bank Account Number) - международный номер банковского счёта, используемый для международных переводов. Формат
   * регулируется стандартом ISO 13616.
   *
   * **See Also:** [IBAN](https://ru.wikipedia.org/wiki/IBAN)
   *
   * @author Dmitry_Emelyanenko
   */
  internal object IbanGenerator {

    /**
     * Генерирует российский IBAN.
     *
     * @param accountNumber Номер счёта (20 цифр).
     * @param bik БИК банка (9 цифр). Если не указан, генерируется случайный.
     * @return Строка IBAN (формат RUXXXXXXXXXXXXXXXXXXXXXXXXX).
     */
    fun generateRussianIBAN(
      accountNumber: String,
      bik: String? = null,
    ): String {
      // 1. Код банка — последние 5 цифр БИК
      val selectedBik = bik ?: BikGenerator.randomBik()
      require(selectedBik.length == 9 && selectedBik.all { it.isDigit() }) {
        "БИК должен содержать 9 цифр"
      }
      val bankCode = selectedBik.substring(4, 9)

      // 2. Формируем BBAN (5 + 4 + 20 = 29 цифр)
      val bban = bankCode + "0000" + accountNumber

      require(bban.length == 29) { "BBAN must be 29 but was ${bban.length}" }

      // 3. Вычисляем контрольное число для IBAN
      val controlNumber = calculateIBANControlNumber("RU", bban)

      // 4. Собираем итоговый IBAN (33 символа)
      return "RU$controlNumber$bban".also {
        require(it.length == 33) { "IBAN must be 33 but was ${it.length}" }
      }
    }

    /** Вычисляет контрольное число IBAN (алгоритм mod-97). */
    @Suppress("SameParameterValue")
    private fun calculateIBANControlNumber(countryCode: String, bban: String): String {
      val tempIban = "$countryCode${"00"}$bban" // RU + 00 + BBAN
      val moved = tempIban.substring(4) + tempIban.substring(0, 4) // Перемещаем первые 4 символа в конец
      val numericIban = moved.map { char ->
        if (char.isLetter()) (char.uppercaseChar() - 'A' + 10).toString()
        else char.toString()
      }.joinToString("")
      val mod97 = BigInteger(numericIban).mod(BigInteger("97")).toInt()
      return "%02d".format(98 - mod97)
    }
  }
}