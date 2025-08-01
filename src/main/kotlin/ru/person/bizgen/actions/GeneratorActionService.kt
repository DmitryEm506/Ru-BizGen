package ru.person.bizgen.actions

import ru.person.bizgen.di.BizGenService
import ru.person.bizgen.di.getBizGenService
import ru.person.bizgen.settings.AppActionSettingsService

/**
 * Сервис по работе с генераторами.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionService : BizGenService {

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
    return getBizGenService<GeneratorActionProvider>().getActions().firstOrNull { action -> action.id == id }
  }

  override fun getActiveAnActions(): List<GeneratorAnAction> {
    val activeSettings = getBizGenService<AppActionSettingsService>().getActiveActionSettings()
    val actions = getBizGenService<GeneratorActionProvider>().getAnActions().associateBy { it.id }

    return activeSettings.mapNotNull { actionSetting -> actions[actionSetting.id] }
  }
}