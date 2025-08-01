package ru.exmpl.rbdg.actions

import ru.exmpl.rbdg.di.RbdgService
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.settings.AppActionSettingsService

/**
 * Сервис по работе с генераторами.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionService : RbdgService {

  /**
   * Найти генератор по идентификатору.
   *
   * @param id идентификатор генератора
   * @return найденный генератор
   */
  fun findActionById(id: String): GeneratorAction<*>?

  /**
   * Получить список активных генераторов.
   *
   * @return список активных генераторов
   */
  fun getActiveAnActions(): List<GeneratorAnAction>
}

/** Реализация [GeneratorActionService]. */
class GeneratorActionServiceImpl() : GeneratorActionService {

  override fun findActionById(id: String): GeneratorAction<*>? {
    return getRbdgService<GeneratorActionProvider>().getActions().firstOrNull { action -> action.id == id }
  }

  override fun getActiveAnActions(): List<GeneratorAnAction> {
    val activeSettings = getRbdgService<AppActionSettingsService>().getActiveActionSettings()
    val actions = getRbdgService<GeneratorActionProvider>().getAnActions().associateBy { it.id }

    return activeSettings.mapNotNull { actionSetting -> actions[actionSetting.id] }
  }
}