package ru.person.bizgen.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.undo.UndoUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.Disposer
import com.intellij.ui.InplaceButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.person.bizgen.actions.BizGenSelectedActionEvent.Companion.subscribeAsync
import ru.person.bizgen.generators.Generator
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import kotlin.random.Random

/**
 * Компонент, который применяется в настройках плагина.
 *
 * Служит для отображения результатов выбранного генератора.
 *
 * @author Dmitry_Emelyanenko
 */
class ActionResultPreviewComponent : Disposable {

  /** Выбранный генератор. */
  private var selectedGenerator: Generator<*>? = null

  /** Для обновления состояния компонента. */
  private var seed = Random.Default.nextInt()

  /** Содержит результат работы генератора. */
  private lateinit var previewEditor: EditorEx

  /** Кнопка для повторного запуска выбранного генератора. */
  private lateinit var refreshButton: InplaceButton

  /** Содержит идентификатор генератора. */
  private lateinit var actionIdLabel: JLabel

  val rootComponent: JPanel = panel {
    // разделитель с отображением идентификатора выбранного генератора и кнопкой для повторного запуска выбранного генератора
    row {
      val separatorPanel = JPanel(BorderLayout()).apply {
        actionIdLabel = JLabel("ActionID: ").apply {
          border = JBUI.Borders.emptyRight(5)
        }
        add(actionIdLabel, BorderLayout.WEST)
        add(JSeparator(), BorderLayout.CENTER)
        border = JBUI.Borders.empty(5)
      }
      cell(separatorPanel)

      refreshButton = InplaceButton("Generate", AllIcons.Actions.Refresh) {
        seed = Random.Default.nextInt()
        updatePreviewTextByGenerator()
      }
      cell(refreshButton).align(AlignX.RIGHT)
    }

    // содержит область для отображения результатов выбранного генератора
    row {
      val factory = EditorFactory.getInstance()
      val previewDocument = factory.createDocument("Выберите генератор").also {
        UndoUtil.disableUndoFor(it)
      }
      previewEditor = factory.createEditor(previewDocument) as EditorEx

      cell(previewEditor.component.apply {
        preferredSize = JBUI.size(600, 200)
        minimumSize = preferredSize
        maximumSize = preferredSize
      }).align(AlignX.FILL)
    }
  }

  init {
    Disposer.register(this) {
      EditorFactory.getInstance().releaseEditor(previewEditor)
    }

    subscribeAsync(this) { action ->
      selectedGenerator = action.generator
      actionIdLabel.text = "ActionID: ${action.id}"
      updatePreviewTextByGenerator()
    }
  }

  private fun updatePreviewTextByGenerator() {
    val result = selectedGenerator?.generate() ?: return

    val resultText = """
            Вставляемый текст в редактор:
            ${result.toEditor}
            
            Копируемый в текст в буфер обмена:
            ${result.toClipboard}
        """.trimIndent()

    runWriteAction {
      previewEditor.document.setTextInReadOnly(resultText)
    }
  }

  override fun dispose() {
    Disposer.dispose(this)
  }

  private companion object {

    /**
     * Чтобы документ был в режиме только просмотра, специально перед вставкой текста снимаем этот режим, потом вставляем текст, а потом
     * опять возвращаем режим только для просмотра.
     *
     * @param text вставляемый текст
     */
    fun Document.setTextInReadOnly(text: String) {
      setReadOnly(false)
      setText(text)
      setReadOnly(true)
    }
  }
}