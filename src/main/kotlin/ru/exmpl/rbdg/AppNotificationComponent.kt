package ru.exmpl.rbdg

import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * AppNotificationComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class NotificationSettingsComponent {

  fun configureComponent(): JComponent {
    val service = getRbdgService<AppSettingsNotificationService>()
    val chosen = service.getNotificationMode()

    return panel {
      buttonsGroup("Уведомления") {
        NotificationMode.entries.forEach { mode ->
          row {
            radioButton(mode.description, mode)
              .applyToComponent {
                actionCommand = mode.name
                if (chosen == mode) isSelected = true
              }.actionListener { _, button ->
                service.updateNotificationMode(NotificationMode.valueOf(button.actionCommand))
              }
          }
        }
      }.bind(service::getNotificationMode, service::updateNotificationMode)
    }
  }
}