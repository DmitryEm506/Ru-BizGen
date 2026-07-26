package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.client.Remote
import com.intellij.driver.sdk.ui.components.elements.dialog
import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.components.elements.textField
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitFor
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.finder_ext.actionButtonByAccessibleNameAndPerform
import java.awt.Point
import java.nio.file.Path
import javax.swing.JList
import javax.swing.JTextField
import kotlin.time.Duration.Companion.seconds

/**
 * UI integration-тесты для [ru.eda.plgn.bizgen.plugin.ui.AppActionsSettingComponent].
 *
 * Тестирует взаимодействие со списком генераторов в Settings → Tools → Ru BizGen через Driver SDK:
 * - отображение списка генераторов;
 * - выбор генератора → публикация [ru.eda.plgn.bizgen.plugin.actions.BizGenSelectedActionEvent] → обновление preview;
 * - переключение checkbox (активность генератора) → `changeActivity`;
 * - перемещение генератора вверх/вниз;
 * - переименование генератора через диалог `Messages.showInputDialog`;
 * - сброс настроек до значений по умолчанию через диалог `Messages.showYesNoDialog`.
 *
 * @author Dmitry_Emelyanenko
 */
internal class AppActionsSettingUITest : RuBizGenSettingsUITest() {

  @Test
  internal fun `Should Driver UI - display generator list in settings`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("actionsListSmoke", projectDir) {
      val actionList = list { byType(JList::class.java) }
      actionList.shouldBe("Generator list is not present in Ru BizGen settings", present)

      val items = actionList.items
      assertTrue(items.isNotEmpty(), "Generator list should contain items")
    }
  }

  @Test
  internal fun `Should Driver UI - update preview when selecting generator`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("selectionPreview", projectDir) {
      val actionList = list { byType(JList::class.java) }

      actionList.clickItemAtIndex(0)

      waitOneText(
        "ActionID label should update after selecting a generator",
        15.seconds,
      ) { uiText ->
        uiText.text.startsWith("ActionID: ") && uiText.text.length > "ActionID: ".length
      }
    }
  }

  @Test
  internal fun `Should Driver UI - toggle generator active state via checkbox`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("toggleCheckbox", projectDir) {
      val actionList = list { byType(JList::class.java) }
      val checkBoxList = driver.cast(actionList.component, CheckBoxListRef::class)

      val wasChecked = checkBoxList.isItemSelected(0)
      assertTrue(wasChecked, "First generator checkbox should be checked by default")

      val bounds = actionList.getCellBounds(0)
      actionList.clickItemAtIndex(0, Point(10, bounds.height / 2))

      waitFor("Checkbox state should toggle after click on checkbox area", 15.seconds) {
        checkBoxList.isItemSelected(0) != wasChecked
      }
    }
  }

  @Test
  internal fun `Should Driver UI - move generator up in list`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("moveUp", projectDir) {
      val actionList = list { byType(JList::class.java) }
      val itemsBefore = actionList.items

      assertTrue(itemsBefore.size >= 2, "Generator list should have at least 2 items")

      actionList.clickItemAtIndex(1)

      actionButtonByAccessibleNameAndPerform("Up")

      waitFor("Second item should move to first position after MoveUp", 15.seconds) {
        actionList.items[0] == itemsBefore[1]
      }
    }
  }

  @Test
  internal fun `Should Driver UI - rename generator via dialog`(@TempDir projectDir: Path) {
    workWithRuBizGenSettingsMultiStep("renameGenerator", projectDir) {
      settingsDialog {
        content {
          val actionList = list { byType(JList::class.java) }
          actionList.clickItemAtIndex(0)

          actionButtonByAccessibleNameAndPerform("Rename")
        }
      }

      dialog(title = "Переименование генератора") {
        val inputField = textField { byType(JTextField::class.java) }
        inputField.text = "Test Renamed Generator"
        okButton.click()
      }

      settingsDialog {
        content {
          val actionList = list { byType(JList::class.java) }
          waitFor("List item should be renamed after dialog confirmation", 15.seconds) {
            actionList.items[0] == "Test Renamed Generator"
          }
        }
      }
    }
  }

  @Test
  internal fun `Should Driver UI - reset generators to defaults via dialog`(@TempDir projectDir: Path) {
    var originalName = ""

    workWithRuBizGenSettingsMultiStep("resetGenerators", projectDir) {
      settingsDialog {
        content {
          val actionList = list { byType(JList::class.java) }
          originalName = actionList.items[0]

          actionList.clickItemAtIndex(0)

          actionButtonByAccessibleNameAndPerform("Rename")
        }
      }

      dialog(title = "Переименование генератора") {
        val inputField = textField { byType(JTextField::class.java) }
        inputField.text = "Custom Name For Reset"
        okButton.click()
      }

      settingsDialog {
        content {
          val actionList = list { byType(JList::class.java) }
          waitFor("List item should be renamed before reset", 15.seconds) {
            actionList.items[0] == "Custom Name For Reset"
          }
        }
      }

      settingsDialog {
        content {
          actionButtonByAccessibleNameAndPerform("Reset")
        }
      }

      dialog(title = "Сброс Настроек") {
        pressButton("Yes")
      }

      settingsDialog {
        content {
          val actionList = list { byType(JList::class.java) }
          waitFor("List item should be restored to original name after reset", 15.seconds) {
            actionList.items[0] == originalName
          }
        }
      }
    }
  }
}

/**
 * @remote-интерфейс для чтения состояния checkbox в [com.intellij.ui.CheckBoxList].
 *
 * `CheckBoxList.isItemSelected(index)` наследуется из `CheckBoxListBase` и возвращает текущее состояние checkbox для элемента по индексу.
 */
@Remote("com.intellij.ui.CheckBoxList")
interface CheckBoxListRef {
  fun isItemSelected(index: Int): Boolean
}
