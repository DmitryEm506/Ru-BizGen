package ru.person.bizgen.notification

import com.intellij.openapi.editor.Editor
import ru.person.bizgen.generators.GeneratorResult

/**
 * Контекст для уведомления.
 *
 * @param T тип результата работы генератора
 * @property actionInfo информация о выполненном действии
 * @property editor форма редактирования
 * @property result результат работы генератора
 * @author Dmitry_Emelyanenko
 */
data class NotificationCtx<T : Any>(
  val actionInfo: ActionInfo,
  val editor: Editor,
  val result: GeneratorResult<T>,
) {

  /**
   * Информация о выполненном действии.
   *
   * @property id идентификатор действия
   * @property name наименование действия
   */
  data class ActionInfo(
    val id: String,
    val name: String,
  )
}