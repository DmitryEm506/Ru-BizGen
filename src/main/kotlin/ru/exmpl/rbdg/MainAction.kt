package ru.exmpl.rbdg

import ai.grazie.utils.mpp.UUID
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import fleet.util.logging.logger
import org.apache.commons.lang3.RandomStringUtils

/**
 * MainAction.
 *
 * @author Dmitry_Emelyanenko
 */
class MainAction : AnAction() {
  private val log = logger<MainAction>()

  override fun actionPerformed(p0: AnActionEvent) {
    val settings = getRbdgService<AppSettingsService>().getAppSettings()

    log.info("User ID: ${settings.userId}")

    settings.userId = RandomStringUtils.randomAlphabetic(5)

    settings.actualActions.firstOrNull()?.let { action ->
      action.id = UUID.random().toString()
    }

    AppSettings.getDefault().userId = settings.userId

    log.info("User ID: ${settings.userId}")

    ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
      Notification(
        " ", "ИНН", "Значение ИНН успешно скопировано в буфер обмена",
        NotificationType.INFORMATION
      )
    )
  }
}


class AppSettings {
  var userId: String = "John Smith"
  var ideaStatus: Boolean = false

  var actualActions: List<ActualAction> = listOf(
    ActualAction("1", 0, "test_0", selected = true),
    ActualAction("2", 1, "test_1", selected = true),
    ActualAction("3", 2, "test_2", selected = true),
    ActualAction("4", 3, "test_3", selected = true),
  )

  fun restoreFromDefault() {
    val default = DEFAULT.actualActions

    actualActions.forEach { action ->
      default.find { it.id == action.id }?.let { defaultAction ->
        action.position = defaultAction.position
        action.description = defaultAction.description
        action.selected = defaultAction.selected
      }
    }
  }

  companion object {
    private val DEFAULT: AppSettings = AppSettings()

    fun getDefault(): AppSettings {
      return DEFAULT.deepCopyByJson()
    }
  }
}

data class ActualAction(
  var id: String = "",
  var position: Int = 0,
  var description: String = "",
  var selected: Boolean = true
)



