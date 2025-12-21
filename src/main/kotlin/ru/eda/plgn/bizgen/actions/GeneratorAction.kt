package ru.eda.plgn.bizgen.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import ru.eda.plgn.bizgen.clipboard.BizGenClipboardSettingsServiceView
import ru.eda.plgn.bizgen.di.getBizGenService
import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.invokeLater
import ru.eda.plgn.bizgen.notification.NotificationCtx
import ru.eda.plgn.bizgen.notification.NotificationService
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
 * @param id идентификатор генератора. **Должен быть уникальным среди всех генераторов.**
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
  override val generator: Generator<T>,
) : GeneratorAction<T>, GeneratorAnAction(id, name) {

  /**
   * Запускает выполнение логики действия.
   *
   * @param event действие
   */
  override fun actionPerformed(event: AnActionEvent) {
    val editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val project = event.getData(CommonDataKeys.PROJECT) ?: return

    val primaryCaret: Caret = editor.caretModel.primaryCaret
    val start = primaryCaret.selectionStart

    generator.generate().let { rsp ->
      val generatedText = rsp.toEditor.takeIf { it.isNotBlank() } ?: return

      invokeLater {
        WriteCommandAction.runWriteCommandAction(project) { editor.document.insertString(start, generatedText) }
        primaryCaret.removeSelection()
        primaryCaret.moveToOffset(start + generatedText.length)
      }

      if (getBizGenService<BizGenClipboardSettingsServiceView>().needToIns()) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(rsp.toClipboard.toString()), null)
        getBizGenService<NotificationService>().sendNotification(configureNotifCtx(rsp, editor))
      }
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