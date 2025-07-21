package ru.exmpl.rbdg

import ru.exmpl.rbdg.AppSettingsService.Direction


interface AppSettingsService {
  fun getAppSettings(): AppSettings

  fun findActionByPosition(position: Int): ActualAction?

  fun moveAction(sourcePosition: Int, direction: Direction): Boolean

  fun getActions(): List<ActualAction>

  fun getDefaultActions(): List<ActualAction>

  enum class Direction {
    UP,
    DOWN
  }
}

interface AppSettingsNotificationService {
  fun updateNotificationMode(notificationMode: NotificationMode)

  fun getNotificationMode(): NotificationMode
}

class AppSettingsNotificationServiceImpl : AppSettingsNotificationService {

  override fun updateNotificationMode(notificationMode: NotificationMode) {
    getRbdgService<AppSettingsStore>().getAppSettings().notificationMode = notificationMode
  }

  override fun getNotificationMode(): NotificationMode {
    return getRbdgService<AppSettingsStore>().getAppSettings().notificationMode
  }
}

/**
 * AppSettingsService.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsServiceImpl : AppSettingsService {

  override fun getAppSettings(): AppSettings {
    return store().getAppSettings()
  }

  override fun findActionByPosition(position: Int): ActualAction? {
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

  override fun getActions(): List<ActualAction> {
    return actions().sortedBy { it.position }
  }

  override fun getDefaultActions(): List<ActualAction> {
    store().getAppSettings().restoreFromDefault()

    return getActions()
  }

  private companion object {
    fun actions(): List<ActualAction> {
      return store().getAppSettings().actualActions
    }

    fun store(): AppSettingsStore {
      return getRbdgService<AppSettingsStore>()
    }
  }
}
