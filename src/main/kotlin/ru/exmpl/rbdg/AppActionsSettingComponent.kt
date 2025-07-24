package ru.exmpl.rbdg

import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.Messages.showYesNoDialog
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckBoxListListener
import com.intellij.ui.ListUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.util.ui.JBUI
import ru.exmpl.rbdg.AppSettingsService.Direction
import ru.exmpl.rbdg.RbdgSelectedActionEvent.Companion.publish
import ru.exmpl.rbdg.settings.model.RbdgGeneratorActionSettings
import javax.swing.JPanel
import javax.swing.event.ListSelectionListener

/**
 * AppActionsSettingComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class AppActionsSettingComponent {

  fun getComponent(): JPanel {
    return ActionListComponent().let {
      it.addListSelectionListener(selectionListener(it))

      ToolbarDecorator.createDecorator(it)
        .setToolbarPosition(ActionToolbarPosition.TOP)
        .setPanelBorder(JBUI.Borders.empty())
        .setScrollPaneBorder(JBUI.Borders.empty())
        .disableAddAction()
        .disableRemoveAction()
        .setForcedDnD()
        .setMoveUpAction { _ -> it.moveUpOrDown(true) }
        .setMoveDownAction { _ -> it.moveUpOrDown(false) }
        .addExtraAction(it.reset())
        .createPanel()
    }
  }

  private companion object {
    private fun selectionListener(checkBoxList: CheckBoxList<String>): ListSelectionListener = ListSelectionListener { event ->
      if (event.valueIsAdjusting) {
        val selectedIndex = checkBoxList.selectedIndex
        val settingsService = getRbdgService<AppSettingsService>()
        settingsService.findActionByPosition(selectedIndex)?.let { action ->
          publish(action.description)
        }
      }
    }
  }
}

private class ActionListComponent : CheckBoxList<String>(listener) {
  private val availableActions: List<RbdgGeneratorActionSettings> = getRbdgService<AppSettingsService>().getActions()

  init {
    fillByActions()
  }

  fun fillByActions(actions: List<RbdgGeneratorActionSettings> = availableActions) {
    actions.forEach { action ->
      addItem(action.id, action.description, action.active)
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

  fun reset(): AnAction {
    return object : AnAction("Reset") {
      override fun actionPerformed(p0: AnActionEvent) {
        showYesNoDialog(
          "Вы уверены, что хотите сбросить настройки до значений по умолчанию?",
          "Сброс Настроек",
          Messages.getInformationIcon()
        ).let {
          if (it == Messages.YES) {
            clear()
            fillByActions(getRbdgService<AppSettingsService>().getDefaultActions())
          }
        }
      }
    }
  }

  companion object {
    private val listener: CheckBoxListListener = CheckBoxListListener { index, value ->
      val settingsService = getRbdgService<AppSettingsService>()
      settingsService.findActionByPosition(index)?.let { action ->
        action.active = value
      }
    }
  }
}