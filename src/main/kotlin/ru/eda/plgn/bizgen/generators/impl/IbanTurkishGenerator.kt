package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.generators.impl.IbanTurkishGenerator.IbanGenerator.randomTurkishIBAN
import java.math.BigInteger

/**
 * Турецкий IBAN (26 символов).
 *
 * **Формат:** *TRkk BBBB 0 RRRRRRRRRRRRRRRR*
 * - *TR - код страны*
 * - *kk - контрольные цифры (2)*
 * - *BBBB -код банка (5 цифр, дополненный нулями)*
 * - *0: резервный ноль (1)*
 * - *R...R - номер счета (16 цифр)*
 *
 * @author Dmitry_Emelyanenko
 */
class IbanTurkishGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomTurkishIBAN())

  /**
   * Турецкий IBAN (26 символов).
   *
   * **Формат:** *TRkk BBBB 0 RRRRRRRRRRRRRRRR*
   * - *TR - код страны*
   * - *kk - контрольные цифры (2)*
   * - *BBBB -код банка (5 цифр, дополненный нулями)*
   * - *0: резервный ноль (1)*
   * - *R...R - номер счета (16 цифр)*
   *
   * @author Dmitry_Emelyanenko
   */
  private object IbanGenerator {
    // Код страны для Турции
    private const val COUNTRY_CODE = "TR"

    // Список банков Турции с их кодами (первые 5 цифр BBAN)
    private val bankCodes = listOf(
      "00001",  // Центральный Банк Турции
      "00010",  // Ziraat Bankası
      "00012",  // Halk Bankası
      "00015",  // VakıfBank
      "00032",  // İş Bankası
      "00046",  // Akbank
      "00059",  // Garanti BBVA
      "00062",  // Yapı Kredi
      "00064",  // QNB Finansbank
      "00067"   // Albaraka Türk
    )

    /** Генерирует турецкий IBAN (26 символов) */
    fun randomTurkishIBAN() = randomTurkishIBANInner(bankCode = null)

    @Suppress("SameParameterValue")
    private fun randomTurkishIBANInner(bankCode: String? = null): String {
      // 1. Выбираем или проверяем код банка
      val selectedBankCode = (bankCode ?: bankCodes.random())
        .padStart(5, '0')
        .take(5)

      require(selectedBankCode.all { it.isDigit() }) { "Код банка должен содержать только цифры" }

      // 2. Генерируем номер счета (16 цифр)
      val accountNumber = (1..16).joinToString("") { (0..9).random().toString() }

      // 3. Формируем BBAN (5 + 1 + 16 = 22 цифры)
      val bban = selectedBankCode + "0" + accountNumber

      // 4. Вычисляем контрольные цифры (2 цифры)
      val controlNumber = calculateControlNumber(bban)

      // 5. Собираем IBAN (26 символов)
      return (COUNTRY_CODE + controlNumber + bban).also {
        require(it.length == 26) { "Turkish IBAN must have 26 characters. Current length(${it.length}): $it" }
      }
    }

    private fun calculateControlNumber(bban: String): String {
      // 1. Создаем временную строку: BBAN + TR00
      val temp = bban + COUNTRY_CODE + "00"

      // 2. Заменяем буквы на числа (A=10, B=11, ..., Z=35)
      val numeric = temp.map { char ->
        if (char.isLetter()) (char.uppercaseChar() - 'A' + 10).toString()
        else char.toString()
      }.joinToString("")

      // 3. Вычисляем mod 97
      val mod97 = BigInteger(numeric).mod(BigInteger("97")).toInt()

      // 4. Контрольное число = 98 - mod97
      return "%02d".format(98 - mod97)
    }
  }
}
