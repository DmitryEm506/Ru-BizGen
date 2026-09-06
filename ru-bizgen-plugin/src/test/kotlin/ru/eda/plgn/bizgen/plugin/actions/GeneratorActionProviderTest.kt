package ru.eda.plgn.bizgen.plugin.actions

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.plugin.BaseTest

/**
 * Модульные тесты для [GeneratorActionProvider].
 *
 * @author Dmitry_Emelyanenko
 */
internal class GeneratorActionProviderTest : BaseTest() {

  val provider: GeneratorActionProvider = GeneratorActionProviderImpl()

  @TestFactory
  internal fun `Should be property from generator actions is unique in provider`() = tests(
    listOf<Pair<(GeneratorInfo<*>) -> String, String>>(
      Pair({ it.id }, "id"),
      Pair({ it.name }, "name"),
      Pair({ it.generator.javaClass.name }, "generator")
    ), { (_, description) -> "Generator property: $description" }) { (selector, _) ->

    val generatorsProperty = provider.getInfos().map(selector)

    // then
    generatorsProperty.shouldBeUnique()
  }

  @Test
  internal fun `Should contains all implemented GeneratorAction`() {
    // given
    val generatorActions = findImplemented(GeneratorInfo::class)

    generatorActions shouldContainExactlyInAnyOrder provider.getInfos().map { it.javaClass }
  }

  @Test
  internal fun `Should contains all implemented GeneratorAnAction`() {
    // given
    provider.getInfos().size shouldBe provider.getAnActions().size
  }
}