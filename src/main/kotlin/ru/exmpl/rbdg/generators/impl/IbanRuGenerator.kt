package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import ru.exmpl.rbdg.generators.impl.IbanRuGenerator.IbanGenerator.generateRussianIBAN
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
 * **Структура BBAN (29 цифр):** *KK BBBBB SSSS C NNNNNNNNNNNNNNNNNNNN*
 * - *KK — контрольные цифры (2 цифры)*
 * - *BBBBB — БИК банка (5 цифр)*
 * - *SSSS — код филиала (4 цифры, часто 0000)*
 * - *C — признак счёта (1 цифра, обычно 0)*
 * - *NN...N — номер счёта (20 цифр)*
 *
 * **See Also:** [IBAN](https://ru.wikipedia.org/wiki/IBAN)
 *
 * @author Dmitry_Emelyanenko
 */
class IbanRuGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(
    data = generateRussianIBAN(
      accountNumber = BankAccountGenerator.randomCorrespondentAccount(
        bik = BikGenerator.randomBik()
      )
    )
  )

  /**
   * IBAN (International Bank Account Number) - международный номер банковского счёта, используемый для международных переводов. Формат
   * регулируется стандартом ISO 13616.
   *
   * **See Also:** [IBAN](https://ru.wikipedia.org/wiki/IBAN)
   *
   * @author Dmitry_Emelyanenko
   */
  private object IbanGenerator {
    // База данных российских банков (БИК или аналогичные коды)
    private val bankCodes = mapOf(
      "Газпромбанк" to "044525823",
      "Сбербанк" to "044525225",
      "ВТБ" to "044525187",
      "Альфа-Банк" to "044525593",
      "Тинькофф" to "044525444",
      "Райффайзенбанк" to "044525202",
      "Открытие" to "044525111",
      "Промсвязьбанк" to "044525060",
      "Россельхозбанк" to "044525402",
      "Совкомбанк" to "044525417"
    )

    /**
     * Генерирует российский IBAN.
     *
     * @param accountNumber Номер счёта (если не указан, генерируется случайный).
     * @param bankCode Код банка (если не указан, выбирается случайный из базы).
     * @return Строка IBAN (формат RUXXXXXXXXXXXXXXXXXXXXXXXXX).
     */
    fun generateRussianIBAN(
      accountNumber: String,
      bankCode: String? = null
    ): String {
      // 1. Выбираем код банка (5 цифр)
      val selectedBankCode = (bankCode ?: bankCodes.values.random()).take(5)

      // 2. Формируем BBAN (29 цифр)
      val bban = buildString {
        append("00")                    // Временные контрольные цифры
        append(selectedBankCode)        // Код банка (5 цифр)
        append("0000")                  // Код филиала
        append("0")                     // Признак счета
        append(accountNumber)           // Номер счета (20 цифр)
      }.take(29)

      // 3. Вычисляем контрольное число для IBAN
      val controlNumber = calculateIBANControlNumber("RU", bban)

      // 4. Собираем итоговый IBAN (33 символа)
      return "RU$controlNumber$bban".also {
        require(it.length == 33) { "IBAN must be 33 but was ${it.length}" }
      }
    }

    /** Вычисляет контрольное число IBAN (алгоритм mod-97) */
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