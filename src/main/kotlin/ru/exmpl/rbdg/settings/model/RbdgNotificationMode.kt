package ru.exmpl.rbdg.settings.model

/**
 * Режимы уведомлений.
 *
 * @author Dmitry_Emelyanenko
 */
enum class RbdgNotificationMode(val description: String) {
  BELL("""Лог событий "Колокольчик""""),
  HINT("Всплывающее окно"),
  DISABLE("Выключено"),
}