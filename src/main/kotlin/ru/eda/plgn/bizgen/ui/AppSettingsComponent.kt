package ru.eda.plgn.bizgen.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.OnePixelSplitter
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * Основной компонент настроек плагина.
 *
 * Включает в себя две области, разделенные вертикально:
 * - Левая область - перечень доступных генераторов и их настройка
 * - Правая область - настройки уведомлений и окно пред просмотра работы выбранного генератора
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsComponent : Disposable {
  private val mainPanel = JPanel(BorderLayout())
  private val splitter = OnePixelSplitter(false, SPLITTER_PROPORTION_KEY, DEFAULT_SPLITTER_PROPORTION).apply {
    firstComponent = AppActionsSettingComponent().createComponent()
    secondComponent = createNotifyAndPreviewPanel()
  }

  init {
    mainPanel.add(splitter)
  }

  /**
   * Создание компонента настроек плагина.
   *
   * @return созданный компонент
   */
  fun createComponent(): JPanel = mainPanel

  private fun createNotifyAndPreviewPanel(): JPanel = FormBuilder.createFormBuilder()
    .addComponent(NotificationAndClipboardSettingsComponent().createComponent())
    .addComponentFillVertically(JPanel(), 0)
    .addComponent(
      ActionResultPreviewComponent()
        .also { Disposer.register(this, it) }.rootComponent
    ).panel

  /** Освобождение ресурса. */
  override fun dispose() = Unit

  private companion object {

    const val SPLITTER_PROPORTION_KEY = "ru.eda.plgn.bizgen.AppSettingsComponent"

    const val DEFAULT_SPLITTER_PROPORTION = 0.25f
  }
}