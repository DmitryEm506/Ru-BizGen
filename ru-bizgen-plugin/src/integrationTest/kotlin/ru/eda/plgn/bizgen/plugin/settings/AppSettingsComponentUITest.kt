package ru.eda.plgn.bizgen.plugin.settings

import com.intellij.driver.sdk.ui.components.elements.checkBox
import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.shouldBe
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.finder_ext.inplaceButtonByAccessibleName
import ru.eda.plgn.bizgen.plugin.base.finder_ext.radioButtonByVisibleText
import java.nio.file.Path
import javax.swing.JCheckBox
import javax.swing.JList
import kotlin.time.Duration.Companion.seconds

/**
 * UI integration-тесты для [ru.eda.plgn.bizgen.plugin.ui.AppSettingsComponent].
 *
 * Тестирует композитный компонент настроек плагина в Settings → Tools → Ru BizGen через Driver SDK:
 * - **splitter composition:** `OnePixelSplitter` делит панель на левую (список генераторов) и
 *   правую (настройки уведомлений + предпросмотр) — верифицируется через одновременное присутствие
 *   дочерних компонентов из обеих панелей;
 * - **panel layout:** `FormBuilder` в `createNotifyToPreviewPanel` вертикально стекает
 *   `NotificationAndClipboardSettingsComponent` и `ActionResultPreviewComponent` — верифицируется
 *   через cross-panel взаимодействие (выбор генератора в левой панели → рендер preview в правой)
 *   и совместное функционирование notification + preview в правой панели;
 * - **dispose:** `AppSettingsComponent.dispose() = Unit`, фактическое освобождение `previewEditor`
 *   через `Disposer.register(this, ActionResultPreviewComponent)` → каскадный dispose при
 *   `disposeUIResources()`. Неявно верифицируется каждым тестом при закрытии IDE
 *   (`useDriverAndCloseIde` → IDE shutdown → `disposeUIResources` → `Disposer.dispose`).
 *   Отдельный UI-сценарий для `dispose` не требуется — если dispose содержит баг (утечка/исключение),
 *   CIServer DI-перехватчик в `BaseUIIntegrationTest` поймает failure.
 *
 * @author Dmitry_Emelyanenko
 */
internal class AppSettingsComponentUITest : RuBizGenSettingsUITest() {

  /**
   * Splitter composition smoke: все дочерние компоненты из обеих панелей `OnePixelSplitter`
   * отображаются одновременно.
   *
   * - Левая панель (`AppActionsSettingComponent`): список генераторов (`JList`) с элементами.
   * - Правая панель верх (`NotificationAndClipboardSettingsComponent`): radio «Выключено» + checkbox.
   * - Правая панель низ (`ActionResultPreviewComponent`): label «Дистанция уникальности» + кнопка «Generate».
   *
   * Доказывает: `OnePixelSplitter` + `FormBuilder` композиция корректна — все три суб-компонента
   * отрендерены в едином settings view. Если splitter или FormBuilder сломан, часть компонентов
   * будет отсутствовать.
   */
  @Test
  internal fun `Should Driver UI - display composite settings with all sub-components from both splitter panels`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("compositeSmoke", projectDir) {
      // Левая панель (AppActionsSettingComponent): список генераторов
      val actionList = list { byType(JList::class.java) }
      actionList.shouldBe("Generator list should be present in left splitter panel", present)
      assertTrue(actionList.items.isNotEmpty(), "Generator list should contain items in left panel")

      // Правая панель верх (NotificationAndClipboardSettingsComponent): radio + checkbox
      val disableRadio = radioButtonByVisibleText("Выключено")
      disableRadio.shouldBe("Notification radio should be present in right panel", present)

      val clipboardCheckbox = checkBox { byType(JCheckBox::class.java) }
      clipboardCheckbox.shouldBe("Clipboard checkbox should be present in right panel", present)

      // Правая панель низ (ActionResultPreviewComponent): label + Generate button
      waitOneText(
        "Distance label should be present in preview section of right panel",
        15.seconds,
      ) { it.text == "Дистанция уникальности" }

      val generateButton = inplaceButtonByAccessibleName("Generate")
      generateButton.shouldBe("Generate button should be present in preview section of right panel", present)
    }
  }

  /**
   * Cross-panel interaction: выбор генератора в левой панели (список) → рендер результата
   * в правой панели (preview editor).
   *
   * `clickItemAtIndex(0)` в левой панели → `BizGenSelectedActionEvent.publish` →
   * `ActionResultPreviewComponent` (правая панель) обновляет `previewEditor`.
   *
   * Доказывает: `OnePixelSplitter` композиция не нарушает event flow — событие выбора
   * генератора корректно публикуется и обрабатывается preview-компонентом в другой панели.
   */
  @Test
  internal fun `Should Driver UI - verify cross-panel interaction via splitter composition`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("crossPanelInteraction", projectDir) {
      val actionList = list { byType(JList::class.java) }

      // Выбор генератора в левой панели
      actionList.clickItemAtIndex(0)

      // Preview рендерится в правой панели — доказывает, что splitter не нарушает event flow
      waitOneContainsText(
        "Общая длина вставляемого текста",
        "Preview should render in right panel after selecting generator in left panel",
        timeout = 15.seconds,
      )
    }
  }

  /**
   * Right panel stacking: переключение notification radio (правая панель верх) не нарушает
   * функционирование preview (правая панель низ).
   *
   * 1. Click HINT radio → `shouldBe` selected (polling, EDT race fix).
   * 2. Select generator в левой панели (list).
   * 3. Preview рендерится в правой панели низ.
   *
   * Доказывает: `FormBuilder` в `createNotifyToPreviewPanel` корректно стекает
   * `NotificationAndClipboardSettingsComponent` и `ActionResultPreviewComponent` —
   * взаимодействие с notification-секцией не interferes с preview-секцией.
   */
  @Test
  internal fun `Should Driver UI - verify notification and preview coexist in right panel`(@TempDir projectDir: Path) {
    workWithRuBizGenSettings("rightPanelStacking", projectDir) {
      // Правая панель верх: переключение notification radio
      val hintRadio = radioButtonByVisibleText("Всплывающее окно")
      hintRadio.click()
      hintRadio.shouldBe("HINT radio should be selected after click in right panel") { isSelected }

      // Левая панель: выбор генератора
      val actionList = list { byType(JList::class.java) }
      actionList.clickItemAtIndex(0)

      // Правая панель низ: preview рендерится — доказывает, что FormBuilder stacking не interferes
      waitOneContainsText(
        "Общая длина вставляемого текста",
        "Preview should render after notification toggle — proves right panel stacking",
        timeout = 15.seconds,
      )
    }
  }
}
