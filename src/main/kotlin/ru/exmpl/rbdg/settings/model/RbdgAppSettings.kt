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
  var actualActions: MutableList<PersistenceActionSetting> = initSettingActions()

  /** Сброс настроек до значений по умолчанию. */
  fun restoreFromDefault() {
    actualActions.clear()
    actualActions.addAll(RbdgDefaultAppSettings.getDefault().actualActions)
  }

  private fun initSettingActions(): MutableList<PersistenceActionSetting> {
    val actions = getRbdgService<GeneratorActionProvider>().getActions()

    return actions.mapIndexed { index, action ->
      PersistenceActionSetting(
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
  data class PersistenceActionSetting(
    override var id: String = "",
    override var position: Int = 0,
    override var description: String = "",
    override var active: Boolean = true
  ) : ActionSettingsView

  /** Отражение настроек для конкретного действия. */
  interface ActionSettingsView {

    /** Идентификатор действия. */
    val id: String

    /** Позиция в общем списке действий. */
    val position: Int

    /** Описание. */
    val description: String

    /** Признак, что действие активное. */
    val active: Boolean
  }

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