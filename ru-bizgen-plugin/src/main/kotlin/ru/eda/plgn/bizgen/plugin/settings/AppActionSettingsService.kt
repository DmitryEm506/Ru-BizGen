package ru.eda.plgn.bizgen.plugin.settings

import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.AppActionSettingsService.Direction
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.ActionSettingsView
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Сервис по работе с настройками плагина.
 *
 * @author Dmitry_Emelyanenko
 */
interface AppActionSettingsService : BizGenService {

  /**
   * Получение настройки действия на основе позиции в списке действий.
   *
   * @param position позиция в общем списке действий
   * @return действие или null
   */
  fun findByPosition(position: Int): ActionSettingsView?

  /**
   * Изменение признака [ActionSettingsView.active] для выбранного действия.
   *
   * @param position позиция действия в общем списке
   * @param isActive признак, что действие стало активным или нет
   * @return измененное действие, если нашли по позиции
   */
  fun changeActivity(position: Int, isActive: Boolean): ActionSettingsView?

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
  fun getActionSettings(): List<ActionSettingsView>

  /**
   * Получение всех активных настроек для действий.
   *
   * @return настройки действий
   */
  fun getActiveActionSettings(): List<ActionSettingsView>

  /**
   * Восстановление настроек действий до состояния по умолчанию.
   *
   * @return восстановленные настройки
   */
  fun restoreByDefault(): List<ActionSettingsView>

  /**
   * Переименование действия.
   *
   * @param position позиция действия в общем списке
   * @param newName новое имя действия
   * @return измененное действие, если нашли по позиции
   */
  fun renameAction(position: Int, newName: String): ActionSettingsView?

  /**
   * Массовое изменение признака [ActionSettingsView.active] для всех действий.
   *
   * @param active новое значение признака активности для всех действий
   * @return количество изменённых действий
   */
  fun setAllActive(active: Boolean): Int

  /**
   * Количество активных действий.
   *
   * @return количество действий с признаком [ActionSettingsView.active] = true
   */
  fun activeCount(): Int

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

  override fun findByPosition(position: Int): ActionSettingsView? {
    return findByPositionInner(position)
  }

  override fun changeActivity(position: Int, isActive: Boolean): ActionSettingsView? {
    return findByPositionInner(position)?.apply { active = isActive }
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

  override fun getActionSettings(): List<ActionSettingsView> {
    return actions().sortedBy { it.position }
  }

  override fun getActiveActionSettings(): List<ActionSettingsView> {
    return getActionSettings().filter { it.active }
  }

  override fun restoreByDefault(): List<ActionSettingsView> {
    store().settings().restoreFromDefault()

    return getActionSettings()
  }

  override fun renameAction(position: Int, newName: String): ActionSettingsView? {
    return findByPositionInner(position)?.apply {
      customName = newName
      description = newName
    }
  }

  override fun setAllActive(active: Boolean): Int {
    val allActions = actions()
    allActions.forEach { it.active = active }
    return allActions.size
  }

  override fun activeCount(): Int {
    return actions().count { it.active }
  }

  private companion object {
    private fun findByPositionInner(position: Int): PersistenceActionSetting? {
      return actions().find { it.position == position }
    }

    private fun actions(): List<PersistenceActionSetting> {
      return store().settings().actualActions
    }

    private fun store(): BizGenAppSettingsRepository {
      return getBizGenService<BizGenAppSettingsRepository>()
    }
  }
}
