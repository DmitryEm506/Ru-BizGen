package ru.exmpl.rbdg.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import ru.exmpl.rbdg.di.getRbdgService
import ru.exmpl.rbdg.generators.Generator
import ru.exmpl.rbdg.generators.GeneratorResult
import ru.exmpl.rbdg.invokeLater
import ru.exmpl.rbdg.notification.NotificationCtx
import ru.exmpl.rbdg.notification.NotificationService
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Описание действия генератора.
 *
 * @param T тип генерируемого значения
 * @author Dmitry_Emelyanenko
 */
interface GeneratorAction<T : Any> {

  /** Идентификатор действия. */
  val id: String

  /** Название действия. */
  val name: String

  /** Применяемый генератор. */
  val generator: Generator<T>
}

/**
 * Абстрактная обертка над стандартным действием.
 *
 * @param name имя действия
 */
abstract class GeneratorAnAction(open val id: String, name: String) : AnAction(name)

/**
 * Базовая реализация применения действия.
 *
 * *После создания действия не забудьте добавить его в общий контейнер [GeneratorActionProvider]*
 *
 * @param T тип генерируемого значения
 * @property id идентификатор действия
 * @property name название действия
 * @property generator генератор
 */
abstract class BaseGeneratorAction<T : Any>(
  override val id: String,
  override val name: String,
  override val generator: Generator<T>
) : GeneratorAction<T>, GeneratorAnAction(id, name) {

  override fun actionPerformed(event: AnActionEvent) {
    val editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val project = event.getData(CommonDataKeys.PROJECT) ?: return

    val primaryCaret: Caret = editor.caretModel.primaryCaret
    val start = primaryCaret.selectionStart

    generator.generate().let { rsp ->
      Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(rsp.data.toString()), null)

      invokeLater {
        WriteCommandAction.runWriteCommandAction(project) { editor.document.insertString(start, rsp.escaped) }
        primaryCaret.removeSelection()
      }

      getRbdgService<NotificationService>().sendNotification(configureNotifCtx(rsp, editor))
    }
  }

  private fun <T : Any> configureNotifCtx(result: GeneratorResult<T>, editor: Editor): NotificationCtx<T> {
    return NotificationCtx(
      actionInfo = NotificationCtx.ActionInfo(
        id = this.id,
        name = this.name,
      ),
      editor = editor,
      result = result,
    )
  }
}