package ru.eda.plgn.bizgen.plugin.settings.model

import com.intellij.util.xmlb.XmlSerializer
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.di.getBizGenService

/**
 * Основной конфиг плагина.
 *
 * @author Dmitry_Emelyanenko
 */
open class BizGenAppSettings {

  /** Режим уведомления. */
  var notificationMode: BizGenNotificationMode = BizGenNotificationMode.DISABLE

  /** Актуальный список действий. */
  var actualActions: MutableList<PersistenceActionSetting> = initSettingActions()

  /** Признак, что необходимо вставить результат работы генератора в буфер обмена. */
  var insToClipboard: Boolean = true

  /** Символ обрамления для вставки значения в редактор. */
  var escapeChar: String = "\""

  /** Сброс настроек до значений по умолчанию. */
  open fun restoreFromDefault() {
    actualActions.clear()
    actualActions.addAll(BizGenDefaultAppSettings.getDefault().actualActions)
  }

  /**
   * Полная копия конфига с новыми ссылками.
   *
   * Реализована через round-trip сериализации, а не ручным перечислением полей: новое свойство или
   * вложенный класс попадают в копию автоматически, без риска забыть скопировать его руками.
   * Глубина гарантирована — из XML собирается полностью новый граф объектов.
   *
   * Используется [XmlSerializer] платформы — тот же механизм, которым настройки персистятся через
   * `PersistentStateComponent`. Поэтому семантика копии совпадает с семантикой сохранения на диск,
   * и не требуется отдельная зависимость на библиотеку сериализации.
   *
   * Свойства, равные значениям по умолчанию, [XmlSerializer] в XML не пишет — это безопасно:
   * копия создаётся тем же конструктором без аргументов, то есть стартует с тех же инициализаторов.
   *
   * @return независимая копия настроек
   */
  fun deepCopy(): BizGenAppSettings =
    XmlSerializer.deserialize(XmlSerializer.serialize(this), BizGenAppSettings::class.java)

  private fun initSettingActions(): MutableList<PersistenceActionSetting> {
    val actions = getBizGenService<GeneratorActionProvider>().getInfos()

    return actions.mapIndexed { index, action ->
      PersistenceActionSetting(
        id = action.id,
        position = index,
        description = action.name,
        active = true
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
   * @property customName пользовательское имя действия
   * @author Dmitry_Emelyanenko
   */
  data class PersistenceActionSetting(
    override var id: String = "",
    override var position: Int = 0,
    override var description: String = "",
    override var active: Boolean = true,
    override var customName: String = "",
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

    /** Пользовательское имя действия. */
    val customName: String get() = ""
  }

  /**
   * Режимы уведомлений.
   *
   * @property description описание режима, которое будет отображаться в UI компоненте
   * @author Dmitry_Emelyanenko
   */
  enum class BizGenNotificationMode(val description: String) {

    /** Лог событий "Колокольчик". */
    BELL("""Лог событий "Колокольчик""""),

    /** Всплывающее окно. */
    HINT("Всплывающее окно"),

    /** Выключено. */
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
    return DEFAULT.deepCopy()
  }
}