/**
 * Логика расчета версии IntelliJ IDEA из версии плагина.
 * 
 * Извлекает build number из version (последняя часть после точки)
 * и преобразует его в формат версии IC.
 * 
 * Пример: version = "1.7.242" -> buildNumber = "242" -> icVersion = "2024.2"
 */

// Извлекаем build number из version (последняя часть после точки)
val buildNumber = version.toString().substringAfterLast(".")

// Преобразуем build number в версию IC: 242 -> 2024.2 (24 -> 2024, последняя цифра -> .2)
val icVersion = buildNumber.let {
  val year = "20${it.take(2)}"
  val minor = it.last().toString()
  "$year.$minor"
}

// Сохраняем в extra properties для гарантированного доступа из основного файла
extra["buildNumber"] = buildNumber
extra["icVersion"] = icVersion

