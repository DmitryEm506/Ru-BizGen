package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.client.Remote
import com.intellij.driver.sdk.ui.components.elements.dialog
import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.components.elements.textField
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitFor
import com.intellij.driver.sdk.waitNotNull
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

      val firstItem = waitNotNull("First generator item key should be available", 10.seconds) {
        checkBoxList.getItemAt(0)
      }
      waitFor("First generator checkbox should be checked by default", 10.seconds) {
        checkBoxList.isItemSelected(firstItem)
      }
      val wasChecked = checkBoxList.isItemSelected(firstItem)

      val bounds = actionList.getCellBounds(0)
      actionList.clickItemAtIndex(0, Point(10, bounds.height / 2))

      waitFor("Checkbox state should toggle after click on checkbox area", 10.seconds) {
        checkBoxList.isItemSelected(firstItem) != wasChecked
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
  internal fun `Should Driver UI - move generator down in list`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("moveDown", projectDir) {
      val actionList = list { byType(JList::class.java) }
      val itemsBefore = actionList.items

      assertTrue(itemsBefore.size >= 2, "Generator list should have at least 2 items")

      actionList.clickItemAtIndex(0)

      actionButtonByAccessibleNameAndPerform("Down")

      waitFor("First item should move to second position after MoveDown", 15.seconds) {
        actionList.items[1] == itemsBefore[0]
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
 * `isItemSelected(item: String)` принимает ключ элемента (action ID), а не индекс.
 * Это позволяет избежать конфликта перегрузок: `CheckBoxList` имеет `isItemSelected(int)` и
 * `isItemSelected(T)`, который стирается в `isItemSelected(Object)`. При передаче `Integer`
 * драйвер может выбрать `isItemSelected(Object)` — тогда `myItemMap.get(Integer)` вернёт `null`.
 * Передача `String` совместима только с `Object`, поэтому перегрузка разрешается однозначно.
 */
@Remote("com.intellij.ui.CheckBoxList")
interface CheckBoxListRef {
  fun getItemAt(index: Int): String?
  fun isItemSelected(item: String): Boolean
}
