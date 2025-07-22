package ru.exmpl.rbdg.actions

import com.intellij.openapi.actionSystem.AnAction
import ru.exmpl.rbdg.actions.impl.UuidForTestGeneratorAction
import ru.exmpl.rbdg.actions.impl.UuidGeneratorAction
import ru.exmpl.rbdg.di.RbdgService

/**
 * Провайдер действий.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionProvider : RbdgService {

  /**
   * Получение всех описаний действий.
   *
   * @return перечень описаний действий
   */
  fun getActions(): List<GeneratorActionDescription<*>>

  /**
   * Получение всех действия для применения.
   *
   * @return перечень действия для применения
   */
  fun getAnActions(): List<AnAction>
}

/** Реализация [GeneratorActionProvider]. */
class GeneratorActionProviderImpl : GeneratorActionProvider {
  private val actions: List<BaseGeneratorAction<*>> = listOf(
    UuidGeneratorAction(),
    UuidForTestGeneratorAction()
  )

  override fun getActions(): List<GeneratorActionDescription<*>> {
    return actions
  }

  override fun getAnActions(): List<AnAction> {
    return actions
  }
}