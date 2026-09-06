package ru.eda.plgn.bizgen.plugin.settings

import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings

/**
 * Отвечает за получение настроек.
 *
 * @author Dmitry_Emelyanenko
 */
interface BizGenAppSettingsRepository : BizGenService {

  /**
   * Получить настройки плагина.
   *
   * @return настройки плагина
   */
  fun settings(): BizGenAppSettings
}