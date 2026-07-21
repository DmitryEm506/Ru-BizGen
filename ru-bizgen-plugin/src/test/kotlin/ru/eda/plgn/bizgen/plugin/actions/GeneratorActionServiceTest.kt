package ru.eda.plgn.bizgen.plugin.actions

import com.intellij.openapi.Disposable
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Интеграционные тесты для [GeneratorActionService].
 *
 * @author Dmitry_Emelyanenko
 */
internal class GeneratorActionServiceTest : BaseIdeaTest() {
  private val underTest: GeneratorActionService = GeneratorActionServiceImpl()

  @Test
  fun `Should return active actions based on settings`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    val actionSettingsService = replaceServiceInApp<AppActionSettingsService>(disposable)

    val action1 = createTestAction("id-1", "Action 1")
    val action2 = createTestAction("id-2", "Action 2")
    val action3 = createTestAction("id-3", "Action 3")

    every { provider.getAnActions() } returns listOf(action1, action2, action3)
    every { actionSettingsService.getActiveActionSettings() } returns listOf(
      PersistenceActionSetting(id = "id-1", position = 0, active = true),
      PersistenceActionSetting(id = "id-3", position = 2, active = true)
    )

    val activeActions = underTest.getActiveAnActions()

    activeActions shouldHaveSize 2
    activeActions.map { it.id } shouldContainExactly listOf("id-1", "id-3")
  }

  @Test
  fun `Should return empty list when no active actions`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    val actionSettingsService = replaceServiceInApp<AppActionSettingsService>(disposable)

    every { provider.getAnActions() } returns listOf(createTestAction("id-1", "Action 1"))
    every { actionSettingsService.getActiveActionSettings() } returns emptyList()

    val activeActions = underTest.getActiveAnActions()

    activeActions shouldHaveSize 0
  }

  @Test
  fun `Should return correct count of active actions`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    val actionSettingsService = replaceServiceInApp<AppActionSettingsService>(disposable)

    val actions = (1..5).map { createTestAction("id-$it", "Action $it") }

    every { provider.getAnActions() } returns actions
    every { actionSettingsService.getActiveActionSettings() } returns listOf(
      PersistenceActionSetting(id = "id-1", position = 0, active = true),
      PersistenceActionSetting(id = "id-2", position = 1, active = true),
      PersistenceActionSetting(id = "id-3", position = 2, active = true)
    )

    val activeActions = underTest.getActiveAnActions()

    activeActions shouldHaveSize 3
  }

  @Test
  fun `Should skip actions not found in provider`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    val actionSettingsService = replaceServiceInApp<AppActionSettingsService>(disposable)

    val action1 = createTestAction("id-1", "Action 1")

    every { provider.getAnActions() } returns listOf(action1)
    every { actionSettingsService.getActiveActionSettings() } returns listOf(
      PersistenceActionSetting(id = "id-1", position = 0, active = true),
      PersistenceActionSetting(id = "non-existent", position = 1, active = true)
    )

    val activeActions = underTest.getActiveAnActions()

    activeActions shouldHaveSize 1
    activeActions.first().id shouldBe "id-1"
  }

  @Test
  fun `Should find action by id`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)

    val action1 = createTestAction("id-1", "Action 1")
    val action2 = createTestAction("id-2", "Action 2")

    every { provider.getInfos() } returns listOf(action1, action2)

    val found = underTest.findActionById("id-2")

    found shouldNotBe null
    found!!.id shouldBe "id-2"
    found.name shouldBe "Action 2"
  }

  @Test
  fun `Should return null when action not found by id`(@TestDisposable disposable: Disposable) {
    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)

    every { provider.getInfos() } returns listOf(createTestAction("id-1", "Action 1"))

    val found = underTest.findActionById("non-existent")

    found shouldBe null
  }

  private class TestGenerator : Generator<String> {
    override val uniqueDistance: Int = 1_000
    override fun generate(): GeneratorResult<String> = GeneratorResult(toEditor = "TEST", toClipboard = "TEST")
  }

  private fun createTestAction(id: String, name: String): BaseGeneratorAction<String> =
    object : BaseGeneratorAction<String>(
      id = id,
      name = name,
      generator = TestGenerator(),
      category = GeneratorCategory.TECHNICAL,
      detailedDescription = "Test action",
      example = "TEST",
    ) {}
}
