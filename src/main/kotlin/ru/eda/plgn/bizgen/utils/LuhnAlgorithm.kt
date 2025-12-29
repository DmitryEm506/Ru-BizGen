package ru.eda.plgn.bizgen.utils

/**
 * Алгоритм Луна.
 *
 * **See Also:** [Алгоритм Луна](https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D0%9B%D1%83%D0%BD%D0%B0)
 *
 * @author Dmitry_Emelyanenko
 */
object LuhnAlgorithm {

  /**
   * Рассчитывается контрольная цифра по алгоритму Луна на основе переданного числа.
   *
   * ***ВАЖНО***: длина исходного числа должна быть нечетной
   *
   * @param source переданное число
   * @return контрольная цифра
   */
  fun calculateCheckDigit(source: String): Int {
    require(source.length % 2 == 1) { "The source: $source (length = ${source.length}) must have an odd length" }

    return calculateLuhnSum(source, LuhnSumMode.FIND_CHECK_DIGIT)
      .let { (10 - (it % 10)) % 10 }
  }

  /**
   * Проверка, что переданное число валидное.
   *
   * ***ВАЖНО***: длина исходного числа должна быть четной.
   *
   * То есть рассчитывается контрольная цифра по алгоритму Луна, если:
   * - Остаток от деления на 10 = 0 - корректное значение
   * - Остаток от деления на 10 != 0 - некорректное значение
   *
   * @param source исходное число
   * @return true - валидное число, false - невалидное число
   */
  fun isValid(source: String): Boolean {
    require(source.length % 2 == 0) { "The source: $source (length = ${source.length}) must have an even length" }

    return (calculateLuhnSum(source, LuhnSumMode.VALID_SOURCE) % 10) == 0
  }

  /**
   * Расчет суммы по алгоритму Луна.
   *
   * Алгоритм следующий:
   * - Происходит переворачивание (revert) исходного числа: "1234" --> "4321"
   * - На основе режима [calculateMode] выбирается начальный индекс с которого цифры будут умножаться на 2.
   * - Если режим [LuhnSumMode.FIND_CHECK_DIGIT] - то все цифры, которые находятся на **четных** позициях (0,2,4,6 ...) умножаются
   * - Если режим [LuhnSumMode.VALID_SOURCE] - то все цифры, которые находятся на **нечетных** позициях (1,3,5,7 ...) умножаются
   * - Если после умножения получается число больше 9, то отнимается 9
   * - Полученные значения после умножения и значения, которые не умножались суммируются
   *
   * @param source исходное число
   * @param calculateMode режим расчета (для нахождения контрольной цифры или для валидации)
   * @return рассчитанная цифра
   */
  private fun calculateLuhnSum(source: String, calculateMode: LuhnSumMode): Int {
    return source.reversed()
      .mapIndexed { index, char ->
        val digit = char.digitToInt()

        when {
          index % 2 == calculateMode.mod -> digit
          digit < 5 -> digit * 2
          else -> digit * 2 - 9
        }
      }.sum()
  }

  /**
   * Режим расчета суммы по алгоритму Луна.
   *
   * @property mod значение остатка от деления на 2, то есть 1 - нечетное число, 0 - четное число
   */
  private enum class LuhnSumMode(val mod: Int) {
    /** Режим поиска проверочной цифры. */
    FIND_CHECK_DIGIT(1),

    /** Режим валидации исходного числа. */
    VALID_SOURCE(0)
  }
}