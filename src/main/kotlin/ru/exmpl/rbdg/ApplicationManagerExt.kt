package ru.exmpl.rbdg

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

inline fun <reified T : Any> getRbdgService(): T {
  return ApplicationManager.getApplication().service<T>()
}
