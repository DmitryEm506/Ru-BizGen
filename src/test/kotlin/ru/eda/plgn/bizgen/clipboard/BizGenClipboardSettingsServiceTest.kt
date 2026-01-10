package ru.eda.plgn.bizgen.clipboard

import com.intellij.openapi.Disposable
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseIdeaTest
import ru.eda.plgn.bizgen.settings.BizGenAppSettingsRepository
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings

/**
 * Интеграционные тесты для [BizGenClipboardSettingsService].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenClipboardSettingsServiceTest : BaseIdeaTest() {
  private val underTest: BizGenClipboardSettingsService = BizGenClipboardSettingsServiceImpl()

  @TestFactory
  fun `Should return current clipboard setting`(@TestDisposable disposable: Disposable) =
    tests(listOf(true, false), { "Clipboard setting: $it" }) { flag ->
      val repository = replaceServiceInApp<BizGenAppSettingsRepository>(disposable)
      val settings = mockk<BizGenAppSettings>()

      every { repository.settings() } returns settings
      every { settings.insToClipboard } returns flag

      underTest.needToIns() shouldBe flag
    }

  @TestFactory
  fun `Should update clipboard setting`(@TestDisposable disposable: Disposable) =
    tests(listOf(true, false), { "Clipboard setting: $it" }) { flag ->
      val repository = replaceServiceInApp<BizGenAppSettingsRepository>(disposable)
      val settings = mockk<BizGenAppSettings>(relaxed = true)

      every { repository.settings() } returns settings

      underTest.setClipboardSetting(flag)

      verify { settings.insToClipboard = flag }
    }
}
