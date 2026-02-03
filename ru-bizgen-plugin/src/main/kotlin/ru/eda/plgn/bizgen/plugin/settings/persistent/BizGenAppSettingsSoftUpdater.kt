package ru.eda.plgn.bizgen.plugin.settings.persistent

import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.di.BizGenService
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.ActionSettingsView
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Мягкое обновление настроек генераторов.
 *
 * @author Dmitry_Emelyanenko
 */
fun interface BizGenAppSettingsSoftUpdater : BizGenService {

  /**
   * Обновление сохраненных генераторов.
   *
   * Процесс обновления выглядит следующим образом:
   * - Удаление генератора, если он отсутствует среди доступных
   * - Если есть среди доступных, то оставляем состояние активности и позицию без изменений и обновляем только название (на всякий случай)
   * - Новые генераторы добавляем в конец и по умолчанию делаем их активными
   *
   * @param savedActions загружаемые настройки генераторов
   * @return обновленные настройки генераторов
   */
  fun softUpdateActions(savedActions: MutableList<PersistenceActionSetting>): MutableList<PersistenceActionSetting>
}

/** Реализация [BizGenAppSettingsSoftUpdater]. */
class BizGenAppSettingsSoftUpdaterImpl : BizGenAppSettingsSoftUpdater {

  override fun softUpdateActions(savedActions: MutableList<PersistenceActionSetting>): MutableList<PersistenceActionSetting> {
    val actualActions = getActualActions()

    removeNotActualActions(savedActions, actualActions)
    updateActualActions(savedActions, actualActions)
    addNewActualActions(savedActions, actualActions)

    return savedActions
  }

  private fun addNewActualActions(savedActions: MutableList<PersistenceActionSetting>, actualActions: List<ActionSettingsView>) {
    val nextPosition = savedActions.maxOfOrNull { it.position }?.let { it + 1 } ?: 0
    val savedActionids = savedActions.map { it.id }

    actualActions
      .filter { it.id !in savedActionids }
      .forEachIndexed { index, newAction ->
        savedActions.add(
          PersistenceActionSetting(
            id = newAction.id,
            position = index + nextPosition,
            description = newAction.description,
            active = true,
          )
        )
      }
  }

  private companion object {
    val notActualCriteria: (PersistenceActionSetting, List<ActionSettingsView>) -> Boolean = { current, actuals ->
      current.id !in actuals.map { it.id }
    }

    fun removeNotActualActions(saved: MutableList<PersistenceActionSetting>, actual: List<ActionSettingsView>) {
      // находим позиции действий, которые потеряли актуальность
      val notActualActionPositions = saved.filter { notActualCriteria(it, actual) }.map { it.position }

      // удаляем неактуальные действия
      saved.removeIf { notActualCriteria(it, actual) }

      restorePositions(saved, notActualActionPositions)
    }

    fun updateActualActions(saved: List<PersistenceActionSetting>, actuals: List<ActionSettingsView>) {
      val actualsById = actuals.associateBy { it.id }

      saved.forEach { savedAction ->
        actualsById[savedAction.id]?.let { actual ->
          savedAction.apply { description = actual.description }
        }
      }
    }

    fun restorePositions(saved: List<PersistenceActionSetting>, removedPositions: List<Int>) {
      return saved.forEach { action ->
        /*
        Определяем сколько было удалено действий относительно позиции текущего действия.
        Например:
        1) Текущая позиция = 5
        2) Были удалены позиции 1,4,6,8
        3) Получается, что наша позиция должна сместиться на 2, так как её затрагивает удаление первой и четвертой позиций
         */
        val fondShift = removedPositions.filter { removeIndex -> removeIndex < action.position }.size

        action.position -= fondShift
      }
    }

    fun getActualActions(): List<ActionSettingsView> {
      return getBizGenService<GeneratorActionProvider>().getInfos().map { info ->
        PersistenceActionSetting(
          id = info.id,
          position = -1,
          description = info.name,
          active = true
        )
      }
    }
  }
}