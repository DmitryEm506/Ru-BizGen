package ru.eda.plgn.bizgen.plugin

import com.intellij.driver.client.Remote
import com.intellij.driver.client.service
import com.intellij.driver.sdk.FileEditorManager
import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.openFile
import com.intellij.driver.sdk.singleProject
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.checkBox
import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.components.elements.popup
import com.intellij.driver.sdk.ui.components.elements.waitForNoOpenedDialogs
import com.intellij.driver.sdk.ui.components.elements.waitSelected
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.copyToClipboard
import com.intellij.driver.sdk.ui.getClipboardText
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitFor
import com.intellij.driver.sdk.waitNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.BaseUIIntegrationTest
import ru.eda.plgn.bizgen.plugin.base.finder_ext.radioButtonByVisibleText
import ru.eda.plgn.bizgen.plugin.settings.CheckBoxListRef
import java.awt.Point
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JCheckBox
import javax.swing.JList
import kotlin.time.Duration.Companion.seconds

/**
 * UI integration-тесты для [BizGenMainAction].
 *
 * Тестирует главное действие плагина через Driver SDK:
 * - smoke: вызов главного действия → popup появляется;
 * - popup content: список генераторов в popup не пуст;
 * - insert to editor: выбор генератора в popup → вставка сгенерированного значения в редактор;
 * - clipboard copy: выбор генератора в popup → копирование сгенерированного значения в буфер обмена.
 *
 * Тесты наследуют [BaseUIIntegrationTest] напрямую (Трек 1 — не-Settings),
 * каждый тест запускает отдельную IDE-инстанцию через `runIdea`.
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenMainActionUITest : BaseUIIntegrationTest() {

  /**
   * Smoke: вызов главного действия → popup появляется.
   *
   * Минимальная проверка: `invokeAction` → popup present.
   * Существующий тест из feasibility-спайка — остаётся как базовый smoke.
   */
  @Test
  internal fun `Should Driver UI - invoke plugin main action and find popup`(@TempDir projectDir: Path) {
    runIdea("spikeUi", projectDir) {
      ideFrame {
        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")
        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)
      }
    }
  }

  /**
   * Popup content: список генераторов в popup не пуст.
   *
   * `invokeAction` → popup → JList внутри popup → `items.isNotEmpty()`.
   * Доказывает: popup отрисован, активные генераторы переданы в `DefaultActionGroup`,
   * список отображается с элементами.
   *
   * После проверки popup закрывается (Escape) для чистого завершения теста.
   */
  @Test
  internal fun `Should Driver UI - display all generators in popup list`(@TempDir projectDir: Path) {
    runIdea("popupContent", projectDir) {
      ideFrame {
        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")
        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val actionList = genPopup.list { byType(JList::class.java) }
        val items = actionList.items
        assertTrue(items.isNotEmpty(), "Popup should contain generator items")

        keyboard { escape() }
      }
    }
  }

  /**
   * Insert to editor: выбор генератора в popup → вставка значения в редактор.
   *
   * 1. Создаёт пустой текстовый файл в проекте и открывает его в редакторе (`openFile`).
   * 2. `invokeAction(BizGenMainAction)` → popup.
   * 3. `clickItemAtIndex(0)` → `BaseGeneratorAction.actionPerformed` →
   *    `invokeLater { WriteCommandAction.runWriteCommandAction { editor.document.insertString } }`.
   * 4. `waitFor` проверяет, что текст редактора стал непустым (через `@Remote FileEditorManager`).
   *
   * Верификация через `@Remote` на `FileEditorManager.getSelectedTextEditor().getDocument().getText()` —
   * чтение текста редактора из IDE-процесса, без зависимости от UI accessibility hierarchy.
   * Вставка асинхронна (`invokeLater`), поэтому polling обязателен.
   *
   * Требует открытого редактора в data context — без него `actionPerformed` возвращается early
   * (`event.getData(CommonDataKeys.EDITOR) ?: return`). Файл создаётся в `projectDir` до старта IDE.
   */
  @Test
  internal fun `Should Driver UI - insert generated value to editor via popup`(@TempDir projectDir: Path) {
    val testFileName = "scratch.txt"
    Files.writeString(projectDir.resolve(testFileName), "")

    runIdea("popupInsert", projectDir) {
      ideFrame {
        driver.openFile(testFileName, waitForCodeAnalysis = false)

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val actionList = genPopup.list { byType(JList::class.java) }
        actionList.clickItemAtIndex(0)

        waitFor("Editor should contain inserted text after selecting generator from popup", 15.seconds) {
          val editor = driver.service<FileEditorManager>(driver.singleProject()).getSelectedTextEditor()
          editor?.getDocument()?.getText()?.isNotBlank() == true
        }
      }
    }
  }

  /**
   * Clipboard copy: выбор генератора в popup → копирование значения в буфер обмена.
   *
   * 1. Создаёт пустой текстовый файл и открывает его (нужен активный editor в data context).
   * 2. Устанавливает маркер в буфер (`copyToClipboard`) для обнаружения изменения.
   * 3. `invokeAction(BizGenMainAction)` → popup → `clickItemAtIndex(0)` →
   *    `BaseGeneratorAction.actionPerformed` → `CopyPasteManager.setContents`.
   * 4. `waitFor` проверяет, что буфер изменился с маркера на сгенерированное значение.
   *
   * Верификация через `Driver.getClipboardText()` — чтение system clipboard.
   * Копирование синхронно в `actionPerformed` (после `invokeLater` для insert),
   * но robot-click async → polling обязателен.
   *
   * Копирование условно: `getBizGenService<BizGenClipboardSettingsServiceView>().needToIns()`
   * (default `true`). В свежей IDE с плагином настройки по умолчанию применяются автоматически.
   */
  @Test
  internal fun `Should Driver UI - copy generated value to clipboard via popup`(@TempDir projectDir: Path) {
    val testFileName = "scratch.txt"
    Files.writeString(projectDir.resolve(testFileName), "")

    runIdea("popupClipboard", projectDir) {
      ideFrame {
        driver.openFile(testFileName, waitForCodeAnalysis = false)

        val marker = "___CLIPBOARD_BEFORE_TEST___"
        driver.copyToClipboard(marker)

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val actionList = genPopup.list { byType(JList::class.java) }
        actionList.clickItemAtIndex(0)

        waitFor("Clipboard should contain generated value after selecting generator from popup", 15.seconds) {
          val clipboardText = driver.getClipboardText().toString()
          clipboardText != marker && clipboardText.isNotBlank()
        }
      }
    }
  }

  /**
   * Cross-setting: escape char disabled → вставка raw значения (без обрамления кавычками).
   *
   * 1. Settings → `Без обрамления` radio → close Settings (OK).
   * 2. `openFile` → `invokeAction(BizGenMainAction)` → popup → `clickItemAtIndex(0)`.
   * 3. `waitFor` проверяет, что текст редактора непустой и НЕ начинается с `"`
   *    (escape char = `""` → `"$escapeChar${rsp.toClipboard}$escapeChar"` = raw value).
   *
   * Контраст с Этапом 4 (default escape = `"` → `rsp.toEditor` = double-quoted).
   * Ветка: `escapeChar != "\""` (`GeneratorAction.kt:61-62`).
   */
  @Test
  internal fun `Should Driver UI - insert raw value when escape char disabled`(@TempDir projectDir: Path) {
    val testFileName = "scratch.txt"
    Files.writeString(projectDir.resolve(testFileName), "")

    runIdea("escapeCharDisabled", projectDir) {
      ideFrame {
        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")
          content {
            radioButtonByVisibleText("Без обрамления").click()
          }
          okButton.click()
        }
        waitForNoOpenedDialogs()

        driver.openFile(testFileName, waitForCodeAnalysis = false)

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val actionList = genPopup.list { byType(JList::class.java) }
        actionList.clickItemAtIndex(0)

        waitFor("Editor should contain raw (unquoted) inserted text when escape char is disabled", 15.seconds) {
          val text = driver.service<FileEditorManager>(driver.singleProject())
            .getSelectedTextEditor()?.getDocument()?.getText()
          text != null && text.isNotBlank() && !text.startsWith("\"")
        }
      }
    }
  }

  /**
   * Cross-setting: clipboard OFF → вставка в редактор без копирования в буфер.
   *
   * 1. Settings → uncheck clipboard → close Settings (OK).
   * 2. `openFile` → `copyToClipboard(marker)` → `invokeAction` → popup → `clickItemAtIndex(0)`.
   * 3. `waitFor` проверяет, что буфер остался неизменным (`== marker`).
   *
   * Ветка: `needToIns() == false` → skip `CopyPasteManager.setContents` (`GeneratorAction.kt:78-81`).
   * Insert при этом выполняется (он до блока `if`).
   */
  @Test
  internal fun `Should Driver UI - skip clipboard when clipboard setting disabled`(@TempDir projectDir: Path) {
    val testFileName = "scratch.txt"
    Files.writeString(projectDir.resolve(testFileName), "")

    runIdea("clipboardDisabled", projectDir) {
      ideFrame {
        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")
          content {
            val clipboardCheckbox = checkBox { byType(JCheckBox::class.java) }
            clipboardCheckbox.uncheck()
            clipboardCheckbox.waitSelected(false)
          }
          okButton.click()
        }
        waitForNoOpenedDialogs()

        driver.openFile(testFileName, waitForCodeAnalysis = false)

        val marker = "___CLIPBOARD_BEFORE_TEST___"
        driver.copyToClipboard(marker)

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val actionList = genPopup.list { byType(JList::class.java) }
        actionList.clickItemAtIndex(0)

        waitFor("Clipboard should remain unchanged (marker) when clipboard setting is disabled", 15.seconds) {
          driver.getClipboardText().toString() == marker
        }
      }
    }
  }

  /**
   * Cross-component: toggle OFF генератор в Settings → popup исключает его.
   *
   * 1. Settings → toggle OFF первый checkbox (запомнить имя) → close Settings (OK).
   * 2. `invokeAction(BizGenMainAction)` → popup → `items` не содержит отключённый генератор,
   *    `items.size` уменьшился на 1.
   *
   * Доказывает: `changeActivity(index, false)` → `getActiveAnActions()` фильтрует неактивные →
   * popup получает отфильтрованный список.
   */
  @Test
  internal fun `Should Driver UI - exclude disabled generator from popup`(@TempDir projectDir: Path) {
    var disabledName = ""
    var totalCount = 0

    runIdea("excludeDisabled", projectDir) {
      ideFrame {
        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")
          content {
            val actionList = list { byType(JList::class.java) }
            val checkBoxList = driver.cast(actionList.component, CheckBoxListRef::class)

            val firstItem = waitNotNull("First generator item key should be available", 10.seconds) {
              checkBoxList.getItemAt(0)
            }

            val items = actionList.items
            assertTrue(items.isNotEmpty(), "Generator list should contain items")
            disabledName = items[0]
            totalCount = items.size

            waitFor("First generator checkbox should be checked by default", 10.seconds) {
              checkBoxList.isItemSelected(firstItem)
            }

            val bounds = actionList.getCellBounds(0)
            actionList.clickItemAtIndex(0, Point(10, bounds.height / 2))

            waitFor("First generator checkbox should be unchecked after toggle", 10.seconds) {
              !checkBoxList.isItemSelected(firstItem)
            }
          }
          okButton.click()
        }
        waitForNoOpenedDialogs()

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)

        val popupList = genPopup.list { byType(JList::class.java) }
        val popupItems = popupList.items

        assertTrue(popupItems.isNotEmpty(), "Popup should still contain other generators")
        assertTrue(
          popupItems.size == totalCount - 1,
          "Popup should have one fewer item after disabling one generator " +
            "(expected ${totalCount - 1}, got ${popupItems.size})"
        )
        assertTrue(
          disabledName !in popupItems,
          "Disabled generator '$disabledName' should not appear in popup"
        )

        keyboard { escape() }
      }
    }
  }

  /**
   * Edge case: все генераторы отключены → `BizGenMainAction.actionPerformed` не падает.
   *
   * 1. `@Remote` на `AppActionSettingsService` → `setAllActive(false)` — массовое отключение
   *    (быстрее, чем 31 UI-toggle; `@Remote("...", plugin = "...")` — plugin classloader).
   * 2. `activeCount()` → 0 — подтверждает, что настройки изменены.
   * 3. `invokeAction(BizGenMainAction)` → не выбрасывает исключение.
   *
   * Доказывает: `getActiveAnActions()` → empty `DefaultActionGroup` →
   * `createActionGroupPopup` не крашит → `showInBestPositionFor` не крашит.
   *
   * **Note:** Popup content не проверяется, т.к. `showInBestPositionFor` может не показать
   * popup без focused editor (`openFile` недоступен — scratch-файл не существует в test project).
   * Ключевое утверждение: action не падает с пустой action group.
   *
   * **`@Remote` limitation:** `setAllActive` и `activeCount` возвращают `Int` (примитив) —
   * `@Remote` dispatch корректно работает с примитивами. Методы, возвращающие complex types
   * (`ActionSettingsView?`), вызывают `IllegalArgumentException: Class void is not annotated
   * with @Remote annotation` из-за `DriverImpl.convertResult` → `refBridge`.
   */
  @Test
  internal fun `Should Driver UI - not crash when all generators disabled`(@TempDir projectDir: Path) {
    runIdea("emptyPopup", projectDir) {
      ideFrame {
        val actionService = driver.service(AppActionSettingsServiceRef::class)
        val count = actionService.setAllActive(false)
        assertTrue(count > 0, "setAllActive should have disabled at least one generator")

        val activeCount = actionService.activeCount()
        assertTrue(
          activeCount == 0,
          "After setAllActive(false), activeCount should be 0 but was $activeCount"
        )

        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        keyboard { escape() }
      }
    }
  }
}

/**
 * @Remote-интерфейс для [ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService] —
 * массовое изменение активности генераторов через plugin classloader.
 *
 * Возвращает `Int` (количество изменённых действий) — примитив, `@Remote` dispatch
 * корректно работает без `refBridge`. Методы с complex return type (`ActionSettingsView?`)
 * вызывают `IllegalArgumentException` в `DriverImpl.convertResult`.
 */
@Remote("ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService", plugin = "ru.eda.plgn.bizgen")
interface AppActionSettingsServiceRef {
  fun setAllActive(active: Boolean): Int
  fun activeCount(): Int
}
