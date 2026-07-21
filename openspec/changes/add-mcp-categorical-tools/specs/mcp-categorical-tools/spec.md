## ADDED Requirements

### Requirement: Categorical tool dispatcher

MCP-сервер SHALL экспонировать 5 тулов-диспетчеров — по одному на каждую категорию `GeneratorCategory` (`TECHNICAL`, `BANKING`, `LEGAL`, `GEO`, `PERSONAL`). Имена тулов формируются как `generate_<lowercase(category)>` (например `generate_legal`). Каждый тул принимает параметры `type: enum[<доступные генераторы категории>]` (обязательный) и `count: int` (опциональный, default=1).

#### Scenario: Регистрируется ровно 5 тулов
- **WHEN** MCP-сервер стартует и клиент запрашивает `tools/list`
- **THEN** возвращается ровно 5 тулов с именами `generate_technical`, `generate_banking`, `generate_legal`, `generate_geo`, `generate_personal`

#### Scenario: Имя тула определяется категорией
- **WHEN** берётся `GeneratorInfo` с `category = GeneratorCategory.LEGAL`
- **THEN** соответствующий тул имеет имя `generate_legal`

#### Scenario: Нет плоских тулов вида generate_<type>
- **WHEN** клиент запрашивает `tools/list`
- **THEN** в списке отсутствуют имена, не соответствующие шаблону `generate_<category>` (например `generate_inn_legal`, `generate_iban_ru`)

### Requirement: Type parameter is enum of available generators

Параметр `type` каждого категорийного тула SHALL описываться как JSON Schema `enum` со списком значений, доступных в категории. Значение enum вычисляется из `GeneratorInfo.id` (префикс до `_` + UUID, преобразованный в snake_case, в нижнем регистре — например `inn_legal`). MCP-сервер НЕ хардкодит список enum, а строит его из `GeneratorInfoProvider` во время старта.

#### Scenario: Enum значения соответствуют генераторам
- **WHEN** клиент запрашивает `tools/list` и берёт тул `generate_legal`
- **THEN** параметр `type` содержит enum со значениями, вычисленными из `id` каждого `GeneratorInfo` категории `LEGAL` (например `inn_legal`, `inn_individual`, `kpp`, `ogrn_legal`, `ogrn_ip`, `oktmo8`, `oktmo11`, `org_ru_name`, `org_eng_name`)

#### Scenario: Enum описания берутся из метаданных ядра
- **WHEN** клиент читает описание enum-значения в `inputSchema`
- **THEN** текст содержит `detailedDescription` и `example` соответствующего `GeneratorInfo` из `GeneratorInfoProvider`

#### Scenario: Новые генераторы автоматически попадают в enum
- **WHEN** в `GeneratorInfoProvider` добавляется новый `GeneratorInfo` с `category = BANKING` и сервер перезапускается
- **THEN** enum параметра `type` у `generate_banking` автоматически включает новое значение без правок MCP-слоя

### Requirement: Count parameter enables batch generation

Параметр `count` SHALL добавлять пакетную генерацию: при `count = N` тул возвращает `N` значений. `count` опционален (default=1), `minimum=1`, имеет верхний предел (защита от abuse). При `count = 1` поведение эквивалентно одиночному вызову (как в предыдущей плоской схеме).

#### Scenario: Одиночная генерация при count по умолчанию
- **WHEN** клиент вызывает тул без указания `count`
- **THEN** возвращается ровно одно сгенерированное значение

#### Scenario: Пакетная генерация при count > 1
- **WHEN** клиент вызывает тул с `count = 5`
- **THEN** возвращается 5 сгенерированных значений

#### Scenario: Отказ при count выше uniqueDistance
- **WHEN** клиент вызывает тул с `count`, превышающим `uniqueDistance` соответствующего генератора (по умолчанию 130)
- **THEN** тул возвращает `CallToolResult` с `isError = true` и пояснением о невозможности гарантировать уникальность

#### Scenario: Отказ при count выше верхнего предела
- **WHEN** клиент вызывает тул с `count`, превышающим верхний предел (например 1000)
- **THEN** тул возвращает `CallToolResult` с `isError = true` и пояснением о превышении лимита

### Requirement: Batch uniqueness via set plus retry

При `count > 1` тул SHALL обеспечивать уникальность значений через `Set` + bounded retry (не более `3 × count` попыток генерации). Если после лимита попыток набрано меньше уникальных значений, чем `count`, возвращается набранное количество с явной пометкой в `structuredContent` (поле `partial = true`).

#### Scenario: Все значения уникальны при достаточном entropy
- **WHEN** клиент вызывает тул с `count = 10` для генератора ИНН
- **THEN** возвращаются 10 различных строк, ни одна не дублирует другую

#### Scenario: Частичный результат при недостаточном entropy
- **WHEN** клиент вызывает тул с `count`, близким к `uniqueDistance`, и генератору не хватает попыток для достижения уникальности
- **THEN** возвращается `structuredContent` с `partial = true` и фактическим количеством значений в поле `values`

### Requirement: Structured content for batch responses

При `count > 1` тул SHALL возвращать `structuredContent` в формате `{ "type": "<type>", "count": <count>, "values": [<сгенерированные значения>], "partial": <опционально true> }` в дополнение к `content` (TextContent со значениями, разделёнными переносом строки). При `count = 1` `structuredContent` MAY быть опущен.

#### Scenario: Structured content присутствует при batch
- **WHEN** клиент вызывает тул с `count = 3`
- **THEN** `CallToolResult.structuredContent` содержит JSON-объект с полями `type`, `count`, `values`

#### Scenario: Structured content отсутствует при одиночной генерации
- **WHEN** клиент вызывает тул с `count = 1` (явно или по умолчанию)
- **THEN** `CallToolResult.structuredContent` равен null

### Requirement: Tool annotations declared

Каждый категорийный тул SHALL декларировать `ToolAnnotations` со значениями `readOnlyHint = true`, `idempotentHint = true`, `destructiveHint = false`, `openWorldHint = false`. Каждый тул SHALL иметь `title` в формате `"Ru BizGen: <человекочитаемое имя категории>"`.

#### Scenario: Все тулы декларируют read-only поведение
- **WHEN** клиент запрашивает `tools/list`
- **THEN** каждый тул в массиве `annotations` имеет `readOnlyHint = true`

#### Scenario: Все тулы декларируют закрытый мир
- **WHEN** клиент запрашивает `tools/list`
- **THEN** каждый тул в массиве `annotations` имеет `openWorldHint = false`

#### Scenario: Все тулы имеют title
- **WHEN** клиент запрашивает `tools/list`
- **THEN** каждый тул содержит непустое поле `title`, начинающееся с префикса `"Ru BizGen: "`

### Requirement: Type dispatches to correct generator

Тул SHALL находить `GeneratorInfo` по значению параметра `type` (через lookup-таблицу `type → GeneratorInfo`, построенную из `GeneratorInfoProvider` при старте). Несуществующее значение `type` возвращает `CallToolResult` с `isError = true` и пояснением.

#### Scenario: Валидный type вызывает нужный генератор
- **WHEN** клиент вызывает `generate_legal` с `type = "inn_legal"`
- **THEN** выполняется `InnLegalGeneratorInfo.generator.generate()` и возвращается валидный ИНН юр. лица

#### Scenario: Несуществующий type возвращает ошибку
- **WHEN** клиент вызывает `generate_legal` с `type = "nonexistent"`
- **THEN** `CallToolResult` имеет `isError = true` и сообщение о неизвестном значении `type`

#### Scenario: type из другой категории возвращает ошибку
- **WHEN** клиент вызывает `generate_legal` с `type = "iban_ru"` (banking-генератор)
- **THEN** `CallToolResult` имеет `isError = true` и сообщение что `type` не принадлежит категории тулa

### Requirement: CancellationException propagated

`ToolDispatcher` (преемник `ToolExecutor`) SHALL НЕ перехватывать `CancellationException`. При отмене запроса (таймаут, отмена сессии) корутина должна корректно завершаться через structured concurrency, а не глотаться как generic `Exception`.

#### Scenario: Отмена запроса не маскируется ошибкой генератора
- **WHEN** во время выполнения тулa корутина получает `CancellationException` (например при таймауте MCP-клиента)
- **THEN** исключение пробрасывается дальше, и `CallToolResult` с `isError = true` НЕ формируется для cancellation

#### Scenario: Бизнес-исключения всё ещё обрабатываются
- **WHEN** во время выполнения тулa `generator.generate()` выбрасывает `IllegalStateException`
- **THEN** формируется `CallToolResult` с `isError = true` и сообщением исключения (как раньше)