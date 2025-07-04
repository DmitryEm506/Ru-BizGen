package ru.exmpl.rbdg

import com.google.gson.Gson

fun <T : Any> T.deepCopyByJson(): T {
  val sourceJson = Gson().toJson(this)
  return Gson().fromJson(sourceJson, this::class.java)
}