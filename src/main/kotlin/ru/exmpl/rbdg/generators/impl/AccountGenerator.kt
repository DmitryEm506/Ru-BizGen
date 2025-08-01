package ru.exmpl.rbdg.generators.impl

import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.generators.GeneratorResultWithEscape
import ru.exmpl.rbdg.generators.impl.AccountGenerator.randomAccount
import ru.exmpl.rbdg.generators.impl.BikGenerator.Companion.randomBik

/**
 * Расчетный счет (Р/с) в рублях.
 * - *Назначение:	Для операций клиентов (юрлиц, ИП, физлиц)*
 * - *Владелец:	Компания, ИП или физлицо*
 * - *Где используется: В реквизитах для платежей между организациями*
 * - *Формат:	20 цифр*
 * - *Пример: 40817810000000000001*
 *
 * @author Dmitry_Emelyanenko
 */
class AccountRubGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomAccount("RUB", randomBik()))
}

/**
 * Расчетный счет (Р/с) в китайских юанях.
 * - *Назначение:	Для операций клиентов (юрлиц, ИП, физлиц)*
 * - *Владелец:	Компания, ИП или физлицо*
 * - *Где используется: В реквизитах для платежей между организациями*
 * - *Формат:	20 цифр*
 * - *Пример: 40817810000000000001*
 *
 * @author Dmitry_Emelyanenko
 */
class AccountCnyGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomAccount("CNY", randomBik()))
}


/**
 * Расчетный счет (Р/с).
 * - *Назначение:	Для операций клиентов (юрлиц, ИП, физлиц)*
 * - *Владелец:	Компания, ИП или физлицо*
 * - *Где используется: В реквизитах для платежей между организациями*
 * - *Формат:	20 цифр*
 * - *Пример: 40817810000000000001*
 *
 * @author Dmitry_Emelyanenko
 */
private object AccountGenerator {

  // Справочник валютных кодов (ISO 4217 -> код ЦБ РФ)
  val currencyCodes = linkedMapOf(
    "RUB" to "810",  // Российский рубль
    "CNY" to "156",  // Китайский юань
    "USD" to "840",  // Доллар США
    "EUR" to "978",  // Евро
    "GBP" to "826",  // Фунт стерлингов
    "JPY" to "392",  // Японская иена
    "CHF" to "756",  // Швейцарский франк
    "AUD" to "036",  // Австралийский доллар
    "CAD" to "124",  // Канадский доллар
    "HKD" to "344",  // Гонконгский доллар
    "SGD" to "702",  // Сингапурский доллар
    "TRY" to "949",  // Турецкая лира
    "AED" to "784",  // Дирхам ОАЭ
    "KZT" to "398",  // Казахстанский тенге
    "BYN" to "933",  // Белорусский рубль
    "UZS" to "860"   // Узбекский сум
  )

  /**
   * Генерирует расчетный счет для юрлица.
   *
   * @param currency Валюта (например, "RUB", "CNY", "USD")
   * @param bankBIC БИК банка (9 цифр, по умолчанию - 044525225)
   */
  fun randomAccount(
    currency: String? = "RUB",
    bankBIC: String = "044525225"
  ): String {
    val currency = currency ?: currencyCodes.keys.random()

    // Проверяем валюту
    val currencyCode = currencyCodes[currency]
      ?: throw IllegalArgumentException("Неверная валюта: $currency. Доступные: ${currencyCodes.keys}")

    // Проверяем БИК
    require(bankBIC.length == 9 && bankBIC.all { it.isDigit() }) {
      "БИК должен содержать 9 цифр"
    }

    // Формируем базовую часть
    val basePart = buildString {
      append("407")       // Балансовый счет (юрлицо)
      append("01")        // Признак счета
      append(currencyCode) // Код валюты
      append("0")         // Контрольный разряд
    }

    // Генерируем случайный номер счета (11 цифр)
    val accountNumber = (1..11).joinToString("") { (0..9).random().toString() }

    // Собираем полный номер (20 цифр)
    return (basePart + accountNumber).also {
      require(it.length == 20) { "AccountNumber length must be 20. Current value: ${it.length}" }
    }
  }
}