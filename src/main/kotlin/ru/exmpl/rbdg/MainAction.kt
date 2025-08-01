package ru.exmpl.rbdg

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.popup.JBPopupFactory
import ru.exmpl.rbdg.actions.GeneratorActionService
import ru.exmpl.rbdg.di.getRbdgService

/**
 * Основное действие плагина.
 *
 * Показывает диалоговое окно с выбором активных генераторов.
 *
 * Для изменения отображаемого списка генераторов и их очередностью необходимо воспользоваться настройкой плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class MainAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val service = getRbdgService<GeneratorActionService>()

    JBPopupFactory.getInstance()
      .createActionGroupPopup(
        "Сгенерировать данные",
        DefaultActionGroup(service.getActiveAnActions()),
        event.dataContext,
        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
        false,
      ).apply {
        showInBestPositionFor(event.dataContext)
      }
  }
}



