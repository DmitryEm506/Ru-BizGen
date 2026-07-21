package ru.eda.plgn.bizgen.mcp

import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo

/**
 * Резолвер type-key для категорийных MCP-тулов.
 *
 * Type-key вычисляется из стабильного идентификатора [GeneratorInfo.id]
 * (формат: `<TypeName>_<UUID>`): извлекается префикс до UUID, преобразуется
 * из CamelCase в snake_case и переводится в нижний регистр.
 *
 * Например: `InnLegal_f5e5e2b3-...` → `inn_legal`.
 *
 * Это значение используется как enum-значение параметра `type` в категорийных
 * тулах и как ключ lookup-таблицы в [ToolDispatcher].
 *
 * @author Dmitry_Emelyanenko
 */
object TypeKeyResolver {

  private val idPrefixRegex = Regex("""^([^_]+)_[0-9a-fA-F]{8}-""")
  private val camelToSnake1 = Regex("([a-z0-9])([A-Z])")
  private val camelToSnake2 = Regex("([A-Z])([A-Z][a-z])")

  /**
   * Вычисляет type-key для [info].
   *
   * @return type-key в нижнем регистре (например, `inn_legal`)
   * @throws IllegalStateException если не удалось извлечь префикс из [GeneratorInfo.id]
   */
  fun resolve(info: GeneratorInfo<*>): String = compute(info.id)

  /**
   * Вычисляет type-key по [id] генератора.
   *
   * @return type-key в нижнем регистре (например, `inn_legal`)
   * @throws IllegalStateException если не удалось извлечь префикс из [id]
   */
  fun compute(id: String): String {
    val prefix = idPrefixRegex.find(id)?.groupValues?.get(1)
      ?: error(
        "Не удалось извлечь префикс из id: '$id'. " +
          "Ожидаемый формат: <TypeName>_<UUID>"
      )

    return prefix
      .replace(camelToSnake1) { "${it.groupValues[1]}_${it.groupValues[2]}" }
      .replace(camelToSnake2) { "${it.groupValues[1]}_${it.groupValues[2]}" }
      .lowercase()
  }
}
