package ru.exmpl.rbdg.di

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

/**
 * Маркерный интерфейс, им должны отмечаться собственные сервисы проекта, которые попадают в DI.
 *
 * @author Dmitry_Emelyanenko
 */
interface RbdgService

/**
 * Получение сервиса плагина из DI.
 *
 * @param T тип сервиса, который обязательно должен быть наследником [RbdgService]
 * @return возвращаемый сервис или ошибка
 */
inline fun <reified T : RbdgService> getRbdgService(): T {
  return ApplicationManager.getApplication().service<T>()
}