package ru.eda.plgn.bizgen.plugin

import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.popup
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IDETestContext
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes

/**
 * Feasibility-spike: проверяет, что JetBrains Integration Test Framework (Starter + Driver)
 * заводится в текущем стеке проекта (Kotlin 2.3, JUnit 6, IntelliJ Platform Gradle Plugin 2.16.0).
 *
 * Тест 1 (lifecycle) — критический: доказывает, что Starter запускает реальную IDE с плагином.
 * Тест 2 (Driver UI) — доказывает, что UI DSL работает и главный action плагина показывает popup.
 *
 * @author Dmitry_Emelyanenko
 */
class PluginStartupSpikeTest {

  init {
    di = DI {
      extend(di)
      bindSingleton<CIServer>(overrides = true) {
        object : CIServer by NoCIServer {
          override fun reportTestFailure(
            testName: String,
            message: String,
            details: String,
            linkToLogs: String?,
          ) {
            System.err.println("=== CIServer.reportTestFailure ===")
            System.err.println("testName = $testName")
            System.err.println("message = $message")
            System.err.println("details = $details")
            System.err.println("linkToLogs = $linkToLogs")
            fail { "$testName fails: $message.\n$details" }
          }
        }
      }
    }
  }

  @Test
  fun `Starter lifecycle - install plugin, start IDE, shutdown`(@TempDir projectDir: Path) {
    newContextWithPlugin("spikeLifecycle", projectDir).runIdeWithDriver(
      configure = { withVMOptions { addSystemProperty("java.net.preferIPv4Stack", "true") } },
    ).useDriverAndCloseIde {
      // waitForProjectOpen вместо waitForIndicators: waitForIndicators вызывает
      // StatusBarEx.getBackgroundProcessModels(), отсутствующий в IC 2024.3 (243).
      // Driver SDK 261 рассчитан на IntelliJ 2026.1 (261), где этот метод есть.
      // waitForProjectOpen использует только ProjectManager.getOpenProjects() — стандартный API.
      waitForProjectOpen(5.minutes)
    }
  }

  @Test
  fun `Driver UI - invoke plugin main action and find popup`(@TempDir projectDir: Path) {
    newContextWithPlugin("spikeUi", projectDir).runIdeWithDriver(
      configure = { withVMOptions { addSystemProperty("java.net.preferIPv4Stack", "true") } },
    ).useDriverAndCloseIde {
      waitForProjectOpen(1.minutes)
      ideFrame {
        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")
        val genPopup = popup()
        genPopup.shouldBe("Ru BizGen popup is not present", present)
      }
    }
  }

  private fun newContextWithPlugin(testName: String, projectDir: Path): IDETestContext {
    val context = Starter.newContext(
      testName = testName,
      TestCase(IdeProductProvider.IC, projectInfo = LocalProjectInfo(projectDir)).withVersion("2024.3"),
    )
    val pluginArchivePath = System.getProperty("path.to.build.plugin")
      ?: fail { "System property 'path.to.build.plugin' is not set. Ensure 'buildPlugin' task ran before the test." }
    context.pluginConfigurator.installPluginFromPath(Path.of(pluginArchivePath))
    return context
  }
}
