package ru.eda.plgn.bizgen.notification

import ru.eda.plgn.bizgen.di.BizGenService
import ru.eda.plgn.bizgen.di.getBizGenService
import ru.eda.plgn.bizgen.settings.BizGenAppSettingsRepository
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings

/**
 * Сервис для просмотра настроек уведомлений.
 *
 * @author Dmitry_Emelyanenko
 */
interface NotificationSettingsView : BizGenService {

  /**
   * Получение текущего режима уведомлений.
   *
   * @return режим уведомлений
   */
  fun getNotificationMode(): BizGenAppSettings.BizGenNotificationMode
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
  fun updateNotificationMode(notificationMode: BizGenAppSettings.BizGenNotificationMode)
}

/** Реализация [NotificationSettingsService]. */
class NotificationSettingsServiceImpl : NotificationSettingsService {

  override fun updateNotificationMode(notificationMode: BizGenAppSettings.BizGenNotificationMode) {
    getBizGenService<BizGenAppSettingsRepository>().settings().notificationMode = notificationMode
  }

  override fun getNotificationMode(): BizGenAppSettings.BizGenNotificationMode {
    return getBizGenService<BizGenAppSettingsRepository>().settings().notificationMode
  }
}