package ru.exmpl.rbdg.settings.persistent

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import ru.exmpl.rbdg.actions.GeneratorActionProvider
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.settings.RbdgAppSettingsRepository
import ru.exmpl.rbdg.settings.model.RbdgAppSettings

/**
 * Отвечает за сохранение настроек [RbdgAppSettings] на диск.
 *
 * @property settings настройки плагина
 * @author Dmitry_Emelyanenko
 */
@State(
  name = "Ru Business Data Generator",
  storages = [
    Storage("rbdg_settings1.xml"),
  ],
  category = SettingsCategory.PLUGINS,
)
internal class RbdgAppSettingsPersistent(
  var settings: RbdgAppSettings = RbdgAppSettings()
) : PersistentStateComponent<RbdgAppSettings>, RbdgAppSettingsRepository {

  override fun getState(): RbdgAppSettings {
    return settings
  }

  override fun loadState(rbdgAppSettings: RbdgAppSettings) {
    // если сохраненное количество действий не равно общему количеству актуальных действий,
    // то происходит автоматическое восстановление настроек по умолчанию
    if (rbdgAppSettings.actualActions.size != getRbdgService<GeneratorActionProvider>().actionCount()) {
      rbdgAppSettings.restoreFromDefault()
    }

    settings = rbdgAppSettings
  }

  override fun settings(): RbdgAppSettings {
    return settings
  }
}

