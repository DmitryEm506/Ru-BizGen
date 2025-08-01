package ru.person.bizgen

import com.google.gson.Gson

/**
 * Метод клонирование объекта через Json.
 *
 * Обеспечивает глубокое клонирование.
 *
 * @param T тип клонируемого объекта
 * @return копия исходного объекта с новыми ссылками
 */
fun <T : Any> T.deepCopyByJson(): T {
  val sourceJson = Gson().toJson(this)
  return Gson().fromJson(sourceJson, this::class.java)
}