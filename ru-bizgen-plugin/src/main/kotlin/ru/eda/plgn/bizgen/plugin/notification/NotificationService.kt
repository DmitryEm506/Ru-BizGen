package ru.eda.plgn.bizgen.plugin.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings

/**
 * Сервис уведомлений.
 *
 * @author Dmitry_Emelyanenko
 */
interface NotificationService : BizGenService {

  /**
   * Отправить уведомление.
   *
   * @param ctx контекст уведомления
   */
  fun sendNotification(ctx: NotificationCtx<*>)
}

/** Реализация [NotificationService]. */
class NotificationServiceImpl : NotificationService {

  override fun sendNotification(ctx: NotificationCtx<*>) {
    val settingsService = getBizGenService<NotificationSettingsView>()

    getNotificator(settingsService.getNotificationMode()).sendNotification(ctx)
  }

  private companion object {
    fun getNotificator(mode: BizGenAppSettings.BizGenNotificationMode): NotificationService {
      return when (mode) {
        BizGenAppSettings.BizGenNotificationMode.BELL -> BellNotification()
        BizGenAppSettings.BizGenNotificationMode.HINT -> HintNotification()
        BizGenAppSettings.BizGenNotificationMode.DISABLE -> SkipNotification()
      }
    }
  }
}

/** Уведомление через всплывающую подсказку. */
private class HintNotification : NotificationService {
  override fun sendNotification(ctx: NotificationCtx<*>) {
    JBPopupFactory.getInstance()
      .createHtmlTextBalloonBuilder(bufferInfo(ctx.result, textLimit = 255), null, JBColor.background(), null)
      .setFadeoutTime(3000) // Автоматическое закрытие через 3 секунды
      .createBalloon()
      .show(
        JBPopupFactory.getInstance().guessBestPopupLocation(ctx.editor),
        Balloon.Position.below
      )
  }
}

/** Уведомление через стандартный механизм уведомлений (колокольчик). */
private class BellNotification : NotificationService {
  override fun sendNotification(ctx: NotificationCtx<*>) {
    ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
      Notification(" ", "Generator: ${ctx.actionInfo.name}", bufferInfo(ctx.result, textLimit = null), NotificationType.INFORMATION)
    )
  }
}

/** Не отправлять уведомление. */
private class SkipNotification : NotificationService {
  override fun sendNotification(ctx: NotificationCtx<*>) = Unit
}

/**
 * Генерирование сообщения, что результат добавлен в буфер.
 *
 * @param T тип результата
 * @param result результат работы генератора
 * @param textLimit лимит на длину текста
 * @return полученный текст
 */
private fun <T : Any> bufferInfo(result: GeneratorResult<T>, textLimit: Int?): String {
  return result.toClipboard.toString().let { source ->
    when {
      textLimit != null && source.length > textLimit -> source.take(textLimit) + "..."
      else -> source
    }
  }.let { "$it добавлен в буфер" }
}