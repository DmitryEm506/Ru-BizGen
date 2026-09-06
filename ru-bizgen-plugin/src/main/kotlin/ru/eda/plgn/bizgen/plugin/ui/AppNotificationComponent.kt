package ru.eda.plgn.bizgen.plugin.ui

import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import ru.eda.plgn.bizgen.plugin.clipboard.BizGenClipboardSettingsService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.escapechar.EscapeCharSettingsService
import ru.eda.plgn.bizgen.plugin.notification.NotificationSettingsService
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.BizGenNotificationMode
import javax.swing.JComponent

/**
 * Компонент настройки опции уведомления и копирования в буфер.
 *
 * @author Dmitry_Emelyanenko
 */
class NotificationAndClipboardSettingsComponent {

  /** Компонент настройки опции уведомления и копирования в буфер. */
  fun createComponent(): JComponent {

    return panel {
      val service = getBizGenService<NotificationSettingsService>()
      var currentMode = service.getNotificationMode()

      buttonsGroup("Уведомления") {
        BizGenNotificationMode.entries.forEach { mode ->
          row {
            radioButton(mode.description, mode)
              .selected(mode == currentMode)  // Устанавливаем начальное состояние
              .actionListener { _, _ -> service.updateNotificationMode(mode) }
          }
        }
      }
        // метод bind не вызывается и сейчас это заглушка, логика сохранения выбранного значения описана в actionListener
        .bind({ currentMode }, { currentMode = it })

      row { label("") }.topGap(TopGap.SMALL)

      row {
        val clipboardSettingsService = getBizGenService<BizGenClipboardSettingsService>()

        label("Результат генератора копировать в буфер")
        checkBox("")
          .actionListener { _, box -> clipboardSettingsService.setClipboardSetting(box.isSelected) }
          .bindSelected(clipboardSettingsService::needToIns, clipboardSettingsService::setClipboardSetting)
      }

      row { label("") }.topGap(TopGap.SMALL)

      val escapeCharService = getBizGenService<EscapeCharSettingsService>()
      var currentChar = escapeCharService.getEscapeChar()

      buttonsGroup("Символ обрамления при вставке в редактор") {
        row {
          radioButton("Двойные кавычки (\")", "\"")
            .selected(currentChar == "\"")
            .actionListener { _, _ -> escapeCharService.setEscapeChar("\"") }
        }
        row {
          radioButton("Одинарные кавычки (')", "'")
            .selected(currentChar == "'")
            .actionListener { _, _ -> escapeCharService.setEscapeChar("'") }
        }
        row {
          radioButton("Без обрамления", "")
            .selected(currentChar == "")
            .actionListener { _, _ -> escapeCharService.setEscapeChar("") }
        }
      }.bind({ currentChar }, { currentChar = it })
    }
  }
}