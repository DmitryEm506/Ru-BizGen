package ru.eda.plgn.bizgen.plugin.ui

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import ru.eda.plgn.bizgen.plugin.clipboard.BizGenClipboardSettingsService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.escapechar.EscapeCharSettingsService
import ru.eda.plgn.bizgen.plugin.notification.NotificationSettingsService
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService
import ru.eda.plgn.bizgen.plugin.settings.BizGenAppSettingsRepository
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings
import java.awt.Container
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JRadioButton

/**
 * UI тесты для компонентов настроек плагина.
 *
 * Тестирует создание UI компонентов, наличие элементов управления
 * и интеграцию UI с сервисами настроек.
 *
 * @author Dmitry_Emelyanenko
 */
internal class AppSettingsUiTest : BaseIdeaTest() {

  @BeforeEach
  fun restoreDefaults() {
    getBizGenService<BizGenAppSettingsRepository>().settings().restoreFromDefault()
  }

  @Test
  fun `Should create and dispose settings component`() {
    val configurable = AppSettingsConfigurable()

    configurable.displayName shouldBe "Ru BizGen"

    val component = configurable.createComponent()
    component shouldNotBe null

    configurable.isModified() shouldBe false

    configurable.disposeUIResources()
  }

  @Test
  fun `Should dispose settings component idempotently`() {
    val configurable = AppSettingsConfigurable()

    configurable.createComponent()
    configurable.disposeUIResources()
    configurable.disposeUIResources()
  }

  @Test
  fun `Should have escape char radio buttons in notification component`() {
    val component = NotificationAndClipboardSettingsComponent().createComponent()

    val radioButtons = findComponents(component, JRadioButton::class.java)

    radioButtons shouldHaveSize 6
  }

  @Test
  fun `Should have clipboard checkbox in notification component`() {
    val component = NotificationAndClipboardSettingsComponent().createComponent()

    val checkboxes = findComponents(component, JCheckBox::class.java)

    checkboxes shouldHaveSize 1
  }

  @Test
  fun `Should have notification mode radio buttons in notification component`() {
    val component = NotificationAndClipboardSettingsComponent().createComponent()

    val radioButtons = findComponents(component, JRadioButton::class.java)

    radioButtons shouldHaveSize 6
  }

  @Test
  fun `Should update escape char setting through service`() {
    val escapeCharService = getBizGenService<EscapeCharSettingsService>()
    val repository = getBizGenService<BizGenAppSettingsRepository>()

    escapeCharService.setEscapeChar("'")
    repository.settings().escapeChar shouldBe "'"

    escapeCharService.setEscapeChar("")
    repository.settings().escapeChar shouldBe ""

    escapeCharService.setEscapeChar("\"")
    repository.settings().escapeChar shouldBe "\""
  }

  @Test
  fun `Should have default escape char as double quote`() {
    val escapeCharService = getBizGenService<EscapeCharSettingsService>()

    escapeCharService.getEscapeChar() shouldBe "\""
  }

  @Test
  fun `Should update clipboard setting through service`() {
    val clipboardService = getBizGenService<BizGenClipboardSettingsService>()
    val repository = getBizGenService<BizGenAppSettingsRepository>()

    clipboardService.setClipboardSetting(false)
    repository.settings().insToClipboard shouldBe false

    clipboardService.setClipboardSetting(true)
    repository.settings().insToClipboard shouldBe true
  }

  @Test
  fun `Should have default clipboard setting as true`() {
    val clipboardService = getBizGenService<BizGenClipboardSettingsService>()

    clipboardService.needToIns() shouldBe true
  }

  @Test
  fun `Should update notification mode through service`() {
    val notificationService = getBizGenService<NotificationSettingsService>()
    val repository = getBizGenService<BizGenAppSettingsRepository>()

    notificationService.updateNotificationMode(BizGenAppSettings.BizGenNotificationMode.BELL)
    repository.settings().notificationMode shouldBe BizGenAppSettings.BizGenNotificationMode.BELL

    notificationService.updateNotificationMode(BizGenAppSettings.BizGenNotificationMode.HINT)
    repository.settings().notificationMode shouldBe BizGenAppSettings.BizGenNotificationMode.HINT

    notificationService.updateNotificationMode(BizGenAppSettings.BizGenNotificationMode.DISABLE)
    repository.settings().notificationMode shouldBe BizGenAppSettings.BizGenNotificationMode.DISABLE
  }

  @Test
  fun `Should have default notification mode as disable`() {
    val notificationService = getBizGenService<NotificationSettingsService>()

    notificationService.getNotificationMode() shouldBe BizGenAppSettings.BizGenNotificationMode.DISABLE
  }

  @Test
  fun `Should have all generators in actions settings`() {
    val actionSettingsService = getBizGenService<AppActionSettingsService>()

    val actionSettings = actionSettingsService.getActionSettings()

    actionSettings shouldHaveSize GeneratorInfoProvider.generatorInfos.size
  }

  @Test
  fun `Should rename action and save custom name`() {
    val actionSettingsService = getBizGenService<AppActionSettingsService>()

    val renamed = actionSettingsService.renameAction(0, "Custom Name")

    renamed shouldNotBe null
    renamed!!.customName shouldBe "Custom Name"
    renamed.description shouldBe "Custom Name"

    val saved = actionSettingsService.findByPosition(0)
    saved?.customName shouldBe "Custom Name"
    saved?.description shouldBe "Custom Name"
  }

  @Test
  fun `Should toggle action active state`() {
    val actionSettingsService = getBizGenService<AppActionSettingsService>()

    val original = actionSettingsService.findByPosition(0)
    original shouldNotBe null
    original!!.active shouldBe true

    actionSettingsService.changeActivity(0, false)

    val deactivated = actionSettingsService.findByPosition(0)
    deactivated?.active shouldBe false

    actionSettingsService.changeActivity(0, true)

    val reactivated = actionSettingsService.findByPosition(0)
    reactivated?.active shouldBe true
  }

  @Test
  fun `Should return only active action settings`() {
    val actionSettingsService = getBizGenService<AppActionSettingsService>()

    actionSettingsService.getActiveActionSettings() shouldHaveSize GeneratorInfoProvider.generatorInfos.size

    actionSettingsService.changeActivity(0, false)

    actionSettingsService.getActiveActionSettings() shouldHaveSize GeneratorInfoProvider.generatorInfos.size - 1
  }

  @Test
  fun `Should restore defaults`() {
    val actionSettingsService = getBizGenService<AppActionSettingsService>()

    actionSettingsService.changeActivity(0, false)
    actionSettingsService.renameAction(0, "Changed Name")

    val restored = actionSettingsService.restoreByDefault()

    restored shouldHaveSize GeneratorInfoProvider.generatorInfos.size
    restored.all { it.active } shouldBe true
    restored.all { it.customName.isBlank() } shouldBe true
  }

  private fun <T : JComponent> findComponents(container: Container, type: Class<T>): List<T> {
    val result = mutableListOf<T>()
    for (component in container.components) {
      if (type.isInstance(component)) {
        @Suppress("UNCHECKED_CAST")
        result.add(component as T)
      }
      if (component is Container) {
        result.addAll(findComponents(component, type))
      }
    }
    return result
  }
}
