package ru.eda.plgn.bizgen.mcp

import io.ktor.http.ContentType
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.slf4j.LoggerFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

private val logger = LoggerFactory.getLogger("McpServerApp")

/**
 * Точка входа в процесс запуска MCP сервера.
 *
 * Регистрирует 5 категорийных тулов (`generate_<category>`) вместо 31 плоского тулa. Каждый тул принимает `type: enum` (генератор внутри
 * категории) и опциональный `count: int`.
 *
 * @properties args входные аргументы. Обрабатываются следующие аргументы: host, port
 */
fun main(args: Array<String>) {
  val host = args.firstOrNull { it.startsWith("--host=") }
    ?.substringAfter("--host=")
    ?: "127.0.0.1"
  val port = args.firstOrNull { it.startsWith("--port=") }
    ?.substringAfter("--port=")
    ?.toIntOrNull() ?: 8081

  val version = mcpVersion()
  val infos = GeneratorInfoProvider.generatorInfos

  logger.info("Запуск ru-bizgen MCP-сервера на основе версии плагина {} на {}:{}", version, host, port)
  logger.info("Зарегистрировано категорийных тулов: {} ({} генераторов)", infos.map { it.category }.distinct().size, infos.size)

  val server = createBizGenServer(version, infos)

  startStreamableHttpServer(server, host, port)
}

private fun createBizGenServer(version: String, infos: List<GeneratorInfo<*>>): Server = Server(
  serverInfo = Implementation(name = "ru-bizgen", version = version),
  options = ServerOptions(
    capabilities = ServerCapabilities(
      tools = ServerCapabilities.Tools(listChanged = false),
    ),
  ),
) {
  GeneratorCategory.entries.forEach { category ->
    val categoryInfos = infos.filter { it.category == category }
    if (categoryInfos.isNotEmpty()) {
      addCategoryTool(category, categoryInfos, infos)
    }
  }
}

/** Регистрирует один категорийный тул `generate_<category>`. */
private fun Server.addCategoryTool(
  category: GeneratorCategory,
  categoryInfos: List<GeneratorInfo<*>>,
  allInfos: List<GeneratorInfo<*>>,
) {
  val toolName = "ru-bizneg_generate_${category.name.lowercase()}"
  val title = "Ru BizGen: ${category.title}"
  val description = buildCategoryDescription(category, categoryInfos)
  val inputSchema = buildCategoryInputSchema(categoryInfos)

  logger.debug("Регистрация тула: name={}, title={}, enumSize={}", toolName, title, categoryInfos.size)

  addTool(
    name = toolName,
    description = description,
    inputSchema = inputSchema,
    title = title,
    toolAnnotations = ToolAnnotations(
      readOnlyHint = true,
      idempotentHint = true,
      destructiveHint = false,
      openWorldHint = false,
    ),
  ) { request ->
    handleCategoryToolCall(category, allInfos, request)
  }
}

/** Обрабатывает вызов категорийного тулa: извлекает `type` и `count` из аргументов, делегирует в [ToolDispatcher]. */
private fun handleCategoryToolCall(
  category: GeneratorCategory,
  allInfos: List<GeneratorInfo<*>>,
  request: CallToolRequest,
): CallToolResult {
  val arguments = request.arguments

  val type = arguments?.get("type")?.jsonPrimitive?.content
  if (type.isNullOrBlank()) {
    return CallToolResult(
      content = listOf(
        TextContent(
          "Обязательный параметр 'type' отсутствует или пуст. " +
              "Доступные значения: ${availableTypes(allInfos, category)}"
        )
      ),
      isError = true,
    )
  }

  val count = arguments["count"]?.jsonPrimitive?.intOrNull ?: 1
  return ToolDispatcher.execute(category, type, count, allInfos)
}

private fun availableTypes(allInfos: List<GeneratorInfo<*>>, category: GeneratorCategory): String =
  allInfos
    .filter { it.category == category }
    .map { TypeKeyResolver.resolve(it) }
    .sorted()
    .joinToString(", ")

/** Строит описание категории для MCP-тула. */
private fun buildCategoryDescription(category: GeneratorCategory, categoryInfos: List<GeneratorInfo<*>>): String {
  val generatorList = categoryInfos.joinToString("; ") { info ->
    "${TypeKeyResolver.resolve(info)} — ${info.name}"
  }

  return "Генерирует случайные тестовые (фейковые) данные категории «${category.title}». " +
      "Доступные генераторы (параметр type): $generatorList. " +
      "Параметр count (1..${ToolDispatcher.MAX_COUNT}, по умолчанию 1) задаёт количество значений. " +
      "Все данные не относятся к реальным субъектам."
}

/** Строит JSON Schema для категорийного тулa: `type` (enum) + `count` (int). */
private fun buildCategoryInputSchema(categoryInfos: List<GeneratorInfo<*>>): ToolSchema {
  val typeKeys = categoryInfos.map { TypeKeyResolver.resolve(it) }

  val enumDescriptions = categoryInfos.joinToString("\n\n") { info ->
    val typeKey = TypeKeyResolver.resolve(info)
    "$typeKey: ${info.detailedDescription} (пример: ${info.example})"
  }

  return ToolSchema(
    properties = buildJsonObject {
      putJsonObject("type") {
        put("type", JsonPrimitive("string"))
        putJsonArray("enum") {
          typeKeys.forEach { add(JsonPrimitive(it)) }
        }
        put(
          "description", JsonPrimitive(
            "Тип генератора. Доступные значения:\n\n$enumDescriptions"
          )
        )
      }
      putJsonObject("count") {
        put("type", JsonPrimitive("integer"))
        put("default", JsonPrimitive(1))
        put("minimum", JsonPrimitive(1))
        put("maximum", JsonPrimitive(ToolDispatcher.MAX_COUNT))
        put("description", JsonPrimitive("Количество значений для генерации (по умолчанию 1)"))
      }
    },
    required = listOf("type"),
  )
}

private fun startStreamableHttpServer(server: Server, host: String, port: Int) {
  embeddedServer(factory = Netty, host = host, port = port) {
    mcpStreamableHttp { server }
    routing {
      get("/health") {
        call.respondText(
          """{"status":"UP"}""",
          contentType = ContentType.Application.Json,
        )
      }
    }
  }.start(wait = true)
}

private fun mcpVersion(): String =
  System.getProperty("mcp.version", "dev")
