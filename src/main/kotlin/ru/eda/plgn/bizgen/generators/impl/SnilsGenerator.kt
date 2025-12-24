package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape

/**
 * Страховой номер индивидуального лицевого счёта (СНИЛС).
 *
 * Формат СНИЛС: «***ХХХ-ХХХ-ХХХ YY***»,
 *
 * где X, Y — цифры, причём первые девять цифр 'X' — это любые цифры, а последние две 'Y' фактически являются контрольной суммой.
 *
 * Алгоритм формирования контрольного числа СНИЛС таков:
 * - Каждая цифра СНИЛС умножается на номер своей позиции (позиции отсчитываются с конца, то есть, справа)
 * - Полученные произведения суммируются
 * - Получить остаток от деления на 101
 * - Если получилось 100, контрольное число равно 0
 *
 * *Например:*
 *
 * *СНИЛС 112-233-445 95. Проверяем правильность контрольного числа:*
 * - *цифры номера 1 1 2 2 3 3 4 4 5*
 * - *номер позиции 9 8 7 6 5 4 3 2 1*
 * - *Сумма = 1 ⋅ 9 + 1 ⋅ 8 + 2 ⋅ 7 + 2 ⋅ 6 + 3 ⋅ 5 + 3 ⋅ 4 + 4 ⋅ 3 + 4 ⋅ 2 + 5 ⋅ 1 = 95*
 * - *Контрольная сумма = 95 mod 101 = 95. Полученное значение совпадает с представленной контрольнной суммой*
 *
 * **See Also:**
 * [СНИЛС](https://ru.wikipedia.org/wiki/%D0%A1%D1%82%D1%80%D0%B0%D1%85%D0%BE%D0%B2%D0%BE%D0%B9_%D0%BD%D0%BE%D0%BC%D0%B5%D1%80_%D0%B8%D0%BD%D0%B4%D0%B8%D0%B2%D0%B8%D0%B4%D1%83%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE%D0%B3%D0%BE_%D0%BB%D0%B8%D1%86%D0%B5%D0%B2%D0%BE%D0%B3%D0%BE_%D1%81%D1%87%D1%91%D1%82%D0%B0)
 *
 * @author Dmitry_Emelyanenko
 */
class SnilsGenerator : Generator<String> {
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(randomSNILS())

  private companion object {
    val COEFFICIENTS = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    fun randomSNILS(): String {
      val data = List(COEFFICIENTS.size) { (1..9).random() }

      val checkDigit = data.zip(COEFFICIENTS)
        .sumOf { (d, c) -> d * c }
        .mod(101)
        .let { if (it == 100) 0 else it }

      return "${data[8]}${data[7]}${data[6]}-${data[5]}${data[4]}${data[3]}-${data[2]}${data[1]}${data[0]} " + "%02d".format(checkDigit)
    }
  }
}