package ru.exmpl.rbdg.settings.persistent

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import ru.exmpl.rbdg.settings.AppSettingsStore
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
    Storage("rbdg_settings.xml"),
  ],
  category = SettingsCategory.PLUGINS,
)
internal class AppPersistentSettings(
  var settings: RbdgAppSettings = RbdgAppSettings()
) : PersistentStateComponent<RbdgAppSettings>, AppSettingsStore {

  override fun getState(): RbdgAppSettings {
    return settings
  }

  override fun loadState(rbdgAppSettings: RbdgAppSettings) {
    settings = rbdgAppSettings
  }

  override fun getAppSettings(): RbdgAppSettings {
    return settings
  }
}

