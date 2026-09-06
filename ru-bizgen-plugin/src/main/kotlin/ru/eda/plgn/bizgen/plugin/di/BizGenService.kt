package ru.eda.plgn.bizgen.plugin.di

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service

/**
 * Маркерный интерфейс, им должны отмечаться собственные сервисы проекта, которые попадают в [ApplicationManager].
 *
 * @author Dmitry_Emelyanenko
 */
interface BizGenService

/**
 * Получение реализованного сервиса (точнее его интерфейса) из DI.
 *
 * @param T тип сервиса
 * @return возвращаемый сервис или ошибка
 */
inline fun <reified T : BizGenService> getBizGenService(): T {
  return ApplicationManager.getApplication().service<T>()
}