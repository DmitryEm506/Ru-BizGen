package ru.eda.plgn.bizgen.plugin

import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.popup
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * UI тест, проверяющий отрисовку диалогового окна при вызове главного действия "ru.eda.plgn.bizgen.BizGenMainAction"
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenMainActionUITest : BaseUIIntegrationTest() {

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
}
