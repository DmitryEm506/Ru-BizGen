package ru.eda.plgn.bizgen.actions

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import kotlin.reflect.KClass

/**
 * Модульные тесты для [GeneratorActionProvider].
 *
 * @author Dmitry_Emelyanenko
 */
class GeneratorActionProviderImplTest {

  val provider: GeneratorActionProvider = GeneratorActionProviderImpl()

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
      return Reflections(baseClass.java.packageName, Scanners.SubTypes).getSubTypesOf(baseClass.java)
        .filterNot { it.isInterface || java.lang.reflect.Modifier.isAbstract(it.modifiers) }
    }
  }
}