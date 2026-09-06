package ru.eda.plgn.bizgen.plugin.actions

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.plugin.invokeLater

/**
 * Общий интерфейс, который обеспечивает возможность работы с выбранным действием генератора.
 *
 * @author Dmitry_Emelyanenko
 */
internal fun interface BizGenSelectedActionEvent {

  /**
   * Метод по работе с выбранным генератором.
   *
   * @param action действие
   */
  fun selectedGenerator(action: GeneratorInfo<*>)

  companion object {
    private const val TOPIC_DSP_NAME = "RuBizGen Selected generator from settings"
    private val listener = BizGenSelectedActionEvent::class.java

    /** Топик, отвечающий за передачу выбранного действия. */
    private val TOPIC: Topic<BizGenSelectedActionEvent> = Topic.create(TOPIC_DSP_NAME, listener)

    /**
     * Метод публикации, что теперь выбран генератор.
     *
     * @param action выбранный генератор
     */
    internal fun publish(action: GeneratorInfo<*>) {
      ApplicationManager.getApplication().messageBus.syncPublisher(TOPIC)
        .selectedGenerator(action)
    }

    /**
     * Метод подписки на выбранный генератор.
     *
     * @param parentDisposable [Disposable] родительского элемента, чтобы при очистке происходила отписка от прослушивания
     * @param handler обработчик выбранного генератора
     */
    internal fun subscribeAsync(parentDisposable: Disposable, handler: (GeneratorInfo<*>) -> Unit) {
      ApplicationManager.getApplication().messageBus.connect(parentDisposable)
        .subscribe(TOPIC, BizGenSelectedActionEvent { action -> invokeLater { handler(action) } })
    }
  }
}