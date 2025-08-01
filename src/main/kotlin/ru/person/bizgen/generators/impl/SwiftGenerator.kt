package ru.person.bizgen.generators.impl

import ru.person.bizgen.generators.Generator
import ru.person.bizgen.generators.GeneratorResult
import ru.person.bizgen.generators.GeneratorResultWithEscape
import ru.person.bizgen.generators.impl.SwiftGenerator.randomSwiftCode11
import ru.person.bizgen.generators.impl.SwiftGenerator.randomSwiftCode8

/**
 * SWIFT-код (BIC, Bank Identifier Code) — это международный идентификатор банка, используемый для международных платежей.
 *
 * Формат кода регулируется стандартом ISO 9362.
 *
 * SWIFT-код из 8 символов (буквы + цифры).
 *
 * Формат: AAAABBCCDDD.
 * - *AAAA - Банковский код 4 симв.	Код банка или организации (только буквы)
 * - *BB - Код страны 2 симв.	Код страны (ISO 3166, только буквы)
 * - *CC - Код местоположения 2 симв.	Код города/региона (буквы/цифры)
 * - *DDD - Код филиала 3 симв. (опционально)	Код отделения (XXX = головной офис)
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8Generator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomSwiftCode8())
}

/**
 * SWIFT-код (BIC, Bank Identifier Code) — это международный идентификатор банка, используемый для международных платежей.
 *
 * Формат кода регулируется стандартом ISO 9362.
 *
 * SWIFT-код из 8 символов (буквы + цифры).
 *
 * Формат: AAAABBCCDDD.
 * - *AAAA - Банковский код 4 симв.	Код банка или организации (только буквы)
 * - *BB - Код страны 2 симв.	Код страны (ISO 3166, только буквы)
 * - *CC - Код местоположения 2 симв.	Код города/региона (буквы/цифры)
 * - *DDD - Код филиала 3 симв. (опционально)	Код отделения (XXX = головной офис)
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11Generator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomSwiftCode11())
}

/**
 * SWIFT-код (BIC, Bank Identifier Code) — это международный идентификатор банка, используемый для международных платежей.
 *
 * Формат кода регулируется стандартом ISO 9362.
 *
 * SWIFT-код может быть 8 или 11 символов (буквы + цифры).
 *
 * Формат: AAAABBCCDDD.
 * - *AAAA - Банковский код 4 симв.	Код банка или организации (только буквы)
 * - *BB - Код страны 2 симв.	Код страны (ISO 3166, только буквы)
 * - *CC - Код местоположения 2 симв.	Код города/региона (буквы/цифры)
 * - *DDD - Код филиала 3 симв. (опционально)	Код отделения (XXX = головной офис)
 *
 * @author Dmitry_Emelyanenko
 */
private object SwiftGenerator {

  fun randomSwiftCode8(): String = randomSwiftCode(false)

  fun randomSwiftCode11(): String = randomSwiftCode(true)

  private fun randomSwiftCode(withBranch: Boolean = false): String {
    // 1. Банковский код (4 буквы)
    val bankCode = (1..4).map { ('A'..'Z').random() }.joinToString("")

    // 2. Код страны (RU, US, DE и т. д.)
    val countryCode = "RU" // Можно сделать случайный выбор из списка

    // 3. Код местоположения (2 символа: буква + цифра/буква)
    val locationCode = "${('A'..'Z').random()}${('0'..'9').random()}"

    // 4. Код филиала (опционально)
    val branchCode = if (withBranch) "XXX" else ""

    return "$bankCode$countryCode$locationCode$branchCode"
  }
}