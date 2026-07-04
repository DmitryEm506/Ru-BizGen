package ru.eda.plgn.bizgen.mcp

import org.slf4j.LoggerFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo

/**
 * Резолвер имён MCP-инструментов.
 *
 * Имя вычисляется из стабильного идентификатора [GeneratorInfo.id]
 * (формат: `<TypeName>_<UUID>`), а не из имени класса генератора.
 * Это гарантирует стабильность имён при рефакторинге классов генераторов.
 *
 * Алгоритм:
 * 1. Извлекается префикс до UUID (часть до `_` + 8 hex-цифр + `-`).
 * 2. CamelCase преобразуется в snake_case.
 * 3. Добавляется префикс `generate_`.
 *
 * Результат валидируется по правилам MCP: `[a-zA-Z0-9_]{1,64}`.
 *
 * @author Dmitry_Emelyanenko
 */
object ToolNameResolver {

  private val logger = LoggerFactory.getLogger("ToolNameResolver")

  private val idPrefixRegex = Regex("""^([^_]+)_[0-9a-fA-F]{8}-""")
  private val camelToSnake1 = Regex("([a-z0-9])([A-Z])")
  private val camelToSnake2 = Regex("([A-Z])([A-Z][a-z])")
  private val validToolNameRegex = Regex("""^[a-zA-Z0-9_]{1,64}$""")

  private val cache = mutableMapOf<String, String>()

  /**
   * Вычисляет имя MCP-инструмента для [info].
   *
   * @return стабильное имя инструмента (например, `generate_inn_legal`)
   * @throws IllegalStateException если не удалось извлечь префикс из [GeneratorInfo.id]
   * @throws IllegalArgumentException если имя не соответствует правилам MCP
   */
  fun resolve(info: GeneratorInfo<*>): String =
    cache.getOrPut(info.id) { compute(info) }

  /**
   * Вычисляет имена для всех [infos], проверяя уникальность.
   *
   * @param infos список всех генераторов
   * @return map `GeneratorInfo.id -> toolName`
   * @throws IllegalStateException если найдены дубликаты имён
   */
  fun resolveAll(infos: List<GeneratorInfo<*>>): Map<String, String> {
    val result = infos.associate { it.id to resolve(it) }

    val duplicates = result.values
      .groupingBy { it }
      .eachCount()
      .filter { it.value > 1 }

    check(duplicates.isEmpty()) {
      "Найдены дубликаты имён MCP-инструментов: $duplicates"
    }

    logger.info("Разрешено {} MCP-инструментов", result.size)
    return result
  }

  private fun compute(info: GeneratorInfo<*>): String {
    val prefix = idPrefixRegex.find(info.id)?.groupValues?.get(1)
      ?: error(
        "Не удалось извлечь префикс из id: '${info.id}'. " +
          "Ожидаемый формат: <TypeName>_<UUID>"
      )

    val snakeCase = prefix
      .replace(camelToSnake1) { "${it.groupValues[1]}_${it.groupValues[2]}" }
      .replace(camelToSnake2) { "${it.groupValues[1]}_${it.groupValues[2]}" }
      .lowercase()

    val toolName = "generate_$snakeCase"

    require(validToolNameRegex.matches(toolName)) {
      "Имя инструмента '$toolName' (из id '${info.id}') " +
        "не соответствует правилам MCP: [a-zA-Z0-9_]{1,64}"
    }

    return toolName
  }
}
