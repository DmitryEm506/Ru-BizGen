package ru.exmpl.rbdg

import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckBoxListListener
import com.intellij.ui.ListUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.util.ui.JBUI
import ru.exmpl.rbdg.AppSettingsService.Direction
import javax.swing.JPanel

/**
 * AppActionsSettingComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class AppActionsSettingComponent {

  fun getComponent(): JPanel {
    return ActionListComponent().let {
      ToolbarDecorator.createDecorator(it)
        .setToolbarPosition(ActionToolbarPosition.TOP)
        .setPanelBorder(JBUI.Borders.empty())
        .setScrollPaneBorder(JBUI.Borders.empty())
        .disableAddAction()
        .disableRemoveAction()
        .setForcedDnD()
        .setMoveUpAction { _ -> it.moveUpOrDown(true) }
        .setMoveDownAction { _ -> it.moveUpOrDown(false) }
        .createPanel()
    }
  }
}

private class ActionListComponent : CheckBoxList<String>(listener) {
  private val availableActions: List<ActualAction> = getRbdgService<AppSettingsService>().getActions()

  init {
    availableActions.forEach { action ->
      addItem(action.id, action.description, action.selected)
    }
  }

  fun moveUpOrDown(isUp: Boolean) {
    if (isUp) {
      val service = getRbdgService<AppSettingsService>()
      if (service.moveAction(selectedIndex, Direction.UP)) {
        ListUtil.moveSelectedItemsUp(this)
      }
    } else {
      val service = getRbdgService<AppSettingsService>()
      if (service.moveAction(selectedIndex, Direction.DOWN)) {
        ListUtil.moveSelectedItemsDown(this)
      }
    }
  }

  private companion object {
    val listener: CheckBoxListListener = CheckBoxListListener { index, value ->
      val settingsService = getRbdgService<AppSettingsService>()

      settingsService.findActionByIndex(index)?.let { action ->
        action.selected = value
      }
    }
  }
}