package ru.exmpl.rbdg.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.undo.UndoUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.Disposer
import com.intellij.ui.InplaceButton
import com.intellij.ui.SeparatorFactory
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import ru.exmpl.rbdg.actions.RbdgSelectedActionEvent
import ru.exmpl.rbdg.generators.Generator
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.random.Random

/**
 * ActionResultPreviewComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class ActionResultPreviewComponent() : Disposable {
  val rootComponent: JBPanel<JBPanel<*>>
  private val refreshButton: JComponent
  private val previewDocument: Document

  private var selectedGenerator: Generator<*>? = null

  private var seed = Random.Default.nextInt()

  init {
    // Components
    refreshButton =
      InplaceButton("Generate", AllIcons.Actions.Refresh) {
        seed = Random.Default.nextInt()
      }

    val factory = EditorFactory.getInstance()
    previewDocument = factory.createDocument("Выберите генератор").also { UndoUtil.disableUndoFor(it) }

    val previewEditor = factory.createViewer(previewDocument)
    Disposer.register(this) { EditorFactory.getInstance().releaseEditor(previewEditor) }

    // Layout
    rootComponent = JBPanel<JBPanel<*>>(BorderLayout())
    val header = JPanel(BorderLayout())
    val separator = SeparatorFactory.createSeparator("ActionID: ", null)
    header.add(separator, BorderLayout.CENTER)
    header.add(refreshButton, BorderLayout.EAST)

    val previewEditorComponent = previewEditor.component.apply {
      preferredSize = JBUI.size(600, 200) // HiDPI-корректный размер
      minimumSize = preferredSize
      maximumSize = preferredSize // Фиксируем, чтобы не растягивался
    }

    rootComponent.add(header, BorderLayout.NORTH)
    rootComponent.add(previewEditorComponent, BorderLayout.SOUTH)

    RbdgSelectedActionEvent.Companion.subscribeAsync(this) { action ->
      selectedGenerator = action.generator

      separator.text = "ActionID: ${action.id}"
      updatePreviewTextByGenerator()
    }
  }

  private fun updatePreviewTextByGenerator() {
    val result = selectedGenerator?.generate() ?: return

    val resultText = """
      Вставляемый текст: ${result.byFormatToInsert}
      Копируемый в буфер: ${result.data}
    """.trimIndent()

    runWriteAction { previewDocument.setTextInReadOnly(resultText) }
  }

  override fun dispose() = Unit

  private companion object {
    fun Document.setTextInReadOnly(text: String) {
      setReadOnly(false)
      setText(text)
      setReadOnly(true)
    }
  }
}