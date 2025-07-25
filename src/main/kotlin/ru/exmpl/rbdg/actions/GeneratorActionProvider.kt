package ru.exmpl.rbdg.actions

import ru.exmpl.rbdg.actions.impl.UuidGeneratorAction
import ru.exmpl.rbdg.actions.impl.UuidInKtTestGeneratorAction
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
  fun getActions(): List<GeneratorAction<*>>

  /**
   * Получение всех действия для применения.
   *
   * @return перечень действия для применения
   */
  fun getAnActions(): List<GeneratorAnAction>
}

/** Реализация [GeneratorActionProvider]. */
class GeneratorActionProviderImpl : GeneratorActionProvider {

  /**
   * Основной компонент плагина.
   *
   * Должен содержать все действия, с которыми предполагается работа.
   *
   * ***ВАЖНО***
   * - от порядка добавления действий зависит порядок отображаемых действий, когда вызывается плагин и при отображении настроек
   * - после добавления действия необходимо скинуть настройки через пункт меню всех настроек *Settings --> Tools --> Ru Business Data
   *   Generator --> Список с генераторами --> Иконка "Reset"*
   */
  private val actions: List<BaseGeneratorAction<*>> = listOf(
    UuidGeneratorAction(),
    UuidInKtTestGeneratorAction()
  )

  override fun getActions(): List<GeneratorAction<*>> {
    return actions
  }

  override fun getAnActions(): List<GeneratorAnAction> {
    return actions
  }
}