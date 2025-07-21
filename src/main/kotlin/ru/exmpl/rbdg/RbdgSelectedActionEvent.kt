package ru.exmpl.rbdg

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic

/**
 * RbdgSelectedAction.
 *
 * @author Dmitry_Emelyanenko
 */
internal fun interface RbdgSelectedActionEvent {
  fun selectedAction(value: String)

  companion object {
    private const val TOPIC_DSP_NAME = "RBG Selected action from settings"
    private val listener = RbdgSelectedActionEvent::class.java

    private val TOPIC: Topic<RbdgSelectedActionEvent> = Topic.create(TOPIC_DSP_NAME, listener)

    internal fun publish(value: String) {
      ApplicationManager.getApplication().messageBus.syncPublisher(TOPIC)
        .selectedAction(value)
    }

    fun subscribeAsync(parentDisposable: Disposable, handler: (String) -> Unit) {
      ApplicationManager.getApplication().messageBus.connect(parentDisposable)
        .subscribe(TOPIC, RbdgSelectedActionEvent {
          ApplicationManager.getApplication().invokeLater {
            handler(it)
          }
        })
    }
  }
}