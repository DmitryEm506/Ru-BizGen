package ru.person.bizgen.settings

import ru.person.bizgen.di.BizGenService
import ru.person.bizgen.settings.model.BizGenAppSettings

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