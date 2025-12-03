package ru.eda.plgn.bizgen.settings

import ru.eda.plgn.bizgen.di.BizGenService
import ru.eda.plgn.bizgen.settings.model.BizGenAppSettings

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