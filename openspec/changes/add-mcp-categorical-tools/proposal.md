## Why

MCP-сервер `ru-bizgen-mcp` сейчас регистрирует 31 отдельный тул (`generate_inn_legal`, `generate_iban_ru`, ...) — по одному на каждый генератор из `GeneratorInfoProvider`. Все тулы структурно идентичны: ноль параметров, одинаковое шаблонное описание, единственное различие — замкнутый `GeneratorInfo`. При 31 генераторе сервер находится на пороге деградации точности выбора LLM (research: >90% точности до ~15 тулов, резкое падение после ~30). Добавление новых генераторов усугубляет проблему. Дополнительно нулевая параметризация тулов делает невозможной пакетную генерацию (`count`), что блокирует распространённый сценарий «сгенерируй 5 тестовых клиентов со всеми реквизитами одним вызовом». MCP-сервер находится на стадии проектирования — изменения не затрагивают существующих потребителей и можно свободно реструктурировать.

## What Changes

- **BREAKING**: Замена 31 плоского тулов вида `generate_<type>` на **5 категорийных тулов-диспетчеров**: `generate_technical`, `generate_banking`, `generate_legal`, `generate_geo`, `generate_personal`. Каждый тул принимает `type: enum[...]` (конкретные генераторы в категории) и опциональный `count: int`. Имена существующих тулов (`generate_inn_legal` и т.д.) исчезают — это допустимо, так как MCP-сервер не имеет внешних потребителей.
- Добавление `count` (batch-генерация) с дефолтом 1 и верхним лимитом; уникальность через `Set` + bounded retry (по умолчанию), опираясь на `uniqueDistance`.
- Новые поля в интерфейсе ядра `GeneratorInfo<T>`:
  - `category: GeneratorCategory` (новый enum `TECHNICAL | BANKING | LEGAL | GEO | PERSONAL`)
  - `detailedDescription: String` (расширенное описание для LLM enum + tooltip плагина)
  - `example: String` (стабильный литерал-пример значения — НЕ `generate()`)
- MCP строит `enum` значений и их описания полностью из полей ядра (`category`, `detailedDescription`, `example`, `name`) — без хардкода описаний в MCP-слое.
- Добавление `ToolAnnotations` (`readOnlyHint=true`, `idempotentHint=true`, `openWorldHint=false`, `destructiveHint=false`) и `title` ко всем категорийным тулам.
- Опциональный `structuredContent` в `CallToolResult` для `count > 1` (машинно-читаемый ответ `{type, count, values[]}`).
- Bugfix в `ToolExecutor`: перехват `CancellationException` сейчас глотается (`catch (e: Exception)`) — ломает structured concurrency MCP SDK на корутинах. Исправляется фильтром `NonCancellationException` или re-throw.
- Bugfix в `ToolNameResolver.cache` (`mutableMapOf`) — заменяется на `ConcurrentHashMap`, либо (после реструктурирования) класс выводится из обращения.
- `ToolNameResolver` в текущем виде (парсинг `id`-префикса для 31 имени тула) удаляется — заменяется на вычисление `type`-key из `id` (строка после `_` до UUID) и группировку по `category`.
- Перегруппировка `GeneratorInfoProvider.generatorInfos` по категориям (формализация существующих комментариев-разделителей). Порядок `id` и `name` генераторов не меняется → персистентный слой плагина (`BizGenAppSettingsSoftUpdater`, `bizgen_plugin_settings.xml`) работает без миграции схемы.

## Capabilities

### New Capabilities
- `mcp-categorical-tools`: Экспозиция генераторов через 5 категорийных MCP-тулов-диспетчеров с параметрами `type` (enum) и `count` (batch), с `ToolAnnotations`, `structuredContent` и обработкой ошибок, соответствующей structured concurrency.
- `generator-metadata`: Метаданные генераторов в ядре — `category`, `detailedDescription`, `example` в `GeneratorInfo`, и enum `GeneratorCategory`. Единый источник истины для MCP enum-описаний и будущей группировки в плагине.

### Modified Capabilities
<!-- Существующих spec-файлов в openspec/specs/ нет — все капабилити новые. -->

## Impact

- **Ядро `ru-bizgen-core`**:
  - `GeneratorInfo.kt`: +3 поля (`category`, `detailedDescription`, `example`).
  - Новый `GeneratorCategory.kt` (enum).
  - `GeneratorStrInfo.kt`: проброс новых полей в конструктор (abstract `val` или параметры).
  - 31 impl-класс в `generator_info/impl/**`: добавление литералов `category`, `detailedDescription`, `example`. Механическая правка, `id`/`name`/`generator` не меняются.
  - `GeneratorInfoProvider.kt`: перегруппировка списка по `category` (формализация комментариев). `id` каждого генератора сохраняется.
  - `Generator`, `GeneratorResult`, `GeneratorStr`, `GeneratorResultWithEscape`, `GeneratorResultAsIs` — БЕЗ ИЗМЕНЕНИЙ.
- **MCP-слой `ru-bizgen-mcp`**:
  - `McpServerApp.kt`: цикл `infos.forEach { addTool }` заменяется на `infos.groupBy { it.category }.forEach { addCategoryTool }`. Регистрация 5 тулов с `enum`+`count` из данных ядра.
  - `ToolNameResolver.kt`: удаляется (или сводится к вычислению `type`-key из `id` для lookup).
  - `ToolExecutor.kt`: обобщается до `ToolDispatcher` (принимает `type`, `count`; ищет `GeneratorInfo`, батчит с dedup). Bugfix `CancellationException`. Bugfix `cache` потокобезопасности либо удаление.
  - Тесты `ToolExecutorTest`, `ToolNameResolverTest`: переписываются под новую структуру.
- **Плагин `ru-bizgen-plugin`**:
  - **Обратно совместимо**. `GeneratorActionProviderImpl` использует `id`, `name`, `generator` — новые поля игнорируются.
  - `BizGenAppSettingsSoftUpdater` ключён по `id`, использует `info.name` — не затрагивается.
  - `bizgen_plugin_settings.xml` — структура НЕ меняется, миграция НЕ требуется.
  - Future bonus: категория и `detailedDescription` доступны плагину для группировки popup и tooltip'ов (за рамками этого change).
- **Тесты ядра**: добавление тестов на заполненность `category`, `detailedDescription`, `example` для всех 31 генераторов; валидация `example` через форматы (где применимо).
- **Перф `ru-bizgen-perf` / `ru-bizgen-perf-validation`**: не затрагиваются (наследники `Generator`, не `GeneratorInfo`).
- **Зависимости**: без изменений (`MCP Kotlin SDK 0.14.0`, `Ktor 3.4.3` — `ToolSchema`, `ToolAnnotations`, `CallToolResult.structuredContent` уже поддерживаются).