package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.sdk.ui.components.UiComponent
import com.intellij.driver.sdk.ui.components.common.IdeaFrameUI
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.ide.starter.models.IDEStartResult
import ru.eda.plgn.bizgen.plugin.base.BaseUIIntegrationTest
import java.nio.file.Path

/**
 * Абстракция для тестирования настроек плагина.
 *
 * @author Dmitry_Emelyanenko
 */
abstract class RuBizGenSettingsUITest : BaseUIIntegrationTest() {

  protected fun workWithRuBizGenSettings(testName: String, projectDir: Path, contentAction: UiComponent.() -> Unit): IDEStartResult {
    return runIdea(testName, projectDir) {
      ideFrame {
        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")
          content(contentAction)
        }
      }
    }
  }

  /**
   * Вариант [workWithRuBizGenSettings] для многошаговых тестов, в которых требуется
   * покидать scope `content { }` (например, для взаимодействия с модальными диалогами
   * переименования/сброса) и возвращаться к `settingsDialog { content { } }`.
   *
   * После открытия Settings и навигации к `Tools → Ru BizGen` управление передаётся
   * в [block] в scope [IdeaFrameUI], где тест может свободно чередовать
   * `settingsDialog { content { } }` и `dialog { }`.
   *
   * @param testName имя тест-кейса
   * @param projectDir директория проекта
   * @param block бизнес-логика теста в scope [IdeaFrameUI]
   * @return результат запуска IDE
   */
  protected fun workWithRuBizGenSettingsMultiStep(
    testName: String, projectDir: Path, block: IdeaFrameUI.() -> Unit
  ): IDEStartResult {
    return runIdea(testName, projectDir) {
      ideFrame {
        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")
        }
        block()
      }
    }
  }
}