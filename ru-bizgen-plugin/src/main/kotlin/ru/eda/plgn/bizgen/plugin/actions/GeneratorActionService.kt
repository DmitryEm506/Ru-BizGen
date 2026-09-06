package ru.eda.plgn.bizgen.plugin.actions

import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService

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
  fun findActionById(id: String): GeneratorInfo<*>?

  /**
   * Получить список активных генераторов.
   *
   * @return список активных генераторов
   */
  fun getActiveAnActions(): List<GeneratorAnAction>
}

/** Реализация [GeneratorActionService]. */
class GeneratorActionServiceImpl : GeneratorActionService {

  override fun findActionById(id: String): GeneratorInfo<*>? {
    return getBizGenService<GeneratorActionProvider>().getInfos()
      .firstOrNull { action -> action.id == id }
  }

  override fun getActiveAnActions(): List<GeneratorAnAction> {
    val activeSettings = getBizGenService<AppActionSettingsService>().getActiveActionSettings()
    val actions = getBizGenService<GeneratorActionProvider>().getAnActions()
      .associateBy { it.id }

    return activeSettings.mapNotNull { actionSetting -> actions[actionSetting.id] }
  }
}