package ru.eda.plgn.bizgen.core.generator_info

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Тесты группировки [GeneratorInfoProvider.generatorInfos] по [GeneratorCategory].
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("GeneratorInfoProvider: группировка по категориям")
internal class GeneratorInfoProviderTest {

  private val categoryOrder = listOf(
    GeneratorCategory.TECHNICAL,
    GeneratorCategory.BANKING,
    GeneratorCategory.LEGAL,
    GeneratorCategory.GEO,
    GeneratorCategory.PERSONAL,
  )

  @Nested
  @DisplayName("5.5 Группировка по категориям")
  inner class GroupingCases {

    @Test
    internal fun `Should place technical generators first`() {
      GeneratorInfoProvider.generatorInfos.first().category shouldBe GeneratorCategory.TECHNICAL
    }

    @Test
    internal fun `Should place personal generators last`() {
      GeneratorInfoProvider.generatorInfos.last().category shouldBe GeneratorCategory.PERSONAL
    }

    @Test
    internal fun `Should group generators of same category contiguously without gaps`() {
      val infos = GeneratorInfoProvider.generatorInfos

      GeneratorCategory.entries.forEach { category ->
        val indices = infos.mapIndexed { index, info -> index to info }
          .filter { it.second.category == category }
          .map { it.first }

        if (indices.isNotEmpty()) {
          val first = indices.first()
          val last = indices.last()
          // Все индексы должны образовывать непрерывный диапазон
          indices.size shouldBe (last - first + 1)
        }
      }
    }

    @Test
    internal fun `Should order categories as TECHNICAL BANKING LEGAL GEO PERSONAL`() {
      val infos = GeneratorInfoProvider.generatorInfos
      val categorySequence = infos.map { it.category }.distinct()

      categorySequence shouldBe categoryOrder
    }
  }
}
