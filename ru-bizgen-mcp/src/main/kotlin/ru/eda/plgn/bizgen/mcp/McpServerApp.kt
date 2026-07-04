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
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.LoggerFactory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

private val logger = LoggerFactory.getLogger("McpServerApp")

/**
 * Точка входа в процесс запуска MCP сервера.
 *
 * @property args входные аргументы. Обрабатываются следующие аргументы: host, port
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

  logger.info("Запуск ru-bizgen MCP-сервера version{} на {}:{}", version, host, port)
  logger.info("Зарегистрировано инструментов: {}", infos.size)

  val toolNames = ToolNameResolver.resolveAll(infos)
  toolNames.forEach { (id, name) ->
    logger.debug("Инструмент: id={} -> name={}", id, name)
  }

  val server = createBizGenServer(version, infos)

  startStreamableHttpServer(server, host, port)
}

private fun createBizGenServer(
  version: String,
  infos: List<ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo<*>>,
): Server = Server(
  serverInfo = Implementation(name = "ru-bizgen", version = version),
  options = ServerOptions(
    capabilities = ServerCapabilities(
      tools = ServerCapabilities.Tools(listChanged = false),
    ),
  ),
) {
  infos.forEach { info ->
    addTool(
      name = ToolNameResolver.resolve(info),
      description = "${info.name}. Генерирует случайные тестовые (фейковые) данные, не относящиеся к реальным субъектам.",
      inputSchema = ToolSchema(properties = buildJsonObject { }),
    ) {
      ToolExecutor.execute(info)
    }
  }
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
