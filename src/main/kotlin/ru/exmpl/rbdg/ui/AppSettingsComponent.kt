package ru.exmpl.rbdg.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.OnePixelSplitter
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * AppSettingsComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsComponent : Disposable {
  private var myMainPanel: JPanel = JPanel(BorderLayout())

  init {
    var splitter = OnePixelSplitter(false, SPLITTER_PROPORTION_KEY, DEFAULT_SPLITTER_PROPORTION).also(myMainPanel::add)
    splitter.firstComponent = AppActionsSettingComponent().getComponent()

    splitter.secondComponent = FormBuilder.createFormBuilder()
      .addComponent(NotificationSettingsComponent().configureComponent())
      .addComponentFillVertically(JPanel(), 0)
      .addComponent(
        ActionResultPreviewComponent()
        .also { Disposer.register(this, it) }.rootComponent
      )
      .panel
  }

  fun getPanel(): JPanel? {
    return myMainPanel
  }


  override fun dispose() = Unit

  companion object {
    /** The key to store the user's last-used splitter proportion under. */
    const val SPLITTER_PROPORTION_KEY = "ru.exmpl.rbdg.AppSettingsComponent"

    /** The default proportion of the splitter component. */
    const val DEFAULT_SPLITTER_PROPORTION = .25f
  }
}