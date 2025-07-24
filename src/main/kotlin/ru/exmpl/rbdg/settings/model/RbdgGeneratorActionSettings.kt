package ru.exmpl.rbdg.settings.model

/**
 * Настройка для конкретного действия.
 *
 * @property id идентификатор действия
 * @property position позиция в общем списке действий
 * @property description описание
 * @property active признак, что действие активное
 * @author Dmitry_Emelyanenko
 */
data class RbdgGeneratorActionSettings(
  var id: String,
  var position: Int,
  var description: String,
  var active: Boolean
)