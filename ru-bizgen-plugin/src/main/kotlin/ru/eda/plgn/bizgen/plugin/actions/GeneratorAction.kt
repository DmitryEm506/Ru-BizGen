package ru.eda.plgn.bizgen.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.plugin.clipboard.BizGenClipboardSettingsServiceView
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.escapechar.EscapeCharSettingsServiceView
import ru.eda.plgn.bizgen.plugin.invokeLater
import ru.eda.plgn.bizgen.plugin.notification.NotificationCtx
import ru.eda.plgn.bizgen.plugin.notification.NotificationService
import java.awt.datatransfer.StringSelection

/**
 * Абстрактная обертка над стандартным действием ([AnAction]).
 *
 * @param id идентификатор генератора. **Должен быть уникальным среди всех генераторов.**
 * @param name имя действия
 */
abstract class GeneratorAnAction(open val id: String, name: String) : AnAction(name)

/**
 * Базовая реализация применения действия.
 *
 * @param T тип генерируемого значения
 * @property id идентификатор действия
 * @property name название действия
 * @property generator генератор
 * @property category категория генератора
 * @property detailedDescription расширенное описание
 * @property example стабильный пример
 */
abstract class BaseGeneratorAction<T : Any>(
  override val id: String,
  override val name: String,
  override val generator: Generator<T>,
  override val category: GeneratorCategory,
  override val detailedDescription: String,
  override val example: String,
) : GeneratorInfo<T>, GeneratorAnAction(id, name) {

  /**
   * Запускает выполнение логики действия.
   *
   * @param event действие
   */
  override fun actionPerformed(event: AnActionEvent) {
    val editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val project = event.getData(CommonDataKeys.PROJECT) ?: return

    generator.generate().let { rsp ->
      val escapeChar = getBizGenService<EscapeCharSettingsServiceView>().getEscapeChar()
      val generatedText = if (escapeChar != "\"") {
        "$escapeChar${rsp.toClipboard}$escapeChar"
      } else {
        rsp.toEditor
      }.takeIf { it.isNotBlank() } ?: return

      invokeLater {
        WriteCommandAction.runWriteCommandAction(project) {
          val primaryCaret: Caret = editor.caretModel.primaryCaret
          val start = primaryCaret.selectionStart

          editor.document.insertString(start, generatedText)
          primaryCaret.removeSelection()
          primaryCaret.moveToOffset(start + generatedText.length)
        }
      }

      if (getBizGenService<BizGenClipboardSettingsServiceView>().needToIns()) {
        CopyPasteManager.getInstance().setContents(StringSelection(rsp.toClipboard.toString()))
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
