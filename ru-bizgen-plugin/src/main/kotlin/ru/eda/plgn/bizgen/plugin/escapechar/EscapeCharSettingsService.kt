package ru.eda.plgn.bizgen.plugin.escapechar

import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.BizGenAppSettingsRepository

/**
 * Сервис для управления настройкой символа обрамления при вставке значения в редактор.
 *
 * @author Dmitry_Emelyanenko
 */
interface EscapeCharSettingsService : EscapeCharSettingsServiceView {

  /**
   * Изменение символа обрамления.
   *
   * @param escapeChar символ обрамления
   */
  fun setEscapeChar(escapeChar: String)
}

/**
 * Сервис получения настройки символа обрамления.
 *
 * @author Dmitry_Emelyanenko
 */
interface EscapeCharSettingsServiceView : BizGenService {

  /**
   * Получение текущего символа обрамления.
   *
   * @return символ обрамления
   */
  fun getEscapeChar(): String
}

internal class EscapeCharSettingsServiceImpl : EscapeCharSettingsService {
  override fun setEscapeChar(escapeChar: String) {
    getBizGenService<BizGenAppSettingsRepository>().settings().escapeChar = escapeChar
  }

  override fun getEscapeChar(): String {
    return getBizGenService<BizGenAppSettingsRepository>().settings().escapeChar
  }
}
