package ru.exmpl.rbdg

import com.intellij.openapi.application.ApplicationManager

/**
 * Асинхронное выполнение действия.
 *
 * @param action действие
 */
fun invokeLater(action: () -> Unit) {
  ApplicationManager.getApplication().invokeLater(action)
}