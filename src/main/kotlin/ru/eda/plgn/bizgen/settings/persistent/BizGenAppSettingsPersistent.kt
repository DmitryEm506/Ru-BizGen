package ru.eda.plgn.bizgen.settings.persistent

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import ru.eda.plgn.bizgen.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.di.getBizGenService
import ru.eda.plgn.bizgen.settings.BizGenAppSettingsRepository
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings

/**
 * Отвечает за сохранение настроек [BizGenAppSettings] на диск.
 *
 * @property settings настройки плагина
 * @author Dmitry_Emelyanenko
 */
@State(
  name = "Ru BizGen",
  storages = [
    Storage("bizgen_plugin_settings.xml"),
  ],
  category = SettingsCategory.PLUGINS,
)
internal class BizGenAppSettingsPersistent(
  var settings: BizGenAppSettings = BizGenAppSettings(),
) : PersistentStateComponent<BizGenAppSettings>, BizGenAppSettingsRepository {

  override fun getState(): BizGenAppSettings {
    return settings
  }

  override fun loadState(bizGenAppSettings: BizGenAppSettings) {
    // если сохраненное количество действий не равно общему количеству актуальных действий,
    // то происходит автоматическое восстановление настроек по умолчанию
    val persistentActionIds = bizGenAppSettings.actualActions.map { it.id }
    val availableActionsIds = getBizGenService<GeneratorActionProvider>().getActions().map { it.id }

    if (persistentActionIds != availableActionsIds) {
      bizGenAppSettings.restoreFromDefault()
    }

    settings = bizGenAppSettings
  }

  override fun settings(): BizGenAppSettings {
    return settings
  }
}

