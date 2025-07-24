package ru.exmpl.rbdg

import ru.exmpl.rbdg.AppSettingsService.Direction
import ru.exmpl.rbdg.settings.AppSettingsStore
import ru.exmpl.rbdg.settings.model.RbdgAppSettings
import ru.exmpl.rbdg.settings.model.RbdgGeneratorActionSettings
import ru.exmpl.rbdg.settings.model.RbdgNotificationMode


interface AppSettingsService {
  fun getAppSettings(): RbdgAppSettings

  fun findActionByPosition(position: Int): RbdgGeneratorActionSettings?

  fun moveAction(sourcePosition: Int, direction: Direction): Boolean

  fun getActions(): List<RbdgGeneratorActionSettings>

  fun getDefaultActions(): List<RbdgGeneratorActionSettings>

  enum class Direction {
    UP,
    DOWN
  }
}

interface AppSettingsNotificationService {
  fun updateNotificationMode(notificationMode: RbdgNotificationMode)

  fun getNotificationMode(): RbdgNotificationMode
}

class AppSettingsNotificationServiceImpl : AppSettingsNotificationService {

  override fun updateNotificationMode(notificationMode: RbdgNotificationMode) {
    getRbdgService<AppSettingsStore>().getAppSettings().notificationMode = notificationMode
  }

  override fun getNotificationMode(): RbdgNotificationMode {
    return getRbdgService<AppSettingsStore>().getAppSettings().notificationMode
  }
}

/**
 * AppSettingsService.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsServiceImpl : AppSettingsService {

  override fun getAppSettings(): RbdgAppSettings {
    return store().getAppSettings()
  }

  override fun findActionByPosition(position: Int): RbdgGeneratorActionSettings? {
    return store().getAppSettings().actualActions.find { it.position == position }
  }

  override fun moveAction(sourcePosition: Int, direction: Direction): Boolean {
    val sourceAction = actions().find { it.position == sourcePosition } ?: return false

    val newPosition = when (direction) {
      Direction.UP -> sourcePosition - 1
      Direction.DOWN -> sourcePosition + 1
    }

    val nextAction = actions().find { it.position == newPosition } ?: return false

    sourceAction.position = newPosition
    nextAction.position = sourcePosition

    return true
  }

  override fun getActions(): List<RbdgGeneratorActionSettings> {
    return actions().sortedBy { it.position }
  }

  override fun getDefaultActions(): List<RbdgGeneratorActionSettings> {
    store().getAppSettings().restoreFromDefault()

    return getActions()
  }

  private companion object {
    fun actions(): List<RbdgGeneratorActionSettings> {
      return store().getAppSettings().actualActions
    }

    fun store(): AppSettingsStore {
      return getRbdgService<AppSettingsStore>()
    }
  }
}
