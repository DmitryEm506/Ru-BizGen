package ru.eda.plgn.bizgen.notification

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.BalloonBuilder
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseIdeaTest
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings

/**
 * Интеграционные тесты для [NotificationService].
 *
 * @author Dmitry_Emelyanenko
 */
internal class NotificationServiceTest : BaseIdeaTest() {
  private val underTest: NotificationService = NotificationServiceImpl()

  @TestFactory
  fun `Should send notification based on settings`(@TestDisposable disposable: Disposable) =
    tests(
      listOf(
        BizGenAppSettings.BizGenNotificationMode.BELL,
        BizGenAppSettings.BizGenNotificationMode.DISABLE
      ),
      { "Notification mode: $it" }
    ) { mode ->
      val settings = replaceServiceInApp<NotificationSettingsView>(disposable)

      every { settings.getNotificationMode() } returns mode

      val receivedNotifications = mutableListOf<Notification>()
      ApplicationManager.getApplication().messageBus.connect(disposable).subscribe(
        Notifications.TOPIC,
        object : Notifications {
          override fun notify(notification: Notification) {
            receivedNotifications.add(notification)
          }
        }
      )

      val ctx = NotificationCtx(
        actionInfo = NotificationCtx.ActionInfo("id", "Test Action"),
        editor = mockk(relaxed = true),
        result = GeneratorResult(toEditor = "Editor", toClipboard = "Clipboard")
      )

      underTest.sendNotification(ctx)

      if (mode == BizGenAppSettings.BizGenNotificationMode.BELL) {
        receivedNotifications.size shouldBe 1
        receivedNotifications.first().content shouldBe "Clipboard добавлен в буфер"
      } else {
        receivedNotifications.size shouldBe 0
      }
    }

  @Test
  fun `Should show hint notification`(@TestDisposable disposable: Disposable) {
    val settings = replaceServiceInApp<NotificationSettingsView>(disposable)
    every { settings.getNotificationMode() } returns BizGenAppSettings.BizGenNotificationMode.HINT

    val popupFactory = mockk<JBPopupFactory>()
    val builder = mockk<BalloonBuilder>(relaxed = true)
    every { builder.setFadeoutTime(any()) } returns builder

    val balloon = mockk<com.intellij.openapi.ui.popup.Balloon>(relaxed = true)

    mockkStatic(JBPopupFactory::class)
    every { JBPopupFactory.getInstance() } returns popupFactory
    every { popupFactory.createHtmlTextBalloonBuilder(any(), any(), any(), any()) } returns builder
    every { popupFactory.guessBestPopupLocation(any<com.intellij.openapi.editor.Editor>()) } returns mockk(relaxed = true)
    every { builder.createBalloon() } returns balloon

    val ctx = NotificationCtx(
      actionInfo = NotificationCtx.ActionInfo("id", "Test Action"),
      editor = mockk(relaxed = true),
      result = GeneratorResult(toEditor = "Editor", toClipboard = "Clipboard")
    )

    underTest.sendNotification(ctx)

    verify { popupFactory.createHtmlTextBalloonBuilder("Clipboard добавлен в буфер", any(), any(), any()) }
    verify { builder.createBalloon() }

    unmockkStatic(JBPopupFactory::class)
  }
}
