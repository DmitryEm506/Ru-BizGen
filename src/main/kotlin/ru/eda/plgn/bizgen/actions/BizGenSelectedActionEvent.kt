package ru.eda.plgn.bizgen.actions

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic
import ru.eda.plgn.bizgen.invokeLater

/**
 * Общий интерфейс, который обеспечивает возможность работы с выбранным действием генератора.
 *
 * @author Dmitry_Emelyanenko
 */
internal fun interface BizGenSelectedActionEvent {

  /**
   * Метод по работе с выбранным действием.
   *
   * @param action действие
   */
  fun selectedAction(action: GeneratorAction<*>)

  companion object {
    private const val TOPIC_DSP_NAME = "BizGen Selected action from settings"
    private val listener = BizGenSelectedActionEvent::class.java


    /** Топик, отвечающий за передачу выбранного действия. */
    private val TOPIC: Topic<BizGenSelectedActionEvent> = Topic.create(TOPIC_DSP_NAME, listener)

    /**
     * Метод публикации, что теперь выбрано передаваемое действие.
     *
     * @param action выбранное действие
     */
    internal fun publish(action: GeneratorAction<*>) {
      ApplicationManager.getApplication().messageBus.syncPublisher(TOPIC)
        .selectedAction(action)
    }

    /**
     * Метод подписки на выбранное действие.
     *
     * @param parentDisposable [com.intellij.openapi.Disposable] родительского элемента, чтобы при очистке происходила отписка от прослушивания
     * @param handler обработчик выбранного действия
     */
    internal fun subscribeAsync(parentDisposable: Disposable, handler: (GeneratorAction<*>) -> Unit) {
      ApplicationManager.getApplication().messageBus.connect(parentDisposable)
        .subscribe(TOPIC, BizGenSelectedActionEvent { action -> invokeLater { handler(action) } })
    }
  }
}