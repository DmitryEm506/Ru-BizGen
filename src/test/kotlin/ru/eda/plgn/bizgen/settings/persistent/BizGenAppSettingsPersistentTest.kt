package ru.eda.plgn.bizgen.settings.persistent

import com.intellij.openapi.Disposable
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.BaseIdeaTest
import ru.eda.plgn.bizgen.actions.GeneratorAction
import ru.eda.plgn.bizgen.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Интеграционные тесты для [BizGenAppSettingsPersistent].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenAppSettingsPersistentTest : BaseIdeaTest() {
  private val underTest = BizGenAppSettingsPersistent()

  @Test
  fun `Should return current state`() {
    val settings = BizGenAppSettings()
    underTest.settings = settings
    
    underTest.state shouldBe settings
  }

  @Test
  fun `Should load state when action IDs match`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    val action = mockk<GeneratorAction<*>>(relaxed = true)

    every { action.id } returns "test-id"
    every { provider.getActions() } returns listOf(action)

    val newSettings = BizGenAppSettings()
    newSettings.actualActions = mutableListOf(PersistenceActionSetting(id = "test-id"))

    underTest.loadState(newSettings)

    underTest.settings shouldBe newSettings
  }

  @Test
  fun `Should restore from default when action IDs do not match`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)

    every { provider.getActions() } returns emptyList()

    val newSettings = object : BizGenAppSettings() {
      var restoreCalled = false
      override fun restoreFromDefault() {
        restoreCalled = true
      }
    }

    newSettings.actualActions = mutableListOf(PersistenceActionSetting(id = "missing-id"))

    underTest.loadState(newSettings)

    newSettings.restoreCalled shouldBe true
    underTest.settings shouldBe newSettings
  }
}
