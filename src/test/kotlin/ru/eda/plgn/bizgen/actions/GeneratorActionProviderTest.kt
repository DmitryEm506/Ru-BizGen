package ru.eda.plgn.bizgen.actions

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import ru.eda.plgn.bizgen.BaseTest
import kotlin.reflect.KClass

/**
 * Модульные тесты для [GeneratorActionProvider].
 *
 * @author Dmitry_Emelyanenko
 */
internal class GeneratorActionProviderTest : BaseTest() {

  val provider: GeneratorActionProvider = GeneratorActionProviderImpl()

  @TestFactory
  internal fun `Should be property from generator actions is unique in provider`() = tests(
    listOf<Pair<(GeneratorAction<*>) -> String, String>>(
      Pair({ it.id }, "id"),
      Pair({ it.name }, "name"),
      Pair({ it.generator.javaClass.name }, "generator")
    ), { (_, description) -> "Generator property: $description" }) { (selector, _) ->

    val generatorsProperty = provider.getActions().map(selector)

    // then
    generatorsProperty.shouldBeUnique()
  }

  @Test
  internal fun `Should contains all implemented GeneratorAction`() {
    // given
    val generatorActions = findImplemented(GeneratorAction::class)

    generatorActions shouldContainExactlyInAnyOrder provider.getActions().map { it.javaClass }
  }

  @Test
  internal fun `Should contains all implemented GeneratorAnAction`() {
    // given
    val generatorAnActions = findImplemented(GeneratorAnAction::class)

    generatorAnActions shouldContainExactlyInAnyOrder provider.getAnActions().map { it.javaClass }
  }

  @Test
  internal fun `Should return correct action count`() {
    // given
    val generatorActions = findImplemented(GeneratorAction::class)
    val generatorAnActions = findImplemented(GeneratorAnAction::class)

    generatorActions.size shouldBe generatorAnActions.size shouldBe provider.actionCount()
  }

  companion object {
    fun <T : Any> findImplemented(baseClass: KClass<T>): List<Class<out T>> {
      return Reflections(baseClass.java.packageName, Scanners.SubTypes).getSubTypesOf(baseClass.java).asSequence()
        .filterNot { it.isInterface || java.lang.reflect.Modifier.isAbstract(it.modifiers) }
        .filterNot { it.name.contains(".Test") }
        .toList()
    }
  }
}