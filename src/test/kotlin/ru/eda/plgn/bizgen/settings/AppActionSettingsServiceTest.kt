package ru.eda.plgn.bizgen.settings

import com.intellij.openapi.Disposable
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.BaseIdeaTest
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Интеграционные тесты для [AppActionSettingsService].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AppActionSettingsServiceTest : BaseIdeaTest() {
  private val underTest: AppActionSettingsService = AppActionSettingsServiceImpl()

  @TestFactory
  fun `Should find action by position`(@TestDisposable disposable: Disposable) =
    tests(listOf(0, 1), { "Find by position: $it" }) { position ->
      val (settings, _) = configureMocks(disposable)
      val action = PersistenceActionSetting(id = "id-$position", position = position)

      every { settings.actualActions } returns mutableListOf(action)

      underTest.findByPosition(position) shouldBe action
    }

  @Test
  fun `Should change activity of an action`(@TestDisposable disposable: Disposable) {
    val (settings, _) = configureMocks(disposable)
    val action = PersistenceActionSetting(id = "test", position = 0, active = false)

    every { settings.actualActions } returns mutableListOf(action)

    underTest.changeActivity(0, true)

    action.active shouldBe true
  }

  @TestFactory
  fun `Should move action in specified direction`(@TestDisposable disposable: Disposable) =
    tests(
      listOf(
        Triple(0, AppActionSettingsService.Direction.DOWN, true),
        Triple(1, AppActionSettingsService.Direction.UP, true),
        Triple(0, AppActionSettingsService.Direction.UP, false),
        Triple(1, AppActionSettingsService.Direction.DOWN, false)
      ),
      { "Move from ${it.first} to ${it.second}, expected success: ${it.third}" }
    ) { (pos, dir, success) ->
      val (settings, _) = configureMocks(disposable)
      val action0 = PersistenceActionSetting(id = "0", position = 0)
      val action1 = PersistenceActionSetting(id = "1", position = 1)

      every { settings.actualActions } returns mutableListOf(action0, action1)

      underTest.moveTo(pos, dir) shouldBe success

      if (success) {
        if (pos == 0) {
          action0.position shouldBe 1
          action1.position shouldBe 0
        } else {
          action0.position shouldBe 1
          action1.position shouldBe 0
        }
      }
    }

  @Test
  fun `Should return all action settings sorted by position`(@TestDisposable disposable: Disposable) {
    val (settings, _) = configureMocks(disposable)
    val action0 = PersistenceActionSetting(id = "0", position = 0)
    val action1 = PersistenceActionSetting(id = "1", position = 1)

    every { settings.actualActions } returns mutableListOf(action1, action0)

    underTest.getActionSettings() shouldContainExactly listOf(action0, action1)
  }

  @Test
  fun `Should return only active action settings`(@TestDisposable disposable: Disposable) {
    val (settings, _) = configureMocks(disposable)
    val action0 = PersistenceActionSetting(id = "0", position = 0, active = true)
    val action1 = PersistenceActionSetting(id = "1", position = 1, active = false)

    every { settings.actualActions } returns mutableListOf(action0, action1)

    underTest.getActiveActionSettings() shouldContainExactly listOf(action0)
  }

  @Test
  fun `Should restore settings to default`(@TestDisposable disposable: Disposable) {
    val repository = replaceServiceInApp<BizGenAppSettingsRepository>(disposable)
    val settings = object : BizGenAppSettings() {
      var restoreCalled = false
      override fun restoreFromDefault() {
        restoreCalled = true
      }
    }

    every { repository.settings() } returns settings

    underTest.restoreByDefault()

    settings.restoreCalled shouldBe true
  }

  private fun configureMocks(disposable: Disposable): Pair<BizGenAppSettings, BizGenAppSettingsRepository> {
    replaceServiceInApp<ru.eda.plgn.bizgen.actions.GeneratorActionProvider>(disposable).apply {
      every { getActions() } returns emptyList()
    }

    val repository = replaceServiceInApp<BizGenAppSettingsRepository>(disposable)
    val settings = mockk<BizGenAppSettings>(relaxed = true)

    every { repository.settings() } returns settings

    return settings to repository
  }
}
