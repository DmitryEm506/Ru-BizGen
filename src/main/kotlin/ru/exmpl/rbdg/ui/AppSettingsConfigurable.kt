package ru.exmpl.rbdg.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.Disposer
import org.jetbrains.annotations.Nls
import ru.exmpl.rbdg.ui.AppSettingsComponent
import javax.swing.JComponent

/**
 * Основной UI компонент, описывающий блок настроек для плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsConfigurable : Configurable, Disposable {

  lateinit var settingsComponent: AppSettingsComponent private set

  @Nls(capitalization = Nls.Capitalization.Title)
  override fun getDisplayName(): String {
    return SETTINGS_DISPLAY_NAME
  }

  override fun createComponent(): JComponent? {
    settingsComponent = AppSettingsComponent()
    return settingsComponent.getPanel()
  }

  override fun isModified(): Boolean = false

  override fun apply() = Unit

  override fun reset() = Unit

  override fun disposeUIResources() {
    Disposer.dispose(this)
  }

  override fun dispose() = Unit

  private companion object {
    const val SETTINGS_DISPLAY_NAME: String = "Ru Business Data Generator"
  }
}