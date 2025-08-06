package ru.person.bizgen.generators.impl

import ru.person.bizgen.generators.Generator
import ru.person.bizgen.generators.GeneratorResult
import ru.person.bizgen.generators.GeneratorResultWithEscape
import ru.person.bizgen.generators.impl.SwiftRuGeneratorInner.generate11
import ru.person.bizgen.generators.impl.SwiftRuGeneratorInner.generate8

/**
 * **SWIFT-код российских банков.**
 *
 * *SWIFT-код (BIC, Bank Identifier Code) — это международный идентификатор банка, используемый для международных платежей.*
 *
 * Формат кода регулируется стандартом ISO 9362.
 *
 * SWIFT-код из 8 символов (буквы + цифры).
 *
 * Формат: AAAABBCC.
 * - *AAAA - Банковский код 4 симв.	Код банка или организации (только буквы)*
 * - *BB - Код страны 2 симв.	Код страны (ISO 3166, только буквы)*
 * - *CC - Код местоположения 2 симв.	Код города/региона (буквы/цифры)*
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8RuGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generate8())
}

/**
 * **SWIFT-код российских банков.**
 *
 * *SWIFT-код (BIC, Bank Identifier Code) — это международный идентификатор банка, используемый для международных платежей.*
 *
 * Формат кода регулируется стандартом ISO 9362.
 *
 * SWIFT-код из 111 символов (буквы + цифры).
 *
 * Формат: AAAABBCC.
 * - *AAAA - Банковский код 4 симв.	Код банка или организации (только буквы)*
 * - *BB - Код страны 2 симв.	Код страны (ISO 3166, только буквы)*
 * - *CC - Код местоположения 2 симв.	Код города/региона (буквы/цифры)*
 * - *DDD - Код филиала 3 симв. (опционально)	Код отделения (XXX = головной офис)
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11RuGenerator : Generator<String> {
  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generate11())
}

private object SwiftRuGeneratorInner {
  fun generate8() = russianBankSwiftCodes.random()

  fun generate11() = russianBankSwiftCodes.random() + (100..999).random().toString()


  private val russianBankSwiftCodes = setOf(
    // Топ-20 федеральных банков
    "GAZPRUMM", // 1. Газпромбанк
    "SABRRUMM", // 2. Сбербанк
    "VTBRRUMM", // 3. ВТБ
    "RUAGRUMM", // 4. Россельхозбанк
    "ALFARUMM", // 5. Альфа-Банк
    "HYPTRUMM", // 6. Открытие
    "RZBKRUMM", // 7. Райффайзенбанк
    "SOVKRUMM", // 8. Совкомбанк
    "PSBKRUMM", // 9. Промсвязьбанк
    "TICSRUMM", // 10. Тинькофф
    "RSBNRUMM", // 11. Росбанк
    "CBGURUMM", // 12. ВБРР (Возрождение)
    "JSNLRUMM", // 13. Банк Санкт-Петербург
    "KREDRUMM", // 14. Кредит Европа Банк
    "MTUZRUMM", // 15. МТС-Банк
    "RNCBRUMM", // 16. Ренессанс Кредит
    "RUSSRUMM", // 17. Русский Стандарт
    "UNJCRUMM", // 18. ЮниКредит Банк
    "AVBKRUMM", // 19. Авангард
    "ESKRRUMM", // 20. Экспобанк

    // Крупные региональные банки
    "ZOLTRUMM", // 21. Зенит (Татарстан)
    "VLGKRUMM", // 22. Волго-Камский (Пермь)
    "UBLKRUMM", // 23. УБРиР (Екатеринбург)
    "RNBKRUMM", // 24. РНКБ (Крым)
    "PRMSRUMM", // 25. Примсоцбанк (Владивосток)
    "OLMDRUMM", // 26. Восточный (Хабаровск)
    "DOLNRUMM", // 27. Долинск (Сахалин)
    "KHKBRUMM", // 28. Хакасский (Абакан)
    "KURSRUMM", // 29. Курскпромбанк
    "CHELRUMM", // 30. Челябинвестбанк

    // Иностранные банки в РФ
    "CITIRUMM", // 31. Ситибанк
    "DEUTRUMM", // 32. Дойче Банк
    "BNPARUMM", // 33. BNP Paribas
    "INGBRUMM", // 34. ING Bank
    "HSBCRUMM", // 35. HSBC
    "OTPHRUMM", // 36. OTP Bank
    "CRLYRUMM", // 37. Credit Agricole
    "COBKRUMM", // 38. Commerzbank
    "GENORUMM", // 39. Дженерали Банк
    "JPMORUMM", // 40. J.P. Morgan

    // Нишевые и специализированные банки
    "QIWIRUMM", // 41. QIWI Банк
    "POISRUMM", // 42. Пойдём!
    "MIRBRUMM", // 43. Мир Бизнес Банк
    "RFFDRUMM", // 44. Росэксимбанк
    "VTBKRUMM", // 45. ВТБ Капитал
    "SBERRU8X", // 46. Сбер CIB (инвестиционный)
    "GAZPRU8X", // 47. Газпромбанк КИБ
    "ALFARU8X", // 48. Альфа КИБ
    "VTBKRU8X", // 49. ВТБ КИБ
    "RUACRUMM", // 50. РосАгроКредит

    // Дополнительные региональные банки
    "AKMBRUMM", // 51. АК Барс
    "BKRURUMM", // 52. БКР (Башкортостан)
    "TATRRUMM", // 53. Татфондбанк
    "UDMURUMM", // 54. Удмуртский
    "ORGRUMM",  // 55. Оргбанк
    "DVCCRUMM", // 56. Дальневосточный
    "NSVZRUMM", // 57. НС Банк (Новосибирск)
    "SLMKRUMM", // 58. Славия (Краснодар)
    "KLUCRUMM", // 59. Клюква (Калининград)
    "AMTBRUMM", // 60. Амта (Якутия)

    // Банки с особыми лицензиями
    "RUSMRUMM", // 61. Руснарбанк
    "EFGKRUMM", // 62. Эксперт Банк
    "FCOMRUMM", // 63. ФК Открытие
    "VESTRUMM", // 64. Веста (расчётный центр)
    "NSHKRUMM", // 65. Наш Банк
    "ELBKRUMM", // 66. Елизаветинский
    "PLATRUMM", // 67. Платина (эквайринг)
    "METRRUMM", // 68. Металлинвестбанк
    "INVKRUMM", // 69. Инвестторгбанк
    "EURARUMM", // 70. Евроазиатский

    // Малые и цифровые банки
    "MODNRUMM", // 71. Модный Банк
    "TOUCRUMM", // 72. Точка
    "DELORUMM", // 73. Дело Банк
    "SOLNRUMM", // 74. Солнце (Солар)
    "RUBLRUMM", // 75. Рублёв
    "DOBKRUMM", // 76. Добродел
    "ZEMKRUMM", // 77. Земский
    "SKOLRUMM", // 78. Сколково
    "INFKRUMM", // 79. Инфин
    "FIXIRUMM", // 80. Фикси

    // Исторические/переименованные
    "MDMKRUMM", // 81. МДМ Банк
    "PETZRUMM", // 82. Петрокоммерц (бывший)
    "NORDRUMM", // 83. Нордеа Банк (бывший ОРГРЭС)
    "MOSWRUMM", // 84. Мосстройэкономбанк
    "PERVRUMM", // 85. Первый Дортрансбанк
    "TRANRUMM", // 86. Транскапиталбанк
    "SMPLRUMM", // 87. СМП Банк (санация)
    "BINBRUMM", // 88. Бинбанк (санация)
    "FOTBRUMM", // 89. Фора-Банк (лицензия отозвана)
    "VEFKRUMM", // 90. ВЕФК (ликвидирован)

    // Расчетные/клиринговые центры
    "NCCLRUMM", // 91. НКО НКЦ (Национальный клиринговый центр)
    "CRDCRUMM", // 92. КРД (Клиринговый дом)
    "RTSBRUMM", // 93. РТС-Банк (расчетная система)
    "VTBCRUMM", // 94. ВТБ Клиринг
    "GPBKRU8X", // 95. ГПБ Клиринг

    // Специализированные финансовые институты
    "EXIARUMM", // 96. ЭКСАР (экспортное страхование)
    "RUSMRU8X", // 97. Росмединвест (медицинские проекты)
    "DOMBRUMM", // 98. Дом.РФ (ипотечный)
    "SVYAZUMM", // 99. Связь-Банк (почтовые услуги)
    "ROSNRUMM"  // 100. Роснефтебанк (топливный сектор)
  )
}
