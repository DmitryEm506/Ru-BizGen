package ru.eda.plgn.bizgen.plugin

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IDETestContext
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.IDEStartResult
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.fail
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes

/**
 * Маркерная аннотация для интеграционных тестов плагина.
 *
 * Совмещает два механизма фильтрации (симметрично [ru.eda.plgn.bizgen.core.generator.impl.find_distance.DistanceFinderTest]):
 * - [Tag] `"integrationTests"` — для будущей тег-фильтрации на gradle-уровне.
 * - [EnabledIfSystemProperty] `runIntegrationTests=true` — для запуска из IDE через VM options
 *   и для gradle-запуска (см. `ru-bizgen-plugin/build.gradle.kts:86,103`, задача `integrationTest`
 *   дополнительно ограничена `enabled = runIntegrationTests`).
 *
 * @author Dmitry_Emelyanenko
 */
@Tag("integrationTests")
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.CLASS])
@EnabledIfSystemProperty(named = "runIntegrationTests", matches = "true")
annotation class IntegrationTest

/**
 * Базовый класс интеграционных тестов плагина.
 *
 * Содержит общую инфраструктуру JetBrains Integration Test Framework (Starter + Driver):
 * - настройку [CIServer] через Kodein DI (перехват failures → [fail]);
 * - фабрику [IDETestContext] [newContextWithPlugin] с установкой собранного архива плагина;
 * - резолвер пути к архиву плагина [resolvePluginArchivePath] (system property → fallback на `build/distributions/`).
 *
 * Конкретные тесты наследуются от этого класса и содержат только `@Test`-методы с бизнес-логикой.
 *
 * @author Dmitry_Emelyanenko
 */
@IntegrationTest
internal abstract class BaseUIIntegrationTest {

  /**
   * Версия IntelliJ IDEA IC, на которой запускаются интеграционные тесты.
   *
   * Наследники могут переопределить для запуска на другой версии.
   * По умолчанию `"2024.3"` — минимально поддерживаемая плагином (sinceBuild=242).
   */
  protected open val ideVersion: String = "2024.3"

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

  protected fun runIdea(testName: String, projectDir: Path, block: Driver.() -> Unit): IDEStartResult {
    return newContextWithPlugin(testName, projectDir).runIdeWithDriver().useDriverAndCloseIde {
      // waitForProjectOpen вместо waitForIndicators: waitForIndicators вызывает
      // StatusBarEx.getBackgroundProcessModels(), отсутствующий в IC 2024.3 (243).
      // Driver SDK 261 рассчитан на IntelliJ 2026.1 (261), где этот метод есть.
      // waitForProjectOpen использует только ProjectManager.getOpenProjects() — стандартный API.
      waitForProjectOpen(1.minutes)
      block()
    }
  }

  /**
   * Создаёт [IDETestContext] с установленным плагином для теста [testName].
   *
   * @param testName имя тест-кейса (используется Starter'ом для логирования/артефактов)
   * @param projectDir директория проекта, открываемого в тестовой IDE
   * @return настроенный контекст с установленным плагином
   */
  protected fun newContextWithPlugin(testName: String, projectDir: Path): IDETestContext {
    val context = Starter.newContext(
      testName = testName,
      TestCase(IdeProductProvider.IC, projectInfo = LocalProjectInfo(projectDir)).withVersion(ideVersion),
    )
    context.pluginConfigurator.installPluginFromPath(resolvePluginArchivePath())
    return context
  }

  /**
   * Резолвинг пути к архиву плагина для установки в тестовую IDE.
   *
   * Приоритет:
   * 1. System property `path.to.build.plugin` (выставляется gradle-задачей `integrationTest`,
   *    см. `ru-bizgen-plugin/build.gradle.kts:96-99`).
   * 2. Последний по версии `ru-bizgen-*.zip` в `build/distributions/` — для запуска из IDE
   *    после ручного выполнения `:ru-bizgen-plugin:buildPlugin` без необходимости править VM options.
   *
   * @return путь к архиву плагина
   */
  private fun resolvePluginArchivePath(): Path {
    System.getProperty("path.to.build.plugin")?.let { return Path.of(it) }

    val distDir = Path.of("build/distributions")
    if (!Files.isDirectory(distDir)) {
      fail { "System property 'path.to.build.plugin' is not set and $distDir not found. Run ':ru-bizgen-plugin:buildPlugin' first." }
    }

    val pluginZips = Files.list(distDir).use { stream ->
      stream
        .filter { it.fileName.toString().matches(Regex("""ru-bizgen-\d+\.\d+\.\d+\.zip""")) }
        .sorted(Comparator.reverseOrder())
        .toList()
    }

    return pluginZips.firstOrNull()
      ?: fail { "No ru-bizgen-*.zip in $distDir. Run ':ru-bizgen-plugin:buildPlugin' first." }
  }
}
