package ru.exmpl.rbdg

import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckBoxListListener
import com.intellij.ui.ToolbarDecorator
import com.intellij.util.ui.JBUI
import javax.swing.JPanel

/**
 * AppActionsSettingComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class AppActionsSettingComponent {

  fun getComponent(): JPanel {
    return CheckBoxList<String>(listener).apply {

      val settingsService = getRbdgService<AppSettingsService>()

      settingsService.getActions().forEach { action ->
        addItem(action.id, action.description, action.selected)
      }
    }.run {
      ToolbarDecorator.createDecorator(this)
        .setToolbarPosition(ActionToolbarPosition.TOP)
        .setPanelBorder(JBUI.Borders.empty())
        .setScrollPaneBorder(JBUI.Borders.empty())
        .disableAddAction()
        .disableRemoveAction()
        .setForcedDnD()
        .createPanel()
    }
  }

  private val listener: CheckBoxListListener = CheckBoxListListener { index, value ->
    val settingsService = getRbdgService<AppSettingsService>()

    settingsService.findActionByIndex(index)?.let { action ->
      action.selected = value
    }
  }
}