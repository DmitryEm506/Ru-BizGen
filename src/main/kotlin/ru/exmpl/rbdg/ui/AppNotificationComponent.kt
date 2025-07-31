package ru.exmpl.rbdg.ui

import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.notification.NotificationSettingsService
import ru.exmpl.rbdg.settings.model.RbdgAppSettings.RbdgNotificationMode
import javax.swing.JComponent

/**
 * Компонент настройки опции уведомления.
 *
 * @author Dmitry_Emelyanenko
 */
class NotificationSettingsComponent {

  /** Компонент настройки опции уведомления. */
  fun createComponent(): JComponent {
    val service = getRbdgService<NotificationSettingsService>()

    var currentMode = service.getNotificationMode()

    return panel {
      buttonsGroup("Уведомления") {
        RbdgNotificationMode.entries.forEach { mode ->
          row {
            radioButton(mode.description, mode)
              .selected(mode == currentMode)  // Устанавливаем начальное состояние
              .actionListener { _, _ -> service.updateNotificationMode(mode) }
          }
        }
      }
        // метод bind не вызывается и сейчас это заглушка, логика сохранения выбранного значения описана в actionListener
        .bind({ currentMode }, { currentMode = it })
    }
  }
}