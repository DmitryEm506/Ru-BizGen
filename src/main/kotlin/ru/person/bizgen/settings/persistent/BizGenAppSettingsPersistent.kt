package ru.person.bizgen.settings.persistent

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import ru.person.bizgen.actions.GeneratorActionProvider
import ru.person.bizgen.di.getBizGenService
import ru.person.bizgen.settings.BizGenAppSettingsRepository
import ru.person.bizgen.settings.model.BizGenAppSettings

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
  var settings: BizGenAppSettings = BizGenAppSettings()
) : PersistentStateComponent<BizGenAppSettings>, BizGenAppSettingsRepository {

  override fun getState(): BizGenAppSettings {
    return settings
  }

  override fun loadState(bizGenAppSettings: BizGenAppSettings) {
    // если сохраненное количество действий не равно общему количеству актуальных действий,
    // то происходит автоматическое восстановление настроек по умолчанию
    if (bizGenAppSettings.actualActions.size != getBizGenService<GeneratorActionProvider>().actionCount()) {
      bizGenAppSettings.restoreFromDefault()
    }

    settings = bizGenAppSettings
  }

  override fun settings(): BizGenAppSettings {
    return settings
  }
}

