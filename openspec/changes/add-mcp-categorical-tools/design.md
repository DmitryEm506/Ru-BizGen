## Context

`ru-bizgen-mcp` экспонирует 31 генератор из `GeneratorInfoProvider` как 31 отдельных MCP-тул (`McpServerApp.kt:61-69`). Все тулы структурно идентичны: ноль параметров, шаблонное описание, делегирование в `ToolExecutor.execute(info)`. При 31 генераторе сервер на пороге деградации точности выбора LLM (research Anthropic/AWS/arxiv: >90% точности до ~15 тулов, резкое падение после ~30). Batch-генерация (`count`) невозможна без правки 31 тулa.

Ядро `ru-bizgen-core` минимально: `GeneratorInfo<T>` содержит только `id`, `name`, `generator`. Категории генераторов существуют как комментарии в `GeneratorInfoProvider.kt:44-83` («Технические», «Организации», «Банки и платёжные реквизиты», «Адресно-территориальные», «Персональные»), но не типизированы. `name` несёт формат в скобках: `"ИНН ЮЛ (10)"`, `"IBAN RU (33)"`. `uniqueDistance = 130` у всех генераторов (95% уникальность при 130 вызовах, парадокс дней рождения).

Плагин `ru-bizgen-plugin` использует `GeneratorInfo` через `GeneratorActionProviderImpl.kt:55-57` (только `id`, `name`, `generator`). Персистентный слой `BizGenAppSettingsPersistent` / `BizGenAppSettingsSoftUpdater` ключён по `id`, использует `info.name` для `PersistenceActionSetting.description`. Хранилище — `bizgen_plugin_settings.xml`, структура не отражает категорий. MCP-слой полностью изолирован от плагина (`ru-bizgen-mcp` зависит только от `ru-bizgen-core`).

MCP-сервер на стадии проектирования — внешних потребителей нет, можно свободно ломать имена тулов.

## Goals / Non-Goals

**Goals:**
- Сжать 31 плоский тул до 5 категорийных тулов-диспетчеров (`generate_<category>`) с параметрами `type: enum` + `count: int`.
- Ввести в ядро метаданные `category`, `detailedDescription`, `example` как единый источник истины для MCP enum-описаний (без хардкода в MCP-слое).
- Обеспечить batch-генерацию (`count > 1`) с уникальностью через `Set` + bounded retry.
- Поправить bug `CancellationException` в `ToolExecutor` (`catch (e: Exception)` глотает cancellation → ломает structured concurrency MCP SDK).
- Сохранить обратную совместимость с персистентным слоем плагина: `GeneratorInfo.id` и `name` не меняются, `bizgen_plugin_settings.xml` не требует миграции.

**Non-Goals:**
- Не добавлять параметры генерации предметного характера (регион для INN, пол для ФИО) — за рамками этого change.
- Не внедрять категорийную группировку в UI плагина (`BizGenMainAction` popup) — future work, плагин лишь получает доступ к `category`/`detailedDescription` «бесплатно», но не использует пока.
- Не менять `Generator`, `GeneratorResult`, `GeneratorStr`, контрольные суммы — ядро генерации стабильно.
- Не вводить `outputSchema` (только `structuredContent`) — для LLM достаточно `TextContent`, машинно-читаемая структура опциональна.
- Не разбивать MCP-сервер на несколько серверов — 5 тулов в одном сервере в зелёной зоне точности.

## Decisions

### D1. 5 категорийных тулов-диспетчеров (вариант B), а не один диспетчер (C)

**Решение:** 5 тулов `generate_<category>` (TECHNICAL, BANKING, LEGAL, GEO, PERSONAL), каждый с `type: enum[<доступные генераторы категории>]` + опциональный `count: int`.

**Альтернативы:**
- **C (один тул `generate(type: enum[31], count?)`)**: максимальная компрессия (-92% токенов), но enum из 31 значения — на границе区域 деградации выбора LLM. При росте до 50+ генераторов деградирует быстрее. Потеря «именованности» — LLM видит `generate`, а не `generate_legal`, весь discoverability на качестве enum-описаний.
- **Гибрид (один тул с `category` + `type`)**: лишний round-trip в reasoning (сначала категория, потом тип), два параметра вместо одного выбора по имени тула.

**Рationale:** Категорийные тулы дают второй уровень фильтрации (имя тула = подсказка категории → enum внутри короче 4-12 значений). LLM «дай ИНН» → `generate_legal` → enum из 9. Для «дай адрес» → `generate_geo` → enum из 4. Сжатие -80% токенов определений при сохранении discoverability. 5 тулов глубоко в зелёной зоне точности (~15 тулов/контекст). Категории уже существуют как комментарии в ядре → formalизация естественна.

### D2. Метаданные в ядре `GeneratorInfo` (3 поля), а не в MCP-слое

**Решение:** Добавить в `GeneratorInfo<T>`:
- `category: GeneratorCategory` (новый enum `TECHNICAL | BANKING | LEGAL | GEO | PERSONAL`)
- `detailedDescription: String` — расширенное описание (что это + формат + контр. сумма) для MCP enum и tooltip плагина
- `example: String` — стабильный литерал-пример значения (НЕ `generate()`)

Свернуть `format` в `detailedDescription` (как текст) — структурированный объект `format{length, charset, checksum}` был бы переинжинирингом под гетерогенные форматы (INN digits, MRZ multiline, free-text org name). `length` уже живёт в `name` («ИНН ЮЛ (10)»).

**Альтернативы:**
- **Категоризация только в MCP-слое** (`ToolCategoryResolver`): ядро не трогается, но создаётся два места правды (комментарии в `GeneratorInfoProvider` + маппинг в MCP). При добавлении генератора можно забыть обновить MCP-маппинг. Плагин не получает категорию «бесплатно».
- **`example` через `generate()`**: динамический пример, но ломает кэш LLM (значение меняется между сессиями), side-effect в конструкторе, нетестабельно как литерал.

**Rationale:** Категории уже существуют в комментариях — formalизация ничего не изобретает. Стоимость: 31 тривиальная правка impl-классов (+3 литерала в каждом), 2 новых поля в интерфейсе, 1 enum. Обратно совместимо для плагина (он игнорирует новые поля). Единый source of truth: MCP строит enum из `GeneratorInfoProvider` без хардкода. Тест-страховка: компилятор заставит заполнить `abstract val` / параметры конструктора — нельзя «забыть».

**Реализация в `GeneratorInfo`:** новые поля как `abstract val` в интерфейсе (или параметры в `GeneratorStrInfo`), forcing explicit implementation. `UuidGeneratorInfo` (единственный не-`GeneratorStrInfo`, `T = UUID`) тоже получает 3 поля — `example` пишется строкой UUID, `detailedDescription` описывает UUID v4.

### D3. `type` key = префикс `id` до `_` + UUID

**Решение:** Enum-значение для каждого генератора = часть `GeneratorInfo.id` до `_` + 8 hex-цифр, преобразованная в snake_case (существующая логика `ToolNameResolver.compute` без префикса `generate_`). Например `id="InnLegal_f5e5e2b3-..."` → `type="inn_legal"`. MCP хранит `Map<String, GeneratorInfo<*>>` (key = `type`) для O(1) lookup по `type` из аргументов LLM.

**Rationale:** Использует существующую稳定ность `id` (персистентный слой плагина уже ключён по `id`). Не вводит нового ключа. `ToolNameResolver` в текущем виде удаляется — его логика вычисления `type`-key сохраняется в новом `ToolDispatcher` / lookup-структуре.

### D4. `count` с dedup через `Set` + bounded retry

**Решение:** `count: int`, default=1, `minimum=1`, `maximum` ограничен сверху (см. риски). Механика: `Set<String>` + цикл retry до `count`, но не более `3 × count` попыток. Если после лимита попыток уникальных меньше `count` — возвращается то, что набрано, с пометкой в `structuredContent`. `count > uniqueDistance(=130)` → `isError=true` с пояснением (нельзя гарантировать уникальность).

**Альтернативы:**
- **Без гарантии уникальности**: проще, быстрее, но дубликаты возможны (редкие для UUID, но для генераторов с малым entropy возможно чаще).
- **Жёсткий dedup без retry-лимита**: риск бесконечного цикла при малом entropy.

**Rationale:** `uniqueDistance = 130` у всех — статистическая 95% оценка. Для `count ≤ 130` retry практически всегда succeeds с первого раза (entropy высокий), bounded overhead. Для `count > 130` — честный отказ.

### D5. `structuredContent` опционален, для `count > 1`

**Решение:** Для `count == 1` — `TextContent(value)` (как сейчас). Для `count > 1` — `TextContent(values.joinToString("\n"))` + `structuredContent = { "type": "...", "count": N, "values": [...] }`. Без `outputSchema` (не обязательно).

**Rationale:** LLM-клиенты читают `TextContent`; программные клиенты могут читать `structuredContent`. Оба формата в одном ответе, мигимальная совместимость.

### D6. `ToolAnnotations` + `title`

**Решение:** Все 5 категорийных тулов декларируют `ToolAnnotations(readOnlyHint=true, idempotentHint=true, destructiveHint=false, openWorldHint=false)` и `title = "Ru BizGen: <категория>"`. Человекочитаемый заголовок категории берётся из нового поля (см. ниже) или хардкода для 5 значений (допустимо).

**Rationale:** Все генераторы чистые, детерминированные, не трогают внешний мир. Текущий код не декларирует это — упущенная best-practice. `title` улучшает UI в MCP-клиентах.

### D7. Группировка в `GeneratorInfoProvider` по `category`

**Решение:** `GeneratorInfoProvider.generatorInfos` перегруппировывается: вместо исторического порядка (UUID → Account×2 → INN×2 → ...) — по `category` (TECHNICAL, BANKING, LEGAL, GEO, PERSONAL). `id` и `name` каждого генератора не меняются.

**Rationale:** Формализация существующих комментариев. Порядок внутри категории сохраняется как todays для минимума diff. Персистентный слой не затрагивается: `BizGenAppSettingsSoftUpdater` не пересчитывает `position` существующих действий при изменении порядка провайдера (`updateActualActions` копирует `description` по `id`, `addNewActualActions` только для новых `id`). Существующие пользователи сохраняют свой порядок; новые получают дефолтный по категориям (улучшение UX).

### D8. Bugfix `CancellationException` в `ToolExecutor`

**Решение:** `catch (e: Exception)` → фильтр `if (e is CancellationException) throw e` (или `catch (e: NonCancellationException)`). MCP SDK на корутинах, перехват cancellation ломает structured concurrency при отмене сессии/таймауте.

## Risks / Trade-offs

- **[LLM discoverability при enum-описаниях слабого качества] → Mitigation:** `detailedDescription` + `example` в каждом генераторе, покрытие тестами на заполненность. `example` даёт LLM конкретный образец формата. Документирование договорённости: `name` короткое человекочитаемое, `detailedDescription` развёрнутое для LLM.
- **[Забыть заполнить новые поля при добавлении генератора] → Mitigation:** `abstract val` в интерфейсе → компилятор заставит. Тест ядра: для всех `GeneratorInfoProvider.generatorInfos` утверждать что `category`, `detailedDescription`, `example` не пустые.
- **[`example` литерал устарел / не проходит валидацию] → Mitigation:** Тест: валидация `example` через реальный формат где возможно (INN — контр. сумма, IBAN — mod-97, карта — Luhn). В категорию `find_distance/` тестов.
- **[Breaking для гипотетических потребителей имён тулов] → Mitigation:** MCP-сервер на стадии проектирования, потребителей нет. Изменение задокументировано как BREAKING в proposal.
- **[`count > uniqueDistance` честный отказ может удивить] → Mitigation:** `isError=true` с понятным сообщением, лимит `count` по умолчанию существенно ниже `uniqueDistance` (например 1000), `uniqueDistance` публикуется в описании enum или в error-сообщении.
- **[Дисбаланс категорий: TECHNICAL=1 (только UUID), PERSONAL=11] → Mitigation:** Допустимо — 1-значный enum корректен. При росте TECHNICAL/CAP до >3 можно перебалансировать позже. Не блокирует.
- **[Маппинг `id→type` хрупкий (regex-парсинг)] → Mitigation:** Тестируется явно (`ToolNameResolverTest` уже покрывает). Альтернатива — новый явный `typeKey: String` поле в `GeneratorInfo`, но это 4-е поле и дублирование информации `id`. Оставляем regex-извлечение из существующего `id`, проверенное тестами.
- **[`uuid` пример как String в не-String `T=UUID` генераторе] → Mitigation:** `example: String` deliberately (не `T`) — избегает generic-усложнений. `UuidGeneratorInfo.example = "550e8400-e29b-41d4-a716-446655440000"`. Описано в detailedDescription: «UUID v4 как строка».

## Migration Plan

- **Core:** добавить `GeneratorCategory` enum, поля в `GeneratorInfo`/`GeneratorStrInfo`, обновить 31 impl-класс. Запустить тесты ядра — должны пройти (кроме новых метаданных-тестов, добавляются).
- **Plugin:** без изменений. `GeneratorActionProviderImpl` игнорирует новые поля. `BizGenAppSettingsSoftUpdater` ключён по `id`/`name` — не затрагивается. `bizgen_plugin_settings.xml` не мигрируется.
- **MCP:** переписать `McpServerApp` (5 тулов из `groupBy(category)`), заменить `ToolNameResolver` + `ToolExecutor` на `ToolDispatcher`. Тесты переписываются. Bugfix `CancellationException`.
- **Rollback:** revert трёх модулей. Ядро backward-compatible (новые поля можно удалить). Плагин не затронут. MCP — самостоятельный модуль, rollback изолирован.
- **No deployment coordination needed** — MCP-сервер на стадии проектирования, без внешних потребителей.

## Open Questions

- **OQ1:** Верхний лимит `count` — 1000 (защита от abuse) или другое значение? Ориентир: `uniqueDistance=130`, сценарий «5-50 тестовых клиентов». Предлагаю 1000 как щедрый дефолт.
- **OQ2:** Заголовок категории (`title` тула) — хардкод 5 строк в MCP или вынести в `GeneratorCategory.description`? Хардкод 5 строк допустим (мало), но enum-поле чище. Решу в tasks как minor.
- **OQ3:** Нужно ли `outputSchema` (JSON Schema для `structuredContent`)? MCP SDK поддерживает. Для LLM не нужно. Для программных клиентов — nice-to-have. Откладываю (non-goal).