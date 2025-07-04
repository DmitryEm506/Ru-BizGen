package ru.exmpl.rbdg

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.Disposer
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * AppSettingsConfigurable.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsConfigurable : Configurable, Disposable {

  lateinit var settingsComponent: AppSettingsComponent private set

  @Nls(capitalization = Nls.Capitalization.Title)
  override fun getDisplayName(): String {
    return "Ru Business Data Generator"
  }

  override fun createComponent(): JComponent? {
    settingsComponent = AppSettingsComponent()
    return settingsComponent.getPanel()
  }

  override fun isModified(): Boolean {
    val settings = getRbdgService<AppSettingsStore>().getAppSettings()

    return settingsComponent.getUserNameText() != settings.userId ||
        settingsComponent.getIdeaUserStatus() != settings.ideaStatus
  }

  override fun apply() {
    getRbdgService<AppSettingsStore>().getAppSettings().apply {
      userId = settingsComponent.getUserNameText()
      ideaStatus = settingsComponent.getIdeaUserStatus()
    }
  }

  override fun reset() {
    getRbdgService<AppSettingsStore>().getAppSettings().let {
      settingsComponent.setUserNameText(it.userId)
      settingsComponent.setIdeaUserStatus(it.ideaStatus)
    }
  }

  override fun disposeUIResources() {
    Disposer.dispose(this)
  }

  override fun dispose() = Unit
}