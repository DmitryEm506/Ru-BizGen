package ru.exmpl.rbdg

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.undo.UndoUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.Disposer
import com.intellij.ui.InplaceButton
import com.intellij.ui.SeparatorFactory
import ru.exmpl.rbdg.RbdgSelectedActionEvent.Companion.subscribeAsync
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.random.Random

/**
 * ActionResultPreviewComponent.
 *
 * @author Dmitry_Emelyanenko
 */
class ActionResultPreviewComponent(private val actionResult: () -> String) : Disposable {
  val rootComponent: JPanel
  private val refreshButton: JComponent
  private val previewDocument: Document

  private var seed = Random.nextInt()

  private var selectedActionGenerator = 1

  var previewText: String
    get() = previewDocument.text
    set(value) = ApplicationManager.getApplication().invokeLater { runWriteAction { previewDocument.setTextInReadOnly(value) } }

  init {
    // Components
    refreshButton =
      InplaceButton("Generate", AllIcons.Actions.Refresh) {
        seed = Random.nextInt()
        previewText = actionResult()
      }

    val factory = EditorFactory.getInstance()
    previewDocument = factory.createDocument("").also { UndoUtil.disableUndoFor(it) }

    val previewEditor = factory.createViewer(previewDocument)
    Disposer.register(this) { EditorFactory.getInstance().releaseEditor(previewEditor) }

    // Layout
    rootComponent = JPanel(BorderLayout())
    val header = JPanel(BorderLayout())
    val separator = SeparatorFactory.createSeparator("ActionID: ", null)
    header.add(separator, BorderLayout.CENTER)
    header.add(refreshButton, BorderLayout.EAST)

    rootComponent.add(header, BorderLayout.NORTH)
    rootComponent.add(previewEditor.component, BorderLayout.CENTER)

    subscribeAsync(this) {
      runWriteAction { previewDocument.setTextInReadOnly(it) }
      separator.text = "ActionID: $it"
    }
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