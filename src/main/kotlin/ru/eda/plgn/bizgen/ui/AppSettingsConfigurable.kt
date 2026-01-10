package ru.eda.plgn.bizgen.ui

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.Disposer
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Основной UI компонент, описывающий блок настроек для плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsConfigurable : Configurable {
  private var component: AppSettingsComponent? = null

  /**
   * Наименование плагина, которое будет отображаться в секции настроек.
   *
   * @return наименование, отображаемое в секции настроек
   */
  @Nls(capitalization = Nls.Capitalization.Title)
  override fun getDisplayName(): String {
    return SETTINGS_DISPLAY_NAME
  }

  /**
   * Создание основного UI компонента, описывающий блок настроек для плагина.
   *
   * @return UI компонент блока настроек
   */
  override fun createComponent(): JComponent {
    val component = AppSettingsComponent()
    this.component = component
    return component.createComponent()
  }

  /**
   * Признак, что были изменения в компоненте.
   *
   * В текущей парадигме всегда будет возвращаться false, так как все изменения сохраняются сразу.
   *
   * @return true - были изменения.
   */
  override fun isModified(): Boolean = false

  /** Сохраняет значения настроек из Swing-формы в конфигурируемый компонент. Метод вызывается в EDT по запросу пользователя. */
  override fun apply() = Unit

  /**
   * Загружает настройки из конфигурируемого компонента в форму Swing. Метод вызывается в EDT сразу после создания формы или позже по
   * запросу пользователя.
   */
  override fun reset() = Unit

  /** Освобождение UI ресурса. */
  override fun disposeUIResources() {
    component?.let { Disposer.dispose(it) }
    component = null
  }

  private companion object {
    const val SETTINGS_DISPLAY_NAME: String = "Ru BizGen"
  }
}