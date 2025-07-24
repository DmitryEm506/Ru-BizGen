package ru.exmpl.rbdg.ui

import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.notification.NotificationSettingsService
import ru.exmpl.rbdg.settings.model.RbdgAppSettings.RbdgNotificationMode
import javax.swing.JComponent

/**
 * AppNotificationComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class NotificationSettingsComponent {

  fun configureComponent(): JComponent {
    val service = getRbdgService<NotificationSettingsService>()
    val chosen = service.getNotificationMode()

    return panel {
      buttonsGroup("Уведомления") {
        RbdgNotificationMode.entries.forEach { mode ->
          row {
            radioButton(mode.description, mode)
              .applyToComponent {
                actionCommand = mode.name
                if (chosen == mode) isSelected = true
              }.actionListener { _, button ->
                service.updateNotificationMode(RbdgNotificationMode.valueOf(button.actionCommand))
              }
          }
        }
      }.bind(service::getNotificationMode, service::updateNotificationMode)
    }
  }
}