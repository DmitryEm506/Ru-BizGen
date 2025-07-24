package ru.exmpl.rbdg.settings

import ru.exmpl.rbdg.settings.model.RbdgAppSettings

/**
 * Отвечает за получение настроек.
 *
 * @author Dmitry_Emelyanenko
 */
interface AppSettingsStore {

  /**
   * Получить настройки плагина.
   *
   * @return настройки плагина
   */
  fun getAppSettings(): RbdgAppSettings
}