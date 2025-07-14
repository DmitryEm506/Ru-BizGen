package ru.exmpl.rbdg

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
  name = "Ru Business Data Generator",
  storages = [
    Storage("rbdg_settings.xml"),
  ],
  category = SettingsCategory.PLUGINS,
)
@Service
internal class AppPersistentSettings(
  var settings: AppSettings = AppSettings()
) : PersistentStateComponent<AppSettings>, AppSettingsStore {

  override fun getState(): AppSettings {
    return settings
  }

  override fun loadState(appSettings: AppSettings) {
    settings = appSettings
  }

  override fun getAppSettings(): AppSettings {
    return settings
  }
}

interface AppSettingsStore {

  fun getAppSettings(): AppSettings
}
