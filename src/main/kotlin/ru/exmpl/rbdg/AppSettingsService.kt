package ru.exmpl.rbdg

import com.intellij.openapi.components.Service
import ru.exmpl.rbdg.AppSettingsService.Direction


interface AppSettingsService {
  fun getAppSettings(): AppSettings

  fun findActionByIndex(index: Int): ActualAction?

  fun moveAction(sourcePosition: Int, direction: Direction): Boolean

  fun getActions(): List<ActualAction>

  enum class Direction {
    UP,
    DOWN
  }
}

/**
 * AppSettingsService.
 *
 * @author Dmitry_Emelyanenko
 */
@Service
class AppSettingsServiceImpl : AppSettingsService {

  override fun getAppSettings(): AppSettings {
    return store().getAppSettings()
  }

  override fun findActionByIndex(index: Int): ActualAction? {
    return store().getAppSettings().actualActions[index]
  }

  override fun moveAction(sourcePosition: Int, direction: Direction): Boolean {
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

  override fun getActions(): List<ActualAction> {
    return actions().sortedBy { it.position }
  }

  private companion object {
    fun actions(): List<ActualAction> {
      return store().getAppSettings().actualActions
    }

    fun store(): AppSettingsStore {
      return getRbdgService<AppSettingsStore>()
    }
  }
}
