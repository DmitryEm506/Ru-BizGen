package ru.exmpl.rbdg.settings

import ru.exmpl.rbdg.di.RbdgService
import ru.exmpl.rbdg.settings.model.RbdgAppSettings

/**
 * Отвечает за получение настроек.
 *
 * @author Dmitry_Emelyanenko
 */
interface RbdgAppSettingsRepository : RbdgService {

  /**
   * Получить настройки плагина.
   *
   * @return настройки плагина
   */
  fun settings(): RbdgAppSettings
}