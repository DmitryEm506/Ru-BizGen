package ru.eda.plgn.bizgen.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import org.slf4j.LoggerFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo

/**
 * Исполнитель MCP-инструментов с обработкой ошибок и логированием.
 *
 * Перехватывает исключения из [GeneratorInfo.generator].generate() и
 * возвращает [CallToolResult] с `isError = true` вместо проброса исключения.
 *
 * @author Dmitry_Emelyanenko
 */
object ToolExecutor {

  private val logger = LoggerFactory.getLogger("ToolExecutor")

  /**
   * Выполняет генерацию для [info] и возвращает MCP-результат.
   *
   * При успешной генерации возвращает `CallToolResult(isError = false/null)`
   * со сгенерированным значением.
   *
   * При ошибке возвращает `CallToolResult(isError = true)` с сообщением об ошибке.
   *
   * @param info описание генератора
   * @return результат выполнения инструмента
   */
  fun execute(info: GeneratorInfo<*>): CallToolResult {
    val toolName = ToolNameResolver.resolve(info)

    return try {
      val result = info.generator.generate()
      val value = result.value.toString()

      logger.debug("Инструмент '{}' выполнен успешно: {}", toolName, value)

      CallToolResult(
        content = listOf(TextContent(value)),
      )
    } catch (e: Exception) {
      logger.error("Ошибка при выполнении инструмента '{}': {}", toolName, e.message, e)

      CallToolResult(
        content = listOf(TextContent("Ошибка генерации: ${e.message ?: e.javaClass.simpleName}")),
        isError = true,
      )
    }
  }
}
