package ru.exmpl.rbdg.settings.model

import ru.exmpl.rbdg.deepCopyByJson

/**
 * Основной конфиг плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class RbdgAppSettings {

  /** Режим уведомления. */
  var notificationMode: RbdgNotificationMode = RbdgNotificationMode.DISABLE

  /** Актуальный список действий. */
  var actualActions: MutableList<RbdgGeneratorActionSettings> = listOf(
    RbdgGeneratorActionSettings("1", 0, "test_0", active = true),
    RbdgGeneratorActionSettings("2", 1, "test_1", active = true),
    RbdgGeneratorActionSettings("3", 2, "test_2", active = true),
    RbdgGeneratorActionSettings("4", 3, "test_3", active = true),
  ).toMutableList()


  /** Сброс настроек до значений по умолчанию. */
  fun restoreFromDefault() {
    actualActions.clear()
    actualActions.addAll(RbdgDefaultAppSettings.getDefault().actualActions)
  }
}

/**
 * Значение конфига по умолчанию.
 *
 * @author Dmitry_Emelyanenko
 */
internal object RbdgDefaultAppSettings {
  private val DEFAULT: RbdgAppSettings = RbdgAppSettings()


  /**
   * Получение конфига по умолчанию.
   *
   * Применяется подход с копированием объекта, чтобы исключить мутацию его по ссылке.
   *
   * @return клон конфигурации по умолчанию
   */
  fun getDefault(): RbdgAppSettings {
    return DEFAULT.deepCopyByJson()
  }
}