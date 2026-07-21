## 1. Метаданные ядра — `GeneratorCategory` enum

- [x] 1.1 Создать `ru-bizgen-core/src/main/kotlin/ru/eda/plgn/bizgen/core/generator_info/GeneratorCategory.kt` с enum `GeneratorCategory { TECHNICAL, BANKING, LEGAL, GEO, PERSONAL }`. Каждый enum-элемент имеет `val title: String` (человекочитаемое имя, например "Технические данные") для `title` MCP-тулов. Добавить KDoc.
- [x] 1.2 (опционально, решить OQ2) Если решено — вынести человекочитаемые имена в `GeneratorCategory.title` (используется в `title` тула); иначе хардкод 5 строк в MCP-слое.

## 2. Метаданные ядра — расширение `GeneratorInfo`

- [x] 2.1 Добавить в `GeneratorInfo.kt` три новых поля (как `abstract val`): `category: GeneratorCategory`, `detailedDescription: String`, `example: String`. Обновить KDoc интерфейса, указав что `id`/`name` стабильны, а новые поля — источник данных для MCP enum и будущей группировки плагина.
- [x] 2.2 Расширить `GeneratorStrInfo.kt`: добавить `category`, `detailedDescription`, `example` как параметры конструктора (после `generator`), пробросить в `GeneratorInfo<String>`. Сохранить backward-совместимость сигнатуры для наследников.
- [x] 2.3 Адаптировать `UuidGeneratorInfo.kt` (единственный не-`GeneratorStrInfo`, `T = UUID`): добавить `override val category = GeneratorCategory.TECHNICAL`, `override val detailedDescription = "..."`, `override val example = "550e8400-e29b-41d4-a716-446655440000"`. `id` и `name` НЕ менять.

## 3. Заполнение метаданных в 31 impl-классе

- [x] 3.1 Заполнить `category`, `detailedDescription`, `example` для генераторов категории `TECHNICAL` (UUID — уже в 2.3).
- [x] 3.2 Заполнить метаданные для категории `BANKING`: `AccountRubGeneratorInfo` (AccountRub, BankAccount, Bik, Swift8, Swift11, IbanRu, IbanTurkish). `id`/`name`/`generator` не менять. `example` — конкретные валидные значения, проходящие контрольные суммы (mod-97, Луна, ключ счёта).
- [x] 3.3 Заполнить метаданные для категории `LEGAL`: `InnIndividual`, `InnLegal`, `Kpp`, `OgrnLegal`, `OgrnIp`, `Oktmo8`, `Oktmo11`, `OrgRuName`, `OrgEngName`. `example` для INN/ОГРН/КПП проходит контрольную сумму.
- [x] 3.4 Заполнить метаданные для категории `GEO`: `Address`, `Country`.
- [x] 3.5 Заполнить метаданные для категории `PERSONAL`: `CardNumber`, `FIOFull`, `FIOShort`, `FIOInitials`, `PhoneNumberRuFormat`, `PhoneNumberRuDigit`, `Snils`, `PassportRuSpaced`, `PassportRuCompact`, `ForeignPassportRuNumber`, `ForeignPassportRuMrz`. `example` для карты/SNILS/загранпаспорта проходит валидацию (Луна, контр. число SNILS, MRZ-формат).

## 4. Перегруппировка `GeneratorInfoProvider`

- [x] 4.1 Перегруппировать `GeneratorInfoProvider.generatorInfos` по `category` в порядке `TECHNICAL, BANKING, LEGAL, GEO, PERSONAL`. Внутри категории сохранить историческое следование генераторов (минимизировать diff). `id` каждого генератора НЕ меняется. Комментарии-разделители теперь избыточны — удалить или заменить на `// <category> (<count>)`.
- [x] 4.2 Запустить существующие тесты ядра, убедиться что не сломалось (тесты ключены по `id`, не по порядку).

## 5. Тесты метаданных ядра

- [x] 5.1 Создать `GeneratorMetadataTest.kt` в `ru-bizgen-core/src/test/`:_assert_ что для всех `GeneratorInfoProvider.generatorInfos` поля `category`, `detailedDescription`, `example` непустые. `detailedDescription` хотя бы N символов, `example` не blank.
- [x] 5.2 В `GeneratorMetadataTest` _assert_ что `category` каждого генератора соответствует ожидаемой категории (параметризованный тест по `id`-префиксу → `GeneratorCategory`).
- [x] 5.3 В `GeneratorMetadataTest` _assert_ что `id` и `name` всех 31 генераторов не изменились по сравнению с реестром-контрольным списком (защита от случайной правки идентификаторов — критично для персистентного слоя).
- [x] 5.4 Добавить валидацию `example` через реальный формат для генераторов с контрольной суммой: INN (P10/P11/P12), OGRN, KPP, Snils, CardNumber (Luhn), IbanRu/IbanTurkish (mod-97), BankAccount (ключ счёта). Использовать существующие алгоритмы `core/utils` (LuhnAlgorithm и др.). Нестандартные (UUID, адрес, страна, ФИО, наименование организации) — проверка только формата (regex/длина).
- [x] 5.5 Добавить тест `GeneratorInfoProviderTest`: `_assert_` что генераторы сгруппированы по категориям (TECHNICAL идет первым, PERSONAL — последним, без разрывов внутри категории).

## 6. Тип-ключ: lookup-таблица

- [x] 6.1 Реализовать вычисление `type`-key из `GeneratorInfo.id` (префикс до `_` + 8 hex, в snake_case, lowercase — например `InnLegal_f5e5e2b3-...` → `inn_legal`). Логика повторяет существующий `ToolNameResolver.compute` без префикса `generate_`. Вынести в `object TypeKeyResolver` (или приватно в новом `ToolDispatcher`).
- [x] 6.2 Тест `TypeKeyResolverTest`: для всех 31 `GeneratorInfo` _assert_ что `type`-key соответствует ожидаемому (`inn_legal`, `iban_ru`, `uuid`, ...). Параметризованный по `id`.
- [x] 6.3 Тест: `_assert_` уникальность `type`-key для всех генераторов (дубликаты → ошибка).

## 7. MCP: реструктуризация тула-диспетчера

- [x] 7.1 Заменить `ToolExecutor.kt` на `ToolDispatcher.kt`: принимает `category: GeneratorCategory`, `type: String`, `count: Int`, `infos: List<GeneratorInfo<*>>`. Находит `GeneratorInfo` по `(category, type)` через lookup-таблицу, построенную из `infos` при старте (или injected). Неудачный lookup → `CallToolResult(isError=true)`.
- [x] 7.2 Реализовать batch-генерацию в `ToolDispatcher`: цикл `generate()` с `Set<String>` + bounded retry (≤ `3 × count` попыток). При `count > uniqueDistance` → `isError=true`. При `count > MAX_COUNT` → `isError=true`. При наборе меньше `count` → `structuredContent.partial = true`.
- [x] 7.3 Реализовать формирование ответа в `ToolDispatcher`: при `count == 1` — `TextContent(value)` без `structuredContent`; при `count > 1` — `TextContent(values.joinToString("\n"))` + `structuredContent = { "type", "count", "values" }` (и опционально `"partial": true`).
- [x] 7.4 Bugfix `CancellationException`: в `ToolDispatcher.execute` перехватывать `Exception` но re-throw `CancellationException` (или `catch (e: NonCancellationException)`). Покрыть тестом: mock `generator.generate()` выбрасывает `CancellationException` → результат `isError != true`, исключение проброшено.

## 8. MCP: регистрация 5 тулов

- [x] 8.1 В `McpServerApp.kt` заменить цикл `infos.forEach { addTool }` на `infos.groupBy { it.category }.forEach { (category, categoryInfos) -> addCategoryTool(category, categoryInfos) }`.
- [x] 8.2 Реализовать `addCategoryTool(category, infos)`: имя `generate_${category.name.lowercase()}`, `title = "Ru BizGen: ${category.title}"` (или хардкод), `description` — обзор категории (что входит).
- [x] 8.3 Построить `inputSchema` как `ToolSchema(properties = { "type": { "type":"string", "enum":[<typeKeys>], "description": <enumerated descriptions из detailedDescription + example> }, "count": { "type":"integer", "default":1, "minimum":1, "maximum": MAX_COUNT } }, required = ["type"])`. Enum-описания и значения собираются из `categoryInfos` (`detailedDescription`, `example`) — без хардкода.
- [x] 8.4 Добавить `ToolAnnotations(readOnlyHint=true, idempotentHint=true, destructiveHint=false, openWorldHint=false)` к каждому из 5 тулов.
- [x] 8.5 В обработчике вызова: извлечь `type` и `count` из `arguments`, вызвать `ToolDispatcher.execute(category, type, count, allInfos)`. Обработать отсутствие обязательного `type` → `isError=true`.

## 9. MCP: очистка устаревшего

- [x] 9.1 Удалить `ToolNameResolver.kt` (логика `type`-key переехла в `TypeKeyResolver` / `ToolDispatcher`, логика проверки уникальности имён 31 тулов больше не нужна). Удалить его `cache` (bug mutableMapOf, не потокобезопасен).
- [x] 9.2 Удалить или переписать `ToolNameResolverTest.kt`: тесты `type`-key переехла в `TypeKeyResolverTest`, тесты уникальности drop ( enumerate из 5 enum-ов тривиально уникальны).
- [x] 9.3 Убедиться что `ToolExecutor.kt` удалён полностью, ссылок на него не осталось.

## 10. MCP: тесты диспетчера

- [x] 10.1 Переписать `ToolExecutorTest.kt` → `ToolDispatcherTest.kt`: успешное выполнение для всех 31 генераторов (как раньше), `isError=false`, `content.size == 1` для одиночного вызова.
- [x] 10.2 Тест batch: `count = 5` → 5 значений, все уникальны. `count = 1` (default) → 1 значение.
- [x] 10.3 Тест `count > uniqueDistance` → `isError=true` и сообщение.
- [x] 10.4 Тест `count > MAX_COUNT` → `isError=true` и сообщение.
- [x] 10.5 Тест несуществующего `type` → `isError=true`.
- [x] 10.6 Тест `type` из другой категории → `isError=true` (например `generate_legal` + `type="iban_ru"`).
- [x] 10.7 Тест `structuredContent`: при `count = 3` присутствует `{type, count, values}`, при `count = 1` — null.
- [x] 10.8 Тест `partial = true`: mock генератор с малым entropy, `count` близкий к `uniqueDistance`, assert что `structuredContent.partial == true` и `values.size < count`.
- [x] 10.9 Тест `CancellationException` не маскируется: mock `generator.generate()` бросает `CancellationException`, assert что исключение проброшено (не сформирован `CallToolResult` с `isError=true`).
- [x] 10.10 Тест бизнес-исключений: mock `generator.generate()` бросает `IllegalStateException` → `isError=true` с сообщением (как раньше).

## 11. Проверка сборки и регрессии

- [x] 11.1 Запустить `./gradlew :ru-bizgen-core:test` — все тесты зелёные (существующие + новые метаданные).
- [x] 11.2 Запустить `./gradlew :ru-bizgen-mcp:test` — все тесты зелёные (новый ToolDispatcher + TypeKey, удалённый ToolName/Executor).
- [x] 11.3 Запустить `./gradlew :ru-bizgen-plugin:test` — плагин не затронут, тесты зелёные (обратная совместимость).
- [x] 11.4 Запустить `./gradlew check` (fast tests без distance finder) — full build зелёный.
- [x] 11.5 (опционально) Запустить `./gradlew :ru-bizgen-perf-validation:test` — бенчмарк-валидация не затронута (генераторы не менялись).
- [ ] 11.6 Вручную проверить: старт MCP-сервера `./gradlew :ru-bizgen-mcp:run`, `curl /health` возвращает UP, `tools/list` через MCP-клиент возвращает ровно 5 тулов с правильными enum.

## 12. Документация

- [x] 12.1 Обновить KDoc в `GeneratorInfo.kt`, `GeneratorCategory.kt`, `ToolDispatcher.kt`, `McpServerApp.kt` (на русском, по конвенции проекта).
- [x] 12.2 Обновить AGENTS.md: исправить счётчик генераторов (26 → 31), описать новую MCP-структуру (5 категорийных тулов вместо 31 плоских), поля метаданных ядра.
- [ ] 12.3 (опционально) Обновить README в части MCP-сервера, если там описаны tools.
- [x] 12.4 Зафиксировать константу `MAX_COUNT` (решение OQ1: предлагаю 1000) как `const val` в `ToolDispatcher` или `McpServerApp` с KDoc-обоснованием.