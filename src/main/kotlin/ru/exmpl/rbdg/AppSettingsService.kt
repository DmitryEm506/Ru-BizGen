package ru.exmpl.rbdg

import com.intellij.openapi.components.Service


interface AppSettingsService {
  fun getAppSettings(): AppSettings

  fun findActionByIndex(index: Int): ActualAction?

  fun getActions(): List<ActualAction>
}

/**
 * AppSettingsService.
 *
 * @author Dmitry_Emelyanenko
 */
@Service
class AppSettingsServiceImpl : AppSettingsService {

  override fun getAppSettings(): AppSettings {
    return store().getAppSettings()
  }

  override fun findActionByIndex(index: Int): ActualAction? {
    return store().getAppSettings().actualActions[index]
  }

  override fun getActions(): List<ActualAction> {
    return store().getAppSettings().actualActions
  }

  private companion object {
    fun store(): AppSettingsStore {
      return getRbdgService<AppSettingsStore>()
    }
  }
}
