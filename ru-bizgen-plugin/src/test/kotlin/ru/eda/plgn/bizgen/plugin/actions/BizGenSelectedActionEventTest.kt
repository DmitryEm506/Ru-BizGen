package ru.eda.plgn.bizgen.plugin.actions

import com.intellij.openapi.Disposable
import com.intellij.testFramework.PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import java.util.concurrent.atomic.AtomicReference

/**
 * Интеграционные тесты для [BizGenSelectedActionEvent].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenSelectedActionEventTest : BaseIdeaTest() {

  @TestFactory
  fun `Should publish and receive selected action`(@TestDisposable disposable: Disposable) =
    tests(listOf(mockk<GeneratorInfo<*>>()), { "Publish action: $it" }) { action ->
      val receivedAction = AtomicReference<GeneratorInfo<*>>()

      BizGenSelectedActionEvent.subscribeAsync(disposable) { generator ->
        receivedAction.set(generator)
      }

      BizGenSelectedActionEvent.publish(action)

      dispatchAllInvocationEventsInIdeEventQueue()

      receivedAction.get() shouldBe action
    }

  @Test
  fun `Should handle multiple publications`(@TestDisposable disposable: Disposable) {
    val receivedActions = mutableListOf<GeneratorInfo<*>>()
    val action1 = mockk<GeneratorInfo<*>>()
    val action2 = mockk<GeneratorInfo<*>>()

    BizGenSelectedActionEvent.subscribeAsync(disposable) { generator ->
      receivedActions.add(generator)
    }

    BizGenSelectedActionEvent.publish(action1)
    BizGenSelectedActionEvent.publish(action2)

    dispatchAllInvocationEventsInIdeEventQueue()

    receivedActions shouldBe listOf(action1, action2)
  }
}
