package ru.exmpl.rbdg.notification

import com.intellij.codeInsight.hint.HintManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import ru.exmpl.rbdg.di.RbdgService
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.settings.model.RbdgAppSettings.RbdgNotificationMode

/**
 * NotificationService.
 *
 * @author Dmitry_Emelyanenko
 */
interface NotificationService : RbdgService {
  fun sendNotification(editor: Editor, result: GeneratorResult<*>)
}

class NotificationServiceImpl : NotificationService {

  private val notificationsByMode = Map/////////////

  override fun sendNotification(editor: Editor, result: GeneratorResult<*>) {
    val settingsService = getRbdgService<NotificationSettingsView>()
    settingsService.getNotificationMode()
  }
}

private sealed interface NotificationByMode {

  val applyingMode: RbdgNotificationMode

  fun notify(editor: Editor, result: GeneratorResult<*>)
}

private class HintNotification(
  override val applyingMode: RbdgNotificationMode = RbdgNotificationMode.HINT
) : NotificationByMode {

  override fun notify(editor: Editor, result: GeneratorResult<*>) {
    HintManager.getInstance().showSuccessHint(editor, "${result.data} скопирован в буфер обмена")
  }
}

private class BellNotification(
  override val applyingMode: RbdgNotificationMode = RbdgNotificationMode.BELL,
) : NotificationByMode {

  override fun notify(editor: Editor, result: GeneratorResult<*>) {
    ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
      Notification(" ", "", result.data.toString(), NotificationType.INFORMATION)
    )
  }
}

private class DisableNotification(
  override val applyingMode: RbdgNotificationMode = RbdgNotificationMode.DISABLE
) : NotificationByMode {
  override fun notify(editor: Editor, result: GeneratorResult<*>) = Unit
}