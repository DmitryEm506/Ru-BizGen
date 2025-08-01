package ru.person.bizgen

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.popup.JBPopupFactory
import ru.person.bizgen.actions.GeneratorActionService
import ru.person.bizgen.di.getBizGenService

/**
 * Основное действие плагина.
 *
 * Показывает диалоговое окно с выбором активных генераторов.
 *
 * Для изменения отображаемого списка генераторов и их очередностью необходимо воспользоваться настройкой плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class BizGenMainAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val service = getBizGenService<GeneratorActionService>()

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



