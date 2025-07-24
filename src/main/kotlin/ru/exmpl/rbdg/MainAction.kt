package ru.exmpl.rbdg

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.popup.JBPopupFactory
import ru.exmpl.rbdg.actions.GeneratorActionService
import ru.exmpl.rbdg.di.getRbdgService

/**
 * MainAction.
 *
 * @author Dmitry_Emelyanenko
 */
class MainAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val service = getRbdgService<GeneratorActionService>()

    JBPopupFactory.getInstance()
      .createActionGroupPopup(
        "Генератор тестовых данных",
        DefaultActionGroup(service.getActiveAnActions()),
        event.dataContext,
        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
        false,
      ).apply {
        showInBestPositionFor(event.dataContext)
      }
//    ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
//      Notification(
//        " ", "ИНН", "Значение ИНН успешно скопировано в буфер обмена",
//        NotificationType.INFORMATION
//      )
//    )
  }


}



