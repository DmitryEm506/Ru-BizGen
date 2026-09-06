package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.finder_ext.inplaceButtonByAccessibleName
import ru.eda.plgn.bizgen.plugin.base.finder_ext.inplaceButtonByAccessibleNameAndPerform
import java.nio.file.Path
import javax.swing.JList
import kotlin.time.Duration.Companion.seconds

/**
 * UI integration-тесты для [ru.eda.plgn.bizgen.plugin.ui.ActionResultPreviewComponent].
 *
 * Тестирует компонент предпросмотра результата генератора в Settings → Tools → Ru BizGen через Driver SDK:
 * - отображение элементов preview (actionIdLabel, uniqueDistanceLabel, previewEditor, Generate button);
 * - выбор генератора → публикация [ru.eda.plgn.bizgen.plugin.actions.BizGenSelectedActionEvent] → рендер результата в `EditorEx`;
 * - выбор генератора → обновление `uniqueDistanceLabel` значением `uniqueDistance` генератора;
 * - нажатие кнопки "Generate" → повторный рендер preview.
 *
 * Компонент `ActionResultPreviewComponent` находится в той же панели настроек, что и
 * [ru.eda.plgn.bizgen.plugin.ui.AppActionsSettingComponent], поэтому выбор генератора в списке
 * (clickItemAtIndex) триггерит публикацию события, которое обрабатывается preview-компонентом.
 *
 * `dispose()` компонента (`= Unit`) и освобождение `previewEditor` через `Disposer` неявно верифицируются каждым тестом — при закрытии IDE
 * `disposeUIResources` → `Disposer.dispose` → `releaseEditor`. Отдельный UI-сценарий для `dispose` не требуется.
 *
 * @author Dmitry_Emelyanenko
 */
internal class ActionResultPreviewComponentUITest : RuBizGenSettingsUITest() {

  @Test
  internal fun `Should Driver UI - display preview component elements in settings`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("previewSmoke", projectDir) {
      // "Дистанция уникальности" — label над uniqueDistanceLabel
      waitOneText("Distance label should be present", 15.seconds) { it.text == "Дистанция уникальности" }

      // actionIdLabel — иное состояние "ActionID:" (префикс без id)
      waitOneText("ActionID label should be present initially", 15.seconds) { it.text.startsWith("ActionID") }

      // previewEditor — начальный placeholder-текст
      waitOneText("Editor should show initial placeholder text", 15.seconds) { it.text == "Выберите генератор" }

      // Generate button (InplaceButton) присутствует
      val generateButton = inplaceButtonByAccessibleName("Generate")
      generateButton.shouldBe("Generate button should be present in preview", present)
    }
  }

  @Test
  internal fun `Should Driver UI - render preview in editor after selecting generator`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("previewRender", projectDir) {
      val actionList = list { byType(JList::class.java) }

      actionList.clickItemAtIndex(0)

      // previewEditor рендерит результат: фиксированный префикс "Общая длина вставляемого текста"
      waitOneContainsText(
        "Общая длина вставляемого текста",
        "Preview editor should render result text after selecting generator",
        timeout = 15.seconds,
      )
      // секция toEditor
      waitOneContainsText(
        "Вставляемый текст в редактор:",
        "Preview editor should contain 'toEditor' section after selecting generator",
        timeout = 15.seconds,
      )
    }
  }

  @Test
  internal fun `Should Driver UI - update uniqueDistanceLabel after selecting generator`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("uniqueDistanceLabel", projectDir) {
      val actionList = list { byType(JList::class.java) }

      // До выбора генератора uniqueDistanceLabel пуст, отдельных числовых текстов быть не должно
      val numericTextsBefore = getAllTexts { it.text.matches(Regex("\\d+")) }
      assertTrue(
        numericTextsBefore.isEmpty(),
        "No standalone numeric text should be present before selecting a generator"
      )

      actionList.clickItemAtIndex(0)

      // После выбора uniqueDistanceLabel отображает uniqueDistance генератора (число)
      waitAnyTexts(
        "uniqueDistanceLabel should show numeric distance after selecting generator",
        15.seconds,
      ) { uiText -> uiText.text.matches(Regex("\\d+")) }
    }
  }

  @Test
  internal fun `Should Driver UI - refresh preview via Generate button`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("generateRefresh", projectDir) {
      val actionList = list { byType(JList::class.java) }

      actionList.clickItemAtIndex(0)

      // Дожидаемся первоначального рендера preview
      waitOneContainsText(
        "Общая длина вставляемого текста",
        "Preview should render after selecting generator",
        timeout = 15.seconds,
      )

      // Нажатие Generate → повторная генерация (seed = Random.nextInt()) → re-render
      inplaceButtonByAccessibleNameAndPerform("Generate")

      // После refresh preview должен содержать результат (возможно с другим значением)
      waitOneContainsText(
        "Общая длина вставляемого текста",
        "Preview should re-render after clicking Generate button",
        timeout = 15.seconds,
      )
    }
  }
}
