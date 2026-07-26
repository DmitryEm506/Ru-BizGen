package ru.eda.plgn.bizgen.plugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.junit5.RunInEdt
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.moduleFixture
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.junit5.fixture.sourceRootFixture
import com.intellij.testFramework.replaceService
import io.mockk.mockk
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.plugin.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.plugin.di.BizGenService
import java.lang.reflect.Proxy
import java.util.concurrent.CopyOnWriteArrayList

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

  /**
   * Подавляет uncaught-исключения, связанные с Vue LSP Server в IntelliJ 2026.1.
   *
   * В IntelliJ IDEA 2026.1 bundled Vue-плагин использует модульную структуру каталогов
   * (lib/modules), которую PluginManagerCore не может корректно обработать. Это приводит
   * к фоновым ошибкам инициализации VueLspServerSupportProvider, которые
   * TestUncaughtExceptionHandler помечает как uncaught-исключения и валирует тесты.
   *
   * Метод заменяет внутренний список uncaughtExceptions в TestUncaughtExceptionHandler
   * на фильтрующий proxy, который отклоняет исключения, связанные с Vue LSP.
   */
  fun suppressVueLspUncaughtExceptions() {
    val handler = Thread.getDefaultUncaughtExceptionHandler() ?: return

    val handlerClassName = "com.intellij.testFramework.junit5.impl.TestUncaughtExceptionHandler"
    if (handler.javaClass.name != handlerClassName) return

    try {
      val field = handler.javaClass.getDeclaredField("uncaughtExceptions")
      field.isAccessible = true

      val originalList = field.get(handler) as? List<*> ?: return
      val delegate = CopyOnWriteArrayList<Throwable>()

      (originalList as List<Throwable>).forEach { throwable ->
        if (!isVueLspError(throwable)) delegate.add(throwable)
      }

      val concurrentListClass = Class.forName("com.intellij.util.containers.ConcurrentList")
      val proxy = Proxy.newProxyInstance(
        concurrentListClass.classLoader,
        arrayOf(concurrentListClass)
      ) { _, method, args ->
        if (method.name == "add" && args != null && args.size == 1 && args[0] is Throwable) {
          if (isVueLspError(args[0] as Throwable)) {
            return@newProxyInstance false
          }
        }
        if (args == null) method.invoke(delegate) else method.invoke(delegate, *args)
      }

      field.set(handler, proxy)
    } catch (e: Exception) {
      // Игнорируем ошибку рефлексии — тест упадёт по своему сценарию
    }
  }

  private fun isVueLspError(throwable: Throwable): Boolean {
    var current: Throwable? = throwable
    while (current != null) {
      val message = current.message ?: ""
      if (message.contains("VueLspServer") ||
        message.contains("vuejs-plugin") ||
        message.contains("org.jetbrains.plugins.vue")
      ) {
        return true
      }
      current = current.cause
    }
    return false
  }

  internal object TestGeneratorAction : BaseGeneratorAction<String>(
    id = "test.generator",
    name = "Test Generator",
    generator = TestGenerator(),
    category = GeneratorCategory.TECHNICAL,
    detailedDescription = "Test generator",
    example = "TEST",
  )

  internal object TestGeneratorEmptyAction : BaseGeneratorAction<String>(
    id = "test.generator.empty",
    name = "Test Generator.empty",
    generator = TestEmptyGenerator(),
    category = GeneratorCategory.TECHNICAL,
    detailedDescription = "Test empty generator",
    example = "",
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