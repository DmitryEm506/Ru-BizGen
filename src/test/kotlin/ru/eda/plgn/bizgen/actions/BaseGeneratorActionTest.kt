package ru.eda.plgn.bizgen.actions

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.testFramework.PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.testFramework.junit5.fixture.editorFixture
import com.intellij.testFramework.junit5.fixture.psiFileFixture
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.BaseIdeaTest
import ru.eda.plgn.bizgen.clipboard.BizGenClipboardSettingsServiceView
import ru.eda.plgn.bizgen.notification.NotificationCtx
import ru.eda.plgn.bizgen.notification.NotificationService

/**
 * Интеграционные тесты для [BaseGeneratorAction].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BaseGeneratorActionTest : BaseIdeaTest() {
  private val fileFixture = sourceRootFixture.psiFileFixture("test.txt", "ABC")
  private val editorFixture = fileFixture.editorFixture()

  @Test
  fun `Should inserts generated text without notifications`(@TestDisposable disposable: Disposable) {
    val editor = editorFixture.get()
    val project = projectFixture.get()
    val (clipboardService, notificationService) = configureProject(disposable)

    every { clipboardService.needToIns() } returns false

    callAction(editor, project)

    editor.document.text shouldBe "TESTABC"
    notificationService.count shouldBe 0
  }

  @Test
  fun `Should inserts generated text with notifications`(@TestDisposable disposable: Disposable) {
    val editor = editorFixture.get()
    val project = projectFixture.get()
    val (clipboardService, notificationService) = configureProject(disposable)

    every { clipboardService.needToIns() } returns true

    callAction(editor, project)

    editor.document.text shouldBe "TESTABC"
    notificationService.count shouldBe 1
  }

  @Test
  fun `Should not inserts generated text when generator return empty text`(@TestDisposable disposable: Disposable) {
    val editor = editorFixture.get()
    val project = projectFixture.get()
    val (clipboardService, notificationService) = configureProject(disposable)

    every { clipboardService.needToIns() } returns true

    callAction(editor, project, TestGeneratorEmptyAction)

    editor.document.text shouldBe "ABC"
    notificationService.count shouldBe 0
  }

  @Test
  fun `Should return when event not contain editor`(@TestDisposable disposable: Disposable) {
    val editor = editorFixture.get()
    val project = projectFixture.get()
    val (clipboardService, notificationService) = configureProject(disposable)

    every { clipboardService.needToIns() } returns true

    callAction(editor = null, project = project)

    editor.document.text shouldBe "ABC"
    notificationService.count shouldBe 0
  }

  @Test
  fun `Should return when event not contain project`(@TestDisposable disposable: Disposable) {
    val editor = editorFixture.get()
    val (clipboardService, notificationService) = configureProject(disposable)

    every { clipboardService.needToIns() } returns true

    callAction(editor = editor, project = null)

    editor.document.text shouldBe "ABC"
    notificationService.count shouldBe 0
  }

  fun configureProject(disposable: Disposable): Pair<BizGenClipboardSettingsServiceView, FakeNotification> {
    val clipboardSettingsService = replaceServiceInApp<BizGenClipboardSettingsServiceView>(disposable)
    val notificationService = replaceServiceInApp<NotificationService>(FakeNotification(), disposable) as FakeNotification

    return clipboardSettingsService to notificationService
  }

  fun callAction(editor: Editor?, project: Project?, action: BaseGeneratorAction<String> = TestGeneratorAction) {
    val dataContext = SimpleDataContext.builder()
      .add(CommonDataKeys.EDITOR, editor)
      .add(CommonDataKeys.PROJECT, project)
      .build()

    val event = TestActionEvent.createTestEvent(action, dataContext)

    action.actionPerformed(event)

    dispatchAllInvocationEventsInIdeEventQueue()
  }

  internal class FakeNotification(var count: Int = 0) : NotificationService {
    override fun sendNotification(ctx: NotificationCtx<*>) {
      count++
    }
  }
}