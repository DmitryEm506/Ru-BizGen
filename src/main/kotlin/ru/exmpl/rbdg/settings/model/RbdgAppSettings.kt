package ru.exmpl.rbdg.settings.model

import ru.exmpl.rbdg.actions.GeneratorActionProvider
import ru.exmpl.rbdg.actions.impl.UuidInKtTestGeneratorAction.Companion.UUID_IN_KT_TEST_GENERATOR_ACTION_ID
import ru.exmpl.rbdg.deepCopyByJson
import ru.exmpl.rbdg.di.getRbdgService

/**
 * Основной конфиг плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class RbdgAppSettings {

  /** Режим уведомления. */
  var notificationMode: RbdgNotificationMode = RbdgNotificationMode.DISABLE

  /** Актуальный список действий. */
  var actualActions: MutableList<ActionSetting> = initSettingActions()

  /** Сброс настроек до значений по умолчанию. */
  fun restoreFromDefault() {
    actualActions.clear()
    actualActions.addAll(RbdgDefaultAppSettings.getDefault().actualActions)
  }

  private fun initSettingActions(): MutableList<ActionSetting> {
    val actions = getRbdgService<GeneratorActionProvider>().getActions()

    return actions.mapIndexed { index, action ->
      ActionSetting(
        id = action.id,
        position = index,
        description = action.name,
        active = UUID_IN_KT_TEST_GENERATOR_ACTION_ID != action.id
      )
    }.toMutableList()
  }

  /**
   * Настройка для конкретного действия.
   *
   * @property id идентификатор действия
   * @property position позиция в общем списке действий
   * @property description описание
   * @property active признак, что действие активное
   * @author Dmitry_Emelyanenko
   */
  data class ActionSetting(
    var id: String = "",
    var position: Int = 0,
    var description: String = "",
    var active: Boolean = true
  )

  /**
   * Режимы уведомлений.
   *
   * @author Dmitry_Emelyanenko
   */
  enum class RbdgNotificationMode(val description: String) {
    BELL("""Лог событий "Колокольчик""""),
    HINT("Всплывающее окно"),
    DISABLE("Выключено"),
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