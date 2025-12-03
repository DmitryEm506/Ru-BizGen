package ru.eda.plgn.bizgen.ui

import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.actionListener
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import ru.eda.plgn.bizgen.clipboard.BizGenClipboardSettingsService
import ru.eda.plgn.bizgen.di.getBizGenService
import ru.eda.plgn.bizgen.notification.NotificationSettingsService
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings.BizGenNotificationMode
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
    }
  }
}