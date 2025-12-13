package ru.eda.plgn.bizgen.generators.impl.find_distance

import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseTest
import ru.eda.plgn.bizgen.generators.Generator
import java.util.Stack

/**
 * Обертка над [Percentile95DistanceUniqTest] для [String] генераторов.
 *
 * @param generator генератор
 * @property distanceLimit интервал, на котором происходит поиск лучшей дистанции уникальности
 * @property minimalUniqueDistance ожидаемая дистанция. Она может быть и меньше, чем максимально найденная дистанции
 * @author Dmitry_Emelyanenko
 */
internal abstract class Percentile95DistanceUniqStrTest(generator: Generator<String>, distanceLimit: IntRange, minimalUniqueDistance: Int) :
  Percentile95DistanceUniqTest<String>(generator, distanceLimit, minimalUniqueDistance)

/**
 * Обертка над [PercentileDistanceUniqTest] для [Percentile.PERCENTILE_95].
 *
 * @param T тип генератора
 * @param generator генератор
 * @property distanceLimit интервал, на котором происходит поиск лучшей дистанции уникальности
 * @property minimalUniqueDistance ожидаемая дистанция. Она может быть и меньше, чем максимально найденная дистанции
 * @author Dmitry_Emelyanenko
 */
internal abstract class Percentile95DistanceUniqTest<T : Any>(
  generator: Generator<T>, protected val distanceLimit: IntRange, protected val minimalUniqueDistance: Int,
) : PercentileDistanceUniqTest<T>(Percentile.PERCENTILE_95, generator) {

  @TestFactory
  internal fun `Generator distance should NotBeLess than expected`(): Iterable<DynamicTest> {
    return listOf(createDynamicTest(distanceLimit = distanceLimit, minimalUniqueDistance = minimalUniqueDistance))
  }

  protected fun createDynamicTest(distanceLimit: IntRange, minimalUniqueDistance: Int): DynamicTest {
    val actual = uniqueDistanceFinder(distanceLimit)
    return dynamicTest("[${generator.javaClass.simpleName}]: Tested distance ${distanceLimit.min()} .. ${distanceLimit.max()}. Min expecting $minimalUniqueDistance. Actual: $actual") {
      actual shouldBeGreaterThanOrEqual minimalUniqueDistance
    }
  }
}

/**
 * Нахождение дистанции уникальности.
 *
 * Дистанция уникальности - количество вызовов генератора, при котором получили уникальные значения.
 *
 * *Например: Генератор SWIFT (8) имеет дистанцию 3 - получается, что если вызывать его три раза, то с заданной вероятностью результаты
 * будут уникальные*
 *
 * Так как не хочется привязываться к специфике конкретного генератора, то определение дистанции уникальности происходит наивным методов, а
 * именно перебором:
 * - Задаётся процентиль, так как генератор будет выдавать уникальные значения всё равно с какой-то вероятностью
 * - Задаётся отрезок дистанции, в рамках которого будет происходить поиск
 *
 * Алгоритм нахождения необходимой дистанции. Разбит на две фазы.
 *
 * ***Первая фаза***. Грубое нахождение списка дистанций:
 * - Выбирается минимальная дистанция уникальности
 * - Делается [Percentile.basicAttempts] попыток. В рамках одной попытки происходит вызов генератора столько раз, сколько выбрали. При этом
 *   если все значения при этих вызовах уникальный, то попытка считается успешной
 * - После этого рассчитывается отношение успешных попыток к общему количеству попыток. Если полученное значение больше заданного в
 *   процентиле, то выбранная дистанция уникальности запоминается
 * - Происходит инкремент дистанции и алгоритм повторяется до тех пор, пока с выбранной дистанцией результат не станет меньше процентиля
 * - Полученный список с найденными дистанциями начинает обрабатываться - будет происходить уточнение найденной дистанции
 *
 * ***Вторая фаза***. Уточнение найденных дистанций и выбор лучшей:
 * - Количество попыток будут становиться больше и соответствует [Percentile.extendedAttempts]
 * - Если последняя (максимальная) дистанция проходит, то она и будет выдана как итоговый результат. Если дистанция не проходит, то
 *   выбирается предыдущая дистанция и алгоритм уточнения повторяется
 *
 * @param T тип генератора
 * @property percentile заданный процентиль
 * @property generator генератор
 * @author Dmitry_Emelyanenko
 */
internal abstract class PercentileDistanceUniqTest<T : Any>(private val percentile: Percentile, protected val generator: Generator<T>) :
  BaseDistanceUniqTest() {

  /**
   * Нахождение дистанции уникальности.
   *
   * @param distanceLimit отрезок поиска дистанций
   * @return найденная дистанция уникальности
   */
  protected fun uniqueDistanceFinder(distanceLimit: IntRange): Int {
    return uniqueDistanceFinder(percentile, distanceLimit)
  }

  /**
   * Нахождение дистанции уникальности.
   *
   * @param percentile выбранный процентиль
   * @param distanceLimit отрезок поиска дистанций
   * @return найденная дистанция уникальности
   */
  @Suppress("SameParameterValue")
  private fun uniqueDistanceFinder(percentile: Percentile, distanceLimit: IntRange): Int {
    val positiveAttempts = Stack<Int>().also { container ->
      distanceLimit.forEach { distance ->
        if (isUniqueInAttempts(percentile, Percentile::basicAttempts, distance))
          container.add(distance)
        else
          return@also
      }
    }

    if (positiveAttempts.isEmpty())
      throw RuntimeException("Generator: ${generator.javaClass.simpleName} failed to find a positive distance within the interval $distanceLimit.")

    while (positiveAttempts.isNotEmpty()) {
      val bestDistance = positiveAttempts.pop()

      if (isUniqueInAttempts(percentile, Percentile::extendedAttempts, bestDistance)) {
        return bestDistance
      }
    }

    throw RuntimeException("Generator: ${generator.javaClass.simpleName} failed to find a positive distance within the interval $distanceLimit.")
  }

  /**
   * Определение попадания в заданный процентиль.
   *
   * @param percentile процентиль
   * @param getAttempts количество попыток
   * @param distanceOfUnique количество вызовов генератора при котором считается, что генератор должен выдавать уникальные результаты
   * @return true - уникальность укладывается в заданный процентиль, false - результаты не укладываются в заданный процентиль
   */
  private fun isUniqueInAttempts(percentile: Percentile, getAttempts: (Percentile) -> Int, distanceOfUnique: Int): Boolean {
    val attempts = getAttempts(percentile)

    var positiveCount = 0
    repeat(attempts) {
      if (isUniqueInDistance(distanceOfUnique)) positiveCount++
    }

    return (positiveCount.toDouble() / attempts.toDouble()) >= percentile.probability
  }


  /**
   * Метод определения, что при заданном количестве вызова генератора результаты будут уникальные.
   *
   * @param distanceOfUnique количество вызовов генератора
   * @return true - результаты уникальные, false - результаты неуникальные
   */
  private fun isUniqueInDistance(distanceOfUnique: Int): Boolean {
    return HashSet<String>().let { container ->
      repeat(distanceOfUnique) {
        val result = generator.generate().toEditor

        if (!container.add(result)) return false
      }
    }.let { true }
  }
}

@Tag("distanceFinderTests")
internal abstract class BaseDistanceUniqTest : BaseTest()

/**
 * Описание процентиля.
 *
 * **See Also:** [Процентиль](https://ru.wikipedia.org/wiki/%D0%9F%D1%80%D0%BE%D1%86%D0%B5%D0%BD%D1%82%D0%B8%D0%BB%D1%8C)
 *
 * @property probability вероятность успешного исхода
 * @property basicAttempts количество попыток при нахождении искомой дистанции уникальности генератора
 * @property extendedAttempts количество попыток уточнения найденной дистанции уникальности генератора
 */
internal enum class Percentile(val probability: Double, val basicAttempts: Int, val extendedAttempts: Int) {
  PERCENTILE_95(probability = 0.95, basicAttempts = 10_000, extendedAttempts = 100_000),
}