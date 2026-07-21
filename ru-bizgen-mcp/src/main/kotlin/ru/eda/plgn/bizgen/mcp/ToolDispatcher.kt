package ru.eda.plgn.bizgen.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.slf4j.LoggerFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo

/**
 * Диспетчер категорийных MCP-тулов.
 *
 * Заменяет [ToolExecutor] — вместо одного тула на генератор, 5 категорийных тулов
 * делегируют сюда вызов по `(category, type, count)`.
 *
 * Lookup-таблица `Map<Pair<GeneratorCategory, String>, GeneratorInfo<*>>` строится
 * один раз из списка всех генераторов. Ключ — `(category, typeKey)`, где `typeKey`
 * вычисляется через [TypeKeyResolver].
 *
 * @author Dmitry_Emelyanenko
 */
object ToolDispatcher {

  private val logger = LoggerFactory.getLogger("ToolDispatcher")

  /** Максимальное количество значений в одном вызове (защита от abuse). */
  const val MAX_COUNT: Int = 1_000

  /** Множитель попыток для bounded retry при дедупликации. */
  private const val RETRY_MULTIPLIER: Int = 3

  /**
   * Выполняет генерацию для заданных параметров.
   *
   * @param category категория генератора
   * @param type type-key генератора (например, `inn_legal`)
   * @param count количество значений (1 по умолчанию)
   * @param infos список всех генераторов для построения lookup
   * @return MCP-результат
   */
  fun execute(
    category: GeneratorCategory,
    type: String,
    count: Int = 1,
    infos: List<GeneratorInfo<*>>,
  ): CallToolResult {
    val lookup = buildLookup(infos)
    val key = category to type

    val info = lookup[key]
      ?: return errorResult(
        "Генератор с type='$type' в категории ${category.name} не найден. " +
          "Доступные type в категории: ${availableTypes(lookup, category)}"
      )

    if (count < 1) {
      return errorResult("Параметр count должен быть >= 1, получено: $count")
    }

    if (count > MAX_COUNT) {
      return errorResult(
        "Параметр count превышает максимум ($MAX_COUNT). Запрошено: $count"
      )
    }

    val uniqueDistance = info.generator.uniqueDistance
    if (count > uniqueDistance) {
      return errorResult(
        "Невозможно сгенерировать $count уникальных значений: " +
          "uniqueDistance генератора '$type' = $uniqueDistance. " +
          "Запросите не более $uniqueDistance значений."
      )
    }

    return generateValues(info, type, count)
  }

  /**
   * Генерирует [count] уникальных значений с bounded retry.
   */
  private fun generateValues(
    info: GeneratorInfo<*>,
    type: String,
    count: Int,
  ): CallToolResult {
    val values = LinkedHashSet<String>(count)
    val maxAttempts = count * RETRY_MULTIPLIER
    var attempts = 0

    while (values.size < count && attempts < maxAttempts) {
      try {
        val result = info.generator.generate()
        values.add(result.value.toString())
      } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) {
          throw e
        }
        logger.error("Ошибка генерации (type='{}', попытка {}): {}", type, attempts + 1, e.message, e)
        return errorResult("Ошибка генерации: ${e.message ?: e.javaClass.simpleName}")
      }
      attempts++
    }

    val isPartial = values.size < count

    return if (count == 1 && values.size == 1) {
      val value = values.first()
      logger.debug("Инструмент type='{}' выполнен успешно: {}", type, value)
      CallToolResult(
        content = listOf(TextContent(value)),
      )
    } else {
      logger.debug("Инструмент type='{}' выполнен: {}/{} значений (partial={})", type, values.size, count, isPartial)
      CallToolResult(
        content = listOf(TextContent(values.joinToString("\n"))),
        structuredContent = buildStructuredContent(type, count, values, isPartial),
      )
    }
  }

  /**
   * Строит `structuredContent` для batch-результата.
   */
  private fun buildStructuredContent(
    type: String,
    count: Int,
    values: Set<String>,
    isPartial: Boolean,
  ): JsonObject = buildJsonObject {
    put("type", JsonPrimitive(type))
    put("count", JsonPrimitive(values.size))
    putJsonArray("values") {
      values.forEach { add(JsonPrimitive(it)) }
    }
    if (isPartial) {
      put("partial", JsonPrimitive(true))
    }
    put("requestedCount", JsonPrimitive(count))
  }

  private fun buildLookup(infos: List<GeneratorInfo<*>>): Map<Pair<GeneratorCategory, String>, GeneratorInfo<*>> =
    infos.associateBy { info ->
      info.category to TypeKeyResolver.resolve(info)
    }

  private fun availableTypes(
    lookup: Map<Pair<GeneratorCategory, String>, GeneratorInfo<*>>,
    category: GeneratorCategory,
  ): String = lookup.keys
    .filter { it.first == category }
    .map { it.second }
    .sorted()
    .joinToString(", ")

  private fun errorResult(message: String): CallToolResult =
    CallToolResult(
      content = listOf(TextContent(message)),
      isError = true,
    )
}
