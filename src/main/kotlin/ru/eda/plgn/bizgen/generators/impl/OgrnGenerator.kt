package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.generators.impl.OgrnGenerator.randomOgrnIp
import ru.eda.plgn.bizgen.generators.impl.OgrnGenerator.randomOgrnLegal
import kotlin.random.Random

/**
 * ОГРН ИП.
 *
 * **See Also:**
 * [Основной государственный регистрационный номер](https://ru.wikipedia.org/wiki/%D0%9E%D1%81%D0%BD%D0%BE%D0%B2%D0%BD%D0%BE%D0%B9_%D0%B3%D0%BE%D1%81%D1%83%D0%B4%D0%B0%D1%80%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9_%D1%80%D0%B5%D0%B3%D0%B8%D1%81%D1%82%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80)
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnIpGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomOgrnIp())
}

/**
 * ОГРН ЮЛ.
 *
 * **See Also:**
 * [Основной государственный регистрационный номер](https://ru.wikipedia.org/wiki/%D0%9E%D1%81%D0%BD%D0%BE%D0%B2%D0%BD%D0%BE%D0%B9_%D0%B3%D0%BE%D1%81%D1%83%D0%B4%D0%B0%D1%80%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9_%D1%80%D0%B5%D0%B3%D0%B8%D1%81%D1%82%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80)
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnLegalGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = randomOgrnLegal())
}

/**
 * ОГРН.
 *
 * **See Also:**
 * [Основной государственный регистрационный номер](https://ru.wikipedia.org/wiki/%D0%9E%D1%81%D0%BD%D0%BE%D0%B2%D0%BD%D0%BE%D0%B9_%D0%B3%D0%BE%D1%81%D1%83%D0%B4%D0%B0%D1%80%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9_%D1%80%D0%B5%D0%B3%D0%B8%D1%81%D1%82%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80)
 *
 * @author Dmitry_Emelyanenko
 */
private object OgrnGenerator {

  /** Random ogrn ip. */
  fun randomOgrnIp() = randomOgrnIp(61)

  /** Random ogrn legal. */
  fun randomOgrnLegal() = randomOgrnLegal(61)

  /**
   * Генерирует ОГРН для юридического лица (13 цифр)
   *
   * Формат: С ГГ КК ХХХХХ Ч.
   * - С (1 цифра) - признак отнесения (1, 5)
   * - ГГ (2 цифры) - год регистрации (00-99)
   * - КК (2 цифры) - код региона (01-99)
   * - ХХХХХ (5 цифр) - номер записи в ЕГРЮЛ
   * - Ч (1 цифра) - контрольная сумма.
   */
  fun randomOgrnLegal(regionCode: Int? = null): String {
    val regCode = regionCode ?: Random.nextInt(1, 100)
    require(regCode in 1..99) { "Код региона должен быть 1-99" }

    val sign = if (Random.nextBoolean()) "1" else "5" // 1 или 5
    val year = "%02d".format(Random.nextInt(0, 100)) // 00-99
    val region = "%02d".format(regCode)
    val inspectCode = "%02d".format(Random.nextInt(0, 100))
    val number = "%05d".format(Random.nextInt(0, 100000))

    val base = "$sign$year$region$inspectCode$number"
    val checksum = (base.toBigInteger() % 13.toBigInteger()).toString().last()

    return base + checksum
  }

  /**
   * Генерирует ОГРНИП для ИП (15 цифр).
   *
   * Формат: 3ГГККХХХХХХХХХЧ.
   * - ГГ (2 цифры) - год регистрации
   * - КК (2 цифры) - код региона
   * - ХХХХХХХХХ (9 цифр) - номер записи
   * - Ч (1 цифра) - контрольная сумма (mod 13)
   */
  @Suppress("unused")
  fun randomOgrnIp(regionCode: Int? = null): String {
    // 1. Признак ИП (всегда 3)
    val type = "3"

    // 2. Год регистрации (00-99)
    val year = "%02d".format(Random.nextInt(0, 100))

    // 3. Код региона (01-99)
    val region = "%02d".format(Random.nextInt(1, 100))

    // 4. Номер записи (9 цифр)
    val number = "%09d".format(Random.nextInt(0, 1_000_000_000))

    // 5. Контрольная цифра (mod 13)
    val base = "$type$year$region$number" // 1 + 2 + 2 + 9 = 14 цифр
    val checksum = (base.toBigInteger() % 11.toBigInteger()).let {
      if (it == 10.toBigInteger()) 0 else it
    }.toString()

    // Собираем итоговый ОГРНИП (15 цифр)
    return "$type$year$region$number$checksum".also {
      require(it.length == 15) {
        "Некорректная длина: ${it.length}. Ожидается 15 цифр. Номер: $it"
      }
    }
  }
}
