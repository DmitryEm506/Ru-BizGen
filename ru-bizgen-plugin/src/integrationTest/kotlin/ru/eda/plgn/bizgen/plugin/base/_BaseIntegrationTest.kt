package ru.eda.plgn.bizgen.plugin.base

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.waitForIndicators
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Маркерная аннотация для интеграционных тестов плагина.
 *
 * Совмещает два механизма фильтрации:
 * - [Tag] `"integrationTests"` — для будущей тег-фильтрации на gradle-уровне.
 * - [EnabledIfSystemProperty] `runIntegrationTests=true` — для запуска из IDE через VM options
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
abstract class BaseUIIntegrationTest {

  /**
   * Версия IntelliJ IDEA для тестирования — fallback при ручном запуске из IDE без gradle.
   *
   * При запуске через gradle-задачу `integrationTest` фактическая версия берётся из system property `bizgen.test.ide.version`
   * (пробрасывается из `ideaVersion` в `ru-bizgen-plugin/build.gradle.kts` — та же версия, под которую собирается плагин), см.
   * [resolveTestIdeVersion].
   *
   * Значение по умолчанию `"2026.1"` совпадает с версией сборки плагина (`version=1.12.261` → `ideaVersion="2026.1"`), чтобы ручной запуск
   * шёл на той же версии. Наследники могут переопределить для запуска на другой версии.
   */
  protected open val ideVersion: String = "2026.1"

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

  protected fun runIdea(testName: String, projectDir: Path, runTimeout: Duration = 2.minutes, block: Driver.() -> Unit): IDEStartResult {
    return newContextWithPlugin(testName, projectDir)
      .runIdeWithDriver(runTimeout = runTimeout)
      .useDriverAndCloseIde {
        waitForIndicators(1.minutes)
        block()
      }
  }

  /**
   * Создаёт [IDETestContext] с установленным плагином для теста [testName].
   *
   * Продукт: IntelliJ IDEA **Ultimate** ([IdeProductProvider.IU]).
   *
   * Версия берётся из [resolveTestIdeVersion] — совпадает с версией, под которую собирается плагин (`ideaVersion` в
   * `ru-bizgen-plugin/build.gradle.kts`). IU 2026.1 используется вместо IC, т.к. IC 2026.1 не опубликована в products-releases API (см.
   * KDoc [ideVersion]).
   *
   * @param testName имя тест-кейса (используется Starter'ом для логирования/артефактов)
   * @param projectDir директория проекта, открываемого в тестовой IDE
   * @return настроенный контекст с установленным плагином
   */
  private fun newContextWithPlugin(testName: String, projectDir: Path): IDETestContext {
    val context = Starter.newContext(
      testName = testName,
      TestCase(IdeProductProvider.IU, projectInfo = LocalProjectInfo(projectDir)).withVersion(resolveTestIdeVersion()),
    )
    context.pluginConfigurator.installPluginFromPath(resolvePluginArchivePath())
    return context
  }

  /**
   * Резолвинг версии IntelliJ IDEA IC для тестирования.
   *
   * Приоритет:
   * 1. System property `bizgen.test.ide.version` (выставляется gradle-задачей `integrationTest` из `ideaVersion` в
   *    `ru-bizgen-plugin/build.gradle.kts` — та же версия, под которую собирается плагин).
   * 2. Поле [ideVersion] — fallback для ручного запуска из IDE без gradle.
   *
   * @return версия IDE для тестирования
   */
  private fun resolveTestIdeVersion(): String = System.getProperty("bizgen.test.ide.version") ?: ideVersion

  /**
   * Резолвинг пути к архиву плагина для установки в тестовую IDE.
   *
   * Приоритет:
   * 1. System property `path.to.build.plugin` (выставляется gradle-задачей `integrationTest`, см.
   *    `ru-bizgen-plugin/build.gradle.kts:96-99`).
   * 2. Последний по версии `ru-bizgen-*.zip` в `build/distributions/` — для запуска из IDE после ручного выполнения
   *    `:ru-bizgen-plugin:buildPlugin` без необходимости править VM options.
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
