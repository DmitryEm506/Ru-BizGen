package ru.eda.plgn.bizgen.ui

import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.Messages.showYesNoDialog
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckBoxListListener
import com.intellij.ui.ListUtil
import com.intellij.ui.ToolbarDecorator.createDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.eda.plgn.bizgen.actions.GeneratorActionService
import ru.eda.plgn.bizgen.actions.BizGenSelectedActionEvent
import ru.eda.plgn.bizgen.di.getBizGenService
import ru.eda.plgn.bizgen.settings.AppActionSettingsService
import ru.eda.plgn.bizgen.settings.AppActionSettingsService.Direction
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings.ActionSettingsView
import javax.swing.JPanel
import javax.swing.event.ListSelectionListener

/**
 * Компонент, который отвечает за отображение доступных генераторов с возможностью:
 * - Изменение порядка отображения их в списке действий в редакторе
 * - Включением\отключением их из списка отображения действий в редакторе
 * - Отправляет событие в шину какой именно генератор выбран
 * - Предоставляет возможность сбросить настройки списка генераторов до значения по умолчанию
 */
class AppActionsSettingComponent {

  fun createComponent(): JPanel = panel {
    val actionListComponent = ActionListComponent().apply {
      addListSelectionListener(selectionListener(this))
    }

    // панель, которая включает основные действия со списком
    val decoratedPanel = createDecorator(actionListComponent)
      .setToolbarPosition(ActionToolbarPosition.TOP)
      .setPanelBorder(JBUI.Borders.empty())
      .setScrollPaneBorder(JBUI.Borders.empty())
      .disableAddAction()
      .disableRemoveAction()
      .setMoveUpAction { _ -> actionListComponent.moveUpOrDown(true) }
      .setMoveDownAction { _ -> actionListComponent.moveUpOrDown(false) }
      .addExtraAction(actionListComponent.reset())
      .createPanel()

    row {
      cell(decoratedPanel)
        .align(Align.FILL)
        .resizableColumn()
    }.resizableRow()
  }

  private fun selectionListener(checkBoxList: CheckBoxList<String>): ListSelectionListener = ListSelectionListener { event ->
    // обработка идёт только финального события
    if (!event.valueIsAdjusting) {
      val selectedIndex = checkBoxList.selectedIndex

      val actionSetting = getBizGenService<AppActionSettingsService>().findByPosition(selectedIndex) ?: return@ListSelectionListener
      val generatorAction = getBizGenService<GeneratorActionService>().findActionById(actionSetting.id) ?: return@ListSelectionListener

      BizGenSelectedActionEvent.publish(generatorAction)
    }
  }
}

private class ActionListComponent : CheckBoxList<String>(listener) {
  private val availableActions: List<ActionSettingsView> = getBizGenService<AppActionSettingsService>().getActionSettings()

  init {
    fillByActions()
  }

  fun fillByActions(actions: List<ActionSettingsView> = availableActions) {
    clear()
    actions.forEach { action ->
      addItem(action.id, action.description, action.active)
    }
  }

  fun moveUpOrDown(isUp: Boolean) {
    val service = getBizGenService<AppActionSettingsService>()
    val direction = if (isUp) Direction.UP else Direction.DOWN

    if (service.moveTo(selectedIndex, direction)) {
      if (isUp) ListUtil.moveSelectedItemsUp(this) else ListUtil.moveSelectedItemsDown(this)
    }
  }

  fun reset(): AnAction = object : AnAction("Reset") {
    override fun actionPerformed(e: AnActionEvent) {
      showYesNoDialog(
        "Вы уверены, что хотите сбросить настройки до значений по умолчанию?",
        "Сброс Настроек",
        Messages.getInformationIcon()
      ).takeIf { it == Messages.YES }?.let {
        clear()
        fillByActions(getBizGenService<AppActionSettingsService>().restoreByDefault())
      }
    }
  }

  companion object {
    private val listener: CheckBoxListListener = CheckBoxListListener { index, value ->
      getBizGenService<AppActionSettingsService>().changeActivity(index, value)
    }
  }
}