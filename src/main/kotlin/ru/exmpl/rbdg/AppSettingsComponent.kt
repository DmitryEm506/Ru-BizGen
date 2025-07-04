package ru.exmpl.rbdg

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.apache.commons.lang3.RandomStringUtils
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * AppSettingsComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class AppSettingsComponent : Disposable {
//  private var myMainPanel: JPanel? = null
//  init {
//    myMainPanel = FormBuilder.createFormBuilder()
//      .addLabeledComponent(JBLabel("User name:"), myUserNameText, 1, false)
//      .addComponent(myIdeaUserStatus, 1)
//      .addComponentFillVertically(JPanel(), 0)
//      .panel
//  }

  private var myMainPanel: JPanel = JPanel(BorderLayout())
  private val myUserNameText = JBTextField()
  private val myIdeaUserStatus = JBCheckBox("IntelliJ IDEA user")

  init {
    var splitter = OnePixelSplitter(false, SPLITTER_PROPORTION_KEY, DEFAULT_SPLITTER_PROPORTION).also(myMainPanel::add)
    splitter.firstComponent = AppActionsSettingComponent().getComponent()

    splitter.secondComponent = FormBuilder.createFormBuilder()
      .addLabeledComponent(JBLabel("User name:"), myUserNameText, 1, false)
      .addComponent(myIdeaUserStatus, 1)
      .addComponent(
        ActionResultPreviewComponent({ RandomStringUtils.randomAlphabetic(10) })
        .also { Disposer.register(this, it) }.rootComponent
      )
      .addComponentFillVertically(JPanel(), 0)
      .panel
  }


  fun getPanel(): JPanel? {
    return myMainPanel
  }

  fun getPreferredFocusedComponent(): JComponent {
    return myUserNameText
  }

  fun getUserNameText(): String {
    return myUserNameText.getText()
  }

  fun setUserNameText(newText: String) {
    myUserNameText.setText(newText)
  }

  fun getIdeaUserStatus(): Boolean {
    return myIdeaUserStatus.isSelected
  }

  fun setIdeaUserStatus(newStatus: Boolean) {
    myIdeaUserStatus.setSelected(newStatus)
  }

  override fun dispose() = Unit

  companion object {
    /** The key to store the user's last-used splitter proportion under. */
    const val SPLITTER_PROPORTION_KEY = "ru.exmpl.rbdg.AppSettingsComponent"

    /** The default proportion of the splitter component. */
    const val DEFAULT_SPLITTER_PROPORTION = .25f

    /** Pixels of margin outside the editor panel. */
    const val EDITOR_PANEL_MARGIN = 10
  }
}