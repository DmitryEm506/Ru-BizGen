package ru.person.bizgen.settings.model

import ru.person.bizgen.actions.GeneratorActionProvider
import ru.person.bizgen.actions.impl.UuidInKtTestGeneratorAction.Companion.UUID_IN_KT_TEST_GENERATOR_ACTION_ID
import ru.person.bizgen.deepCopyByJson
import ru.person.bizgen.di.getBizGenService

/**
 * Основной конфиг плагина.
 *
 * @author Dmitry_Emelyanenko
 */
class BizGenAppSettings {

  /** Режим уведомления. */
  var notificationMode: BizGenNotificationMode = BizGenNotificationMode.DISABLE

  /** Актуальный список действий. */
  var actualActions: MutableList<PersistenceActionSetting> = initSettingActions()

  /** Признак, что необходимо вставить результат работы генератора в буфер обмена. */
  var insToClipboard: Boolean = true

  /** Сброс настроек до значений по умолчанию. */
  fun restoreFromDefault() {
    actualActions.clear()
    actualActions.addAll(BizGenDefaultAppSettings.getDefault().actualActions)
  }

  private fun initSettingActions(): MutableList<PersistenceActionSetting> {
    val actions = getBizGenService<GeneratorActionProvider>().getActions()

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
  enum class BizGenNotificationMode(val description: String) {
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
internal object BizGenDefaultAppSettings {
  private val DEFAULT: BizGenAppSettings = BizGenAppSettings()

  /**
   * Получение конфига по умолчанию.
   *
   * Применяется подход с копированием объекта, чтобы исключить мутацию его по ссылке.
   *
   * @return клон конфигурации по умолчанию
   */
  fun getDefault(): BizGenAppSettings {
    return DEFAULT.deepCopyByJson()
  }
}