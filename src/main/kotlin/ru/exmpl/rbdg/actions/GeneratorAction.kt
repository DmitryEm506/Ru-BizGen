package ru.exmpl.rbdg.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import ru.exmpl.rbdg.ApplicationUtils.invokeLater
import ru.exmpl.rbdg.generators.Generator
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Описание действия генератора.
 *
 * @param T тип генерируемого значения
 */
interface GeneratorActionDescription<T : Any> {

  /** Идентификатор действия. */
  val id: String

  /** Название действия. */
  val name: String
}

/**
 * Базовая реализация применения действия.
 *
 * @param T тип генерируемого значения
 * @property id идентификатор действия
 * @property name название действия
 * @property generator генератор
 */
abstract class BaseGeneratorAction<T : Any>(
  override val id: String,
  override val name: String,
  private val generator: Generator<T>
) : GeneratorActionDescription<T>, AnAction(name) {

  override fun actionPerformed(event: AnActionEvent) {
    val editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val project = event.getData(CommonDataKeys.PROJECT) ?: return

    val primaryCaret: Caret = editor.caretModel.primaryCaret
    val start = primaryCaret.selectionStart

    generator.generate().let { rsp ->
      Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(rsp.data.toString()), null)

      invokeLater {
        WriteCommandAction.runWriteCommandAction(project) { editor.document.insertString(start, rsp.byFormatToInsert) }
        primaryCaret.removeSelection()
      }

//      блок с уведомлениями
//      HintManager.getInstance().showSuccessHint(editor, "${rsp.source} скопирован в буфер обмена")
//      ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(
//        Notification(
//          " ", "", rsp.source,
//          NotificationType.INFORMATION
//        )
//      )
//      Messages.showInfoMessage(rsp.source, "awdaw")
    }
  }
}
