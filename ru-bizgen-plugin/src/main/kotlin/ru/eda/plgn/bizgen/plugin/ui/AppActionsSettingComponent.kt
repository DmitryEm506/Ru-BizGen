package ru.eda.plgn.bizgen.plugin.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionToolbarPosition
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.Messages.showInputDialog
import com.intellij.openapi.ui.Messages.showYesNoDialog
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckBoxListListener
import com.intellij.ui.ListUtil
import com.intellij.ui.ToolbarDecorator.createDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.eda.plgn.bizgen.plugin.actions.BizGenSelectedActionEvent
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService.Direction
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.ActionSettingsView
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

  /**
   * Создание компонента отображения доступных генераторов.
   *
   * @return созданный компонент
   */
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
      .addExtraAction(actionListComponent.rename())
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

  fun reset(): AnAction = object : AnAction("Reset", "Сбросить настройки до значений по умолчанию", AllIcons.Actions.Rollback) {
    override fun actionPerformed(e: AnActionEvent) {
      showYesNoDialog(
        "Вы уверены, что хотите сбросить настройки до значений по умолчанию?",
        "Сброс Настроек",
        Messages.getInformationIcon()
      ).takeIf { it == Messages.YES }?.let {
        clear()
        fillByActions(getBizGenService<AppActionSettingsService>().restoreByDefault())

        val infos = getBizGenService<GeneratorActionProvider>().getInfos().associateBy { it.id }
        getBizGenService<GeneratorActionProvider>().getAnActions().forEach { action ->
          infos[action.id]?.let { info -> action.templatePresentation.text = info.name }
        }
      }
    }
  }

  fun rename(): AnAction = object : AnAction("Rename", "Переименовать генератор", AllIcons.Actions.Edit) {
    override fun actionPerformed(e: AnActionEvent) {
      val selectedIndex = selectedIndex
      if (selectedIndex < 0) return

      val actionSetting = getBizGenService<AppActionSettingsService>().findByPosition(selectedIndex) ?: return

      val newName = showInputDialog(
        null,
        "Введите новое имя для генератора:",
        "Переименование генератора",
        Messages.getQuestionIcon(),
        actionSetting.description,
        null
      ) ?: return

      if (newName.isBlank()) return

      getBizGenService<AppActionSettingsService>().renameAction(selectedIndex, newName)

      getBizGenService<GeneratorActionProvider>().getAnActions()
        .find { it.id == actionSetting.id }
        ?.let { it.templatePresentation.text = newName }

      fillByActions(getBizGenService<AppActionSettingsService>().getActionSettings())
    }
  }

  companion object {
    private val listener: CheckBoxListListener = CheckBoxListListener { index, value ->
      getBizGenService<AppActionSettingsService>().changeActivity(index, value)
    }
  }
}