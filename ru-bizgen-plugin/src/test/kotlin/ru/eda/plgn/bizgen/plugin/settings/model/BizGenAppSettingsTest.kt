package ru.eda.plgn.bizgen.plugin.settings.model

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.BizGenNotificationMode

/**
 * Тесты копирования и сброса настроек [BizGenAppSettings].
 *
 * Ключевое свойство: конфиг по умолчанию не должен мутировать при правке полученной из него копии,
 * иначе повторный сброс вернёт уже испорченные значения.
 *
 * Тесты намеренно плоские, без `@Nested`: `BaseIdeaTest` помечен `@RunInEdt`, а EDT-интерцептор
 * платформы не умеет разрешать конструкторы вложенных тест-классов.
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("BizGenAppSettings: копирование и сброс")
internal class BizGenAppSettingsTest : BaseIdeaTest() {

  @Test
  fun `Should copy all scalar fields`() {
    val original = BizGenAppSettings().apply {
      notificationMode = BizGenNotificationMode.BELL
      insToClipboard = false
      escapeChar = "'"
    }

    val copy = original.deepCopy()

    copy.notificationMode shouldBe BizGenNotificationMode.BELL
    copy.insToClipboard shouldBe false
    copy.escapeChar shouldBe "'"
  }

  @Test
  fun `Should create new instances for every action setting`() {
    val original = BizGenAppSettings()
    original.actualActions.shouldNotBeEmpty()

    val copy = original.deepCopy()

    copy.actualActions shouldBe original.actualActions
    copy.actualActions shouldNotBeSameInstanceAs original.actualActions
    copy.actualActions.forEachIndexed { index, setting ->
      setting shouldNotBeSameInstanceAs original.actualActions[index]
    }
  }

  @Test
  fun `Should not propagate changes from the copy back to the original`() {
    val original = BizGenAppSettings()
    val originalFirstName = original.actualActions.first().description

    val copy = original.deepCopy()
    copy.actualActions.first().description = "Изменено в копии"
    copy.actualActions.first().active = false
    copy.escapeChar = ""

    original.actualActions.first().description shouldBe originalFirstName
    original.actualActions.first().active shouldBe true
    original.escapeChar shouldBe "\""
  }

  @Test
  fun `Should restore actions after they were modified`() {
    val settings = BizGenAppSettings()
    val expected = settings.actualActions.map { it.copy() }

    settings.actualActions.first().active = false
    settings.actualActions.removeAt(settings.actualActions.lastIndex)

    settings.restoreFromDefault()

    settings.actualActions shouldBe expected
  }

  @Test
  fun `Should keep defaults intact across repeated resets`() {
    val settings = BizGenAppSettings()

    settings.restoreFromDefault()
    settings.actualActions.first().description = "Испорчено после первого сброса"

    settings.restoreFromDefault()

    settings.actualActions.first().description shouldNotBe "Испорчено после первого сброса"
  }
}
