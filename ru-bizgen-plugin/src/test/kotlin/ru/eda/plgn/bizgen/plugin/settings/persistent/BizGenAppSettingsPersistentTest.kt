package ru.eda.plgn.bizgen.plugin.settings.persistent

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.JDOMUtil
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.util.xmlb.XmlSerializer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.actions.GeneratorAnAction
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.PersistenceActionSetting
import java.nio.file.Files
import java.nio.file.Path

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
  fun `Should restore from default when updater throw exception`(@TestDisposable disposable: Disposable) {
    // given
    underTest.settings = BizGenAppSettings().also { it.restoreFromDefault() }
    val newSettings = BizGenAppSettings().apply { actualActions = mutableListOf(PersistenceActionSetting(id = "test-id")) }

    // and
    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    every { updater.softUpdateActions(any()) } throws Exception("Ops when update actions")

    // when
    underTest.loadState(newSettings)

    // then
    underTest.settings shouldBe newSettings
  }

  @Test
  fun `Should load state when action IDs match`(@TestDisposable disposable: Disposable) {
    // given
    val storedActions = mutableListOf<PersistenceActionSetting>()
    underTest.settings = BizGenAppSettings().also { it.actualActions = storedActions }
    val newActions = mutableListOf(PersistenceActionSetting(id = "test-id"))

    // and
    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    every { updater.softUpdateActions(storedActions) } returns newActions

    // when
    underTest.loadState(underTest.settings)

    // then
    underTest.settings.actualActions shouldBe newActions
  }

  @Test
  @Suppress("LocalVariableName")
  fun `Should deserialized correctly xml settings version 1_10`() {
    val settingsV1_10 = loadSettingsFromFile("bizgen_plugin_settings_v1_10.xml")

    settingsV1_10 shouldNotBe null
  }

  fun loadSettingsFromFile(fileName: String): BizGenAppSettings {
    return Files.readString(Path.of("src/test/resources/settings/$fileName"))
      .let(JDOMUtil::load)
      .let { element -> XmlSerializer.deserialize(element, BizGenAppSettings::class.java) }
  }

  @Test
  fun `Should apply custom names to actions on loadState`(@TestDisposable disposable: Disposable) {
    val action = TestGeneratorAction
    action.templatePresentation.text = action.name

    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    every { provider.getAnActions() } returns listOf<GeneratorAnAction>(action)
    every { provider.getInfos() } returns listOf(action)

    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    val settings = BizGenAppSettings().apply {
      actualActions = mutableListOf(
        PersistenceActionSetting(
          id = action.id,
          position = 0,
          description = action.name,
          active = true,
          customName = "My Custom Generator",
        )
      )
    }
    every { updater.softUpdateActions(any()) } returns settings.actualActions

    underTest.loadState(settings)

    action.templatePresentation.text shouldBe "My Custom Generator"
  }

  @Test
  fun `Should not change action name when custom name is blank`(@TestDisposable disposable: Disposable) {
    val action = TestGeneratorAction
    action.templatePresentation.text = action.name

    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    every { provider.getAnActions() } returns listOf<GeneratorAnAction>(action)
    every { provider.getInfos() } returns listOf(action)

    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    val settings = BizGenAppSettings().apply {
      actualActions = mutableListOf(
        PersistenceActionSetting(
          id = action.id,
          position = 0,
          description = action.name,
          active = true,
          customName = "",
        )
      )
    }
    every { updater.softUpdateActions(any()) } returns settings.actualActions

    underTest.loadState(settings)

    action.templatePresentation.text shouldBe action.name
  }

  @Test
  fun `Should reset action name to default when custom name was set but is now blank`(@TestDisposable disposable: Disposable) {
    val action = TestGeneratorAction
    action.templatePresentation.text = "Old Custom Name"

    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    every { provider.getAnActions() } returns listOf<GeneratorAnAction>(action)
    every { provider.getInfos() } returns listOf(action)

    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    val settings = BizGenAppSettings().apply {
      actualActions = mutableListOf(
        PersistenceActionSetting(
          id = action.id,
          position = 0,
          description = action.name,
          active = true,
          customName = "",
        )
      )
    }
    every { updater.softUpdateActions(any()) } returns settings.actualActions

    underTest.loadState(settings)

    action.templatePresentation.text shouldBe action.name
  }

  @Test
  fun `Should not fail when custom name references unknown action`(@TestDisposable disposable: Disposable) {
    val action = TestGeneratorAction
    action.templatePresentation.text = action.name

    val provider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    every { provider.getAnActions() } returns listOf<GeneratorAnAction>(action)
    every { provider.getInfos() } returns listOf(action)

    val updater = replaceServiceInApp<BizGenAppSettingsSoftUpdater>(disposable)
    val settings = BizGenAppSettings().apply {
      actualActions = mutableListOf(
        PersistenceActionSetting(
          id = "non-existent-id",
          position = 0,
          description = "Unknown",
          active = true,
          customName = "Ghost Name",
        )
      )
    }
    every { updater.softUpdateActions(any()) } returns settings.actualActions

    underTest.loadState(settings)

    action.templatePresentation.text shouldBe action.name
  }
}
