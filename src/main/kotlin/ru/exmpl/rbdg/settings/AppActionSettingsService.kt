package ru.exmpl.rbdg.settings

import ru.exmpl.rbdg.di.RbdgService
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.settings.AppActionSettingsService.Direction
import ru.exmpl.rbdg.settings.model.RbdgAppSettings.ActionSetting

/**
 * Сервис по работе с настройками плагина.
 *
 * @author Dmitry_Emelyanenko
 */
interface AppActionSettingsService : RbdgService {

  /**
   * Получение настройки действия на основе позиции в списке действий.
   *
   * @param position позиция в общем списке действий
   * @return действие или null
   */
  fun findByPosition(position: Int): ActionSetting?

  /**
   * Перемещение действия.
   *
   * @param sourcePosition исходная позиция
   * @param direction направление перемещения
   * @return true - удалось переместить, false - нет
   */
  fun moveTo(sourcePosition: Int, direction: Direction): Boolean

  /**
   * Получение всех настроек для действий.
   *
   * @return настройки действий
   */
  fun getActionSettings(): List<ActionSetting>

  /**
   * Получение всех активных настроек для действий.
   *
   * @return настройки действий
   */
  fun getActiveActionSettings(): List<ActionSetting>

  /**
   * Восстановление настроек действий до состояния по умолчанию.
   *
   * @return восстановленные настройки
   */
  fun restoreByDefault(): List<ActionSetting>


  /** Направление перемещения действия. */
  enum class Direction {

    /** Вниз. */
    UP,

    /** Вверх. */
    DOWN
  }
}

/** Реализация [AppActionSettingsService]. */
class AppActionSettingsServiceImpl : AppActionSettingsService {

  override fun findByPosition(position: Int): ActionSetting? {
    return store().settings().actualActions.find { it.position == position }
  }

  override fun moveTo(sourcePosition: Int, direction: Direction): Boolean {
    val sourceAction = actions().find { it.position == sourcePosition } ?: return false

    val newPosition = when (direction) {
      Direction.UP -> sourcePosition - 1
      Direction.DOWN -> sourcePosition + 1
    }

    val nextAction = actions().find { it.position == newPosition } ?: return false

    sourceAction.position = newPosition
    nextAction.position = sourcePosition

    return true
  }

  override fun getActionSettings(): List<ActionSetting> {
    return actions().sortedBy { it.position }
  }

  override fun getActiveActionSettings(): List<ActionSetting> {
    return getActionSettings().filter { it.active }
  }

  override fun restoreByDefault(): List<ActionSetting> {
    store().settings().restoreFromDefault()

    return getActionSettings()
  }

  private companion object {
    fun actions(): List<ActionSetting> {
      return store().settings().actualActions
    }

    fun store(): RbdgAppSettingsRepository {
      return getRbdgService<RbdgAppSettingsRepository>()
    }
  }
}
