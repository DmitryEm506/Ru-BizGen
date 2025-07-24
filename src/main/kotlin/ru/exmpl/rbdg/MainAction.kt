package ru.exmpl.rbdg

import ai.grazie.utils.mpp.UUID
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

/**
 * MainAction.
 *
 * @author Dmitry_Emelyanenko
 */
class MainAction : AnAction() {
  override fun actionPerformed(p0: AnActionEvent) {
    val settings = getRbdgService<AppSettingsService>().getAppSettings()

    settings.actualActions.firstOrNull()?.let { action ->
      action.id = UUID.random().toString()
    }

    ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
      Notification(
        " ", "ИНН", "Значение ИНН успешно скопировано в буфер обмена",
        NotificationType.INFORMATION
      )
    )
  }
}



