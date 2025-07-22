package ru.exmpl.rbdg

import com.intellij.openapi.application.ApplicationManager

/**
 * Утилиты для приложения.
 *
 * @author Dmitry_Emelyanenko
 */
object ApplicationUtils {

  /**
   * Асинхронное выполнение действия.
   *
   * @param action действие
   */
  fun invokeLater(action: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(action)
  }
}