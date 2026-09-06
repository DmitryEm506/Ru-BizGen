package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.sdk.ui.components.elements.checkBox
import com.intellij.driver.sdk.ui.components.elements.waitSelected
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.finder_ext.radioButtonByVisibleText
import java.nio.file.Path
import javax.swing.JCheckBox

/**
 * UI integration-тесты для [ru.eda.plgn.bizgen.plugin.ui.NotificationAndClipboardSettingsComponent].
 *
 * Тестирует взаимодействие radio buttons и checkbox в Settings → Tools → Ru BizGen через Driver SDK: переключение режимов уведомлений,
 * toggle буфера обмена, переключение символа обрамления.
 *
 * @author Dmitry_Emelyanenko
 */
internal class NotificationAndClipboardSettingsUITest : RuBizGenSettingsUITest() {

  @Test
  internal fun `Should open Settings dialog and navigate to Ru BizGen page`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("settingsSmoke", projectDir) {
      val disableRadio = radioButtonByVisibleText("Выключено")
      disableRadio.shouldBe("Ru BizGen settings content is not present", present)
    }
  }

  @Test
  internal fun `Should switch notification mode via radio buttons`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("switchNotificationMode", projectDir) {
      val disableRadio = radioButtonByVisibleText("Выключено")
      val hintRadio = radioButtonByVisibleText("Всплывающее окно")
      val bellRadio = radioButtonByVisibleText("""Лог событий "Колокольчик"""")

      assertTrue(disableRadio.isSelected, "DISABLE radio should be selected by default")
      assertFalse(hintRadio.isSelected, "HINT radio should not be selected by default")
      assertFalse(bellRadio.isSelected, "BELL radio should not be selected by default")

      hintRadio.click()
      hintRadio.shouldBe("HINT radio should be selected after click") { isSelected }
      disableRadio.shouldBe("DISABLE radio should not be selected after HINT click") { !isSelected }
      bellRadio.shouldBe("BELL radio should not be selected after HINT click") { !isSelected }

      bellRadio.click()
      bellRadio.shouldBe("BELL radio should be selected after click") { isSelected }
      disableRadio.shouldBe("DISABLE radio should not be selected after HINT click") { !isSelected }
      hintRadio.shouldBe("HINT radio should not be selected after BELL click") { !isSelected }
    }
  }

  @Test
  internal fun `Should toggle clipboard checkbox`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("clipboardToggle", projectDir) {
      val clipboardCheckbox = checkBox { byType(JCheckBox::class.java) }

      assertTrue(clipboardCheckbox.isSelected(), "Clipboard checkbox should be checked by default")

      clipboardCheckbox.uncheck()
      clipboardCheckbox.waitSelected(false)

      clipboardCheckbox.check()
      clipboardCheckbox.waitSelected(true)
    }
  }

  @Test
  internal fun `Should switch escape char via radio buttons`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("escapeCharSwitch", projectDir) {
      val doubleQuoteRadio = radioButtonByVisibleText("""Двойные кавычки (")""")
      val singleQuoteRadio = radioButtonByVisibleText("Одинарные кавычки (')")
      val noEscapeRadio = radioButtonByVisibleText("Без обрамления")

      assertTrue(doubleQuoteRadio.isSelected, "Double quote radio should be selected by default")
      assertFalse(singleQuoteRadio.isSelected, "Single quote radio should not be selected by default")

      singleQuoteRadio.click()
      singleQuoteRadio.shouldBe("Single quote radio should be selected after click") { isSelected }
      doubleQuoteRadio.shouldBe("Double quote radio should not be selected after single quote click") { !isSelected }

      noEscapeRadio.click()
      noEscapeRadio.shouldBe("No escape radio should be selected after click") { isSelected }
      singleQuoteRadio.shouldBe("Single quote radio should not be selected after no escape click") { !isSelected }
    }
  }
}
