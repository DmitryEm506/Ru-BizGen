package ru.eda.plgn.bizgen

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.junit5.RunInEdt
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.moduleFixture
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.junit5.fixture.sourceRootFixture
import com.intellij.testFramework.replaceService
import io.mockk.mockk
import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.di.BizGenService
import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult

/**
 * Базовая абстракция для интеграционных тестов.
 *
 * @author Dmitry_Emelyanenko
 */
@RunInEdt
@TestApplication
internal abstract class BaseIdeaTest : BaseTest() {

  protected val projectFixture = projectFixture()
  protected val moduleFixture = projectFixture.moduleFixture()
  protected val sourceRootFixture = moduleFixture.sourceRootFixture()

  /**
   * Заменяет на заглушку сервис, который был создан на уровне приложения.
   *
   * @param T тип сервиса для замены
   * @param disposable объект для управления временем жизни сервиса
   * @return замененный сервис (заглушка)
   */
  inline fun <reified T : BizGenService> replaceServiceInApp(disposable: Disposable): T {
    val service = mockk<T>()
    ApplicationManager.getApplication().replaceService(T::class.java, service, disposable)
    return service
  }

  /**
   * Заменяет на переданный сервис, который был создан на уровне приложения.
   *
   * @param T тип сервиса для замены
   * @param service объект сервиса
   * @param disposable объект для управления временем жизни сервиса
   * @return замененный сервис (заглушка)
   */
  inline fun <reified T : BizGenService> replaceServiceInApp(service: T, disposable: Disposable): T {
    ApplicationManager.getApplication().replaceService(T::class.java, service, disposable)
    return service
  }

  internal object TestGeneratorAction : BaseGeneratorAction<String>(
    id = "test.generator",
    name = "Test Generator",
    generator = TestGenerator()
  )

  internal object TestGeneratorEmptyAction : BaseGeneratorAction<String>(
    id = "test.generator.empty",
    name = "Test Generator.empty",
    generator = TestEmptyGenerator()
  )

  private class TestGenerator : Generator<String> {
    override val uniqueDistance: Int = 1_000
    override fun generate(): GeneratorResult<String> = GeneratorResult(toEditor = "TEST", toClipboard = "TEST")
  }

  private class TestEmptyGenerator : Generator<String> {
    override val uniqueDistance: Int = 0
    override fun generate(): GeneratorResult<String> = GeneratorResult(toEditor = "", toClipboard = "")
  }
}