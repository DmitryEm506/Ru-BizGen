package ru.exmpl.rbdg.notification

import ru.exmpl.rbdg.di.RbdgService
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.settings.RbdgAppSettingsRepository
import ru.exmpl.rbdg.settings.model.RbdgAppSettings

/**
 * Сервис для просмотра настроек уведомлений.
 *
 * @author Dmitry_Emelyanenko
 */
interface NotificationSettingsView : RbdgService {

  /**
   * Получение текущего режима уведомлений.
   *
   * @return режим уведомлений
   */
  fun getNotificationMode(): RbdgAppSettings.RbdgNotificationMode
}

/**
 * Сервис по работе с настройками уведомлений.
 *
 * @author Dmitry_Emelyanenko
 */
interface NotificationSettingsService : NotificationSettingsView {

  /**
   * Обновление режима уведомлений.
   *
   * @param notificationMode режим уведомлений
   */
  fun updateNotificationMode(notificationMode: RbdgAppSettings.RbdgNotificationMode)
}

/** Реализация [NotificationSettingsService]. */
class NotificationSettingsServiceImpl : NotificationSettingsService {

  override fun updateNotificationMode(notificationMode: RbdgAppSettings.RbdgNotificationMode) {
    getRbdgService<RbdgAppSettingsRepository>().settings().notificationMode = notificationMode
  }

  override fun getNotificationMode(): RbdgAppSettings.RbdgNotificationMode {
    return getRbdgService<RbdgAppSettingsRepository>().settings().notificationMode
  }
}