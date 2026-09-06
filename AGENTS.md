# Глубокий анализ проекта Ru BizGen

## 0. Работа в репозитории

Этот файл — единый источник истины для всех агентов и для людей. `CLAUDE.md` в корне ссылается сюда и собственного содержимого не имеет:
правки вносим только в `AGENTS.md`.

### 0.1. Язык

Код, KDoc, коммиты, CHANGELOG и документация — **на русском языке**. В Dokka включён `reportUndocumented`, поэтому все публичные и
`protected` API должны быть задокументированы.

Формат коммитов — Conventional Commits; допустимые `type`/`scope`/`footer` перечислены в
`conventionalcommit.json` (scopes: `core`, `plugin`, `mcp`, `perf`, `archunit`, `docs`).

### 0.2. Команды

```bash
# Быстрая проверка: unit-тесты всех модулей + ArchUnit + Kover (то, что гоняет CI на main/dev)
./gradlew check

# Один модуль / один тест-класс / один тест
./gradlew :ru-bizgen-core:test
./gradlew :ru-bizgen-core:test --tests "*InnGeneratorTest*"
./gradlew :ru-bizgen-mcp:test --tests "*ToolDispatcherTest*"
./gradlew :ru-bizgen-plugin:test --tests "*BizGenAppSettingsSoftUpdaterImplTest*"

# Только тесты уникальности (тег distanceFinderTests) — тяжёлые, из обычного check исключены
./gradlew check -PrunDistanceFinderTests

# Сборка плагина / запуск IDE с плагином / верификация
./gradlew :ru-bizgen-plugin:buildPlugin
./gradlew :ru-bizgen-plugin:runIde
./gradlew :ru-bizgen-plugin:verifyPlugin                  # границы диапазона: 2024.2 (sinceBuild) и целевая сборка
./gradlew :ru-bizgen-plugin:verifyPlugin -PverifyAllIdes  # полная матрица (все каналы, ~10 ГБ загрузки)

# UI integration-тесты (реальная IDE + Driver SDK; отключены по умолчанию, ~6-10 мин)
./gradlew :ru-bizgen-plugin:integrationTest -PrunIntegrationTests
# Linux/CI требует xvfb: xvfb-run -a ./gradlew :ru-bizgen-plugin:integrationTest -PrunIntegrationTests

# MCP-сервер
./gradlew :ru-bizgen-mcp:installDist
./ru-bizgen-mcp/build/install/ru-bizgen-mcp/bin/ru-bizgen-mcp --host=0.0.0.0 --port=8081
docker build -f ru-bizgen-mcp/Dockerfile -t ru-bizgen-mcp .   # контекст сборки — корень репозитория

# JMH-бенчмарки и markdown-отчёт для README
./gradlew :ru-bizgen-perf:jmh
./gradlew :ru-bizgen-perf:jmhMarkdownReport

# Документация (Dokka HTML -> build/dokka/html, деплоится на GitHub Pages)
./gradlew dokkaGenerateHtml
```

`-PrunDistanceFinderTests` и `-PrunIntegrationTests` дублируются system property (`-DrunDistanceFinderTests=true`,
`-DrunIntegrationTests=true`) — так их можно включить из VM options IDE.

**Важно:** `-PrunDistanceFinderTests` делает `includeTags("distanceFinderTests")`, то есть запускает **только** distance-тесты и исключает
все остальные (см. `ru-bizgen.testing-convention.gradle.kts`). Полный прогон — это два последовательных запуска; именно так устроен
`ci-all.yml`, причём тяжёлый прогон идёт первым, потому что каждый запуск перезаписывает `test-results` и отчёты Kover.

## 1. Обзор

**Ru BizGen** — генератор российских (и не только) тестовых данных, реализованный в двух форм-факторах: **IntelliJ IDEA Plugin** и
**MCP-сервер**. Все данные генерируются локально, без обращений к внешним сервисам.

- **Версия:** 1.12.261
- **Язык:** Kotlin 2.4.0, JVM 21
- **Сборка:** Gradle (Kotlin DSL), Version Catalog (`libs.versions.toml`), composite build (`build-logic`)
- **Репозиторий:** [GitHub](https://github.com/DmitryEm506/Ru-BizGen), ветки `main`/`dev`
- **Статистика кода:** 204 Kotlin-файла (~5 451 строк main, ~3 850 unit-test, ~1 110 integration, ~382 JMH)

## 2. Архитектура модулей

```
ru-bizgen/
├── build-logic              — composite build: convention plugins (kotlin, testing, dokka, dokka-root, kover)
├── ru-bizgen-core           — ядро: генераторы, инфо-описания, утилиты
├── ru-bizgen-plugin          — IntelliJ IDEA плагин (UI, actions, settings, DI, integration tests)
├── ru-bizgen-mcp             — MCP-сервер (Ktor/Netty + MCP Kotlin SDK)
├── ru-bizgen-perf            — JMH-бенчмарки + генерация markdown-отчётов
├── ru-bizgen-archunit        — ArchUnit: границы модулей, слои core, naming Generator↔Benchmark
├── _polygon                  — Maven-полигон для ручного тестирования (Java, вне Gradle-сборки)
└── build.gradle.kts          — root: Dokka, Kover, общая конфигурация
```

### 2.0. `build-logic` — convention plugins

Composite build (`includeBuild("build-logic")` в `settings.gradle.kts`), содержит 5 convention plugin'ов в `src/main/kotlin/`:

| Plugin                            | Назначение                                                                          |
|-----------------------------------|-------------------------------------------------------------------------------------|
| `ru-bizgen.kotlin-convention`     | `org.jetbrains.kotlin.jvm` + `jvmToolchain(21)` из version catalog                  |
| `ru-bizgen.testing-convention`    | JUnit Platform, управление тегом `distanceFinderTests` (`-PrunDistanceFinderTests`) |
| `ru-bizgen.dokka-convention`      | Dokka HTML-документация (модули)                                                    |
| `ru-bizgen.dokka-root-convention` | Корневая Dokka: агрегация, Guides/презентации, Kover copy                           |
| `ru-bizgen.kover-convention`      | Kover покрытие кода (XML + HTML на `check`)                                         |

Применяются в модулях через `id("ru-bizgen.<name>-convention")`.

### 2.1. `ru-bizgen-core` — ядро генерации

Центральный модуль, не зависящий от IntelliJ Platform. Содержит:

**Ключевые абстракции:**

- `Generator<T>` (`Generator.kt:9`) — интерфейс генератора: `uniqueDistance` + `generate(): GeneratorResult<T>`
- `GeneratorStr` (`GeneratorStr.kt:8`) — специализация для строковых данных
- `GeneratorResult<T>` (`GeneratorResult.kt:12`) — результат с двумя каналами: `toClipboard` (сырое значение) и `toEditor` (значение для
  вставки в редактор, обрамлённое кавычками через `withEscape()`)
- `GeneratorInfo<T>` (`GeneratorInfo.kt:15`) — метаданные: `id`, `name`, `generator`, `category`, `detailedDescription`, `example`. Поля
  `id`/`name` стабильны (ключи персистентного слоя), новые поля — источник данных для MCP enum-описаний.
- `GeneratorStrInfo` (`GeneratorStrInfo.kt:15`) — базовый класс для строковых генераторов
- `GeneratorCategory` (`GeneratorCategory.kt`) — enum категорий: `TECHNICAL`, `BANKING`, `LEGAL`, `GEO`, `PERSONAL` (каждый с
  `title: String`)
- `GeneratorInfoProvider` (`GeneratorInfoProvider.kt:44`) — реестр всех 31 генераторов (singleton object), сгруппированных по
  `GeneratorCategory`

**31 генератор** в 5 категориях:

| Категория    | Генераторы                                                                                                                                |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| Технические  | UUID                                                                                                                                      |
| Банковские   | Расчётный счёт (RUB/CNY), корр. счёт, БИК, SWIFT (8/11), IBAN (RU/TR)                                                                     |
| Юридические  | ИНН (ФЛ/ЮЛ), ОГРН (ИП/ЮЛ), КПП, ОКТМО (8/11), названия организаций (RU/EN)                                                                |
| Геоданные    | Адрес, страна                                                                                                                             |
| Персональные | ФИО (полное/сокращённое/инициалы), карта, телефон (формат/цифры), СНИЛС, паспорт РФ (компактный/с пробелом), загранпаспорт РФ (номер/MRZ) |

**Утилиты:**

- `LuhnAlgorithm` (`LuhnAlgorithm.kt`) — алгоритм Луна для валидации/расчёта контрольных цифр (карты)
- `AccountKeyAlgorithm` (`AccountKeyAlgorithm.kt`) — контрольный ключ лицевого счёта по Положению ЦБ РФ № 515 (счета RUB/CNY, корр. счёт)
- `StringExt.withEscape()` (`StringExt.kt`) — обрамление строки в кавычки

**Алгоритмическая корректность:** Генераторы используют реальные контрольные суммы (ИНН — веса P10/P11/P12, IBAN — mod-97, СНИЛС —
контрольное число, карты — Луна). Это гарантирует прохождение валидации в реальных системах.

**Инвариант `GeneratorInfo.id`.** `id` имеет формат `<TypeName>_<UUID>` и **не меняется никогда**:

- плагин хранит `id` в персистентных настройках (`bizgen_plugin_settings.xml`) — смена `id` сбросит пользователю порядок, активность и
  пользовательские имена генераторов;
- MCP выводит из префикса `id` значение enum `type` (`TypeKeyResolver`: `InnLegal_f5e5...` -> `inn_legal`) — смена `id` ломает внешний
  контракт MCP-инструментов.

`GeneratorMetadataTest` содержит контрольный список всех 31 пар `id -> name` именно для защиты от случайной правки.

**Добавление нового генератора** затрагивает пять мест; если забыть любое из них, `check` упадёт:

1. `ru-bizgen-core/.../generator/impl/XxxGenerator.kt` — реализация `GeneratorStr`/`Generator<T>`.
2. `ru-bizgen-core/.../generator_info/impl/.../XxxGeneratorInfo.kt` — `GeneratorStrInfo` с новым UUID в `id`, `category`,
   `detailedDescription` и `example` (последние два попадают прямо в описание MCP enum).
3. Регистрация в `GeneratorInfoProvider.generatorInfos` — в блоке своей категории (тесты проверяют, что категории идут непрерывными группами
   в фиксированном порядке).
4. `ru-bizgen-perf/src/jmh/.../bench/impl/XxxGeneratorBenchmark.kt` — `BenchmarkNamingArchTest` требует ровно
   `<GeneratorSimpleName>Benchmark` для каждого генератора и совпадения количества с `generatorInfos`.
5. Тест генератора в `ru-bizgen-core/src/test` (наследник `GeneratorBaseTest`) + запись `id -> name`
   в `GeneratorMetadataTest`.

Плагин и MCP при этом менять не нужно — оба подтягивают генератор из реестра автоматически. У существующих пользователей плагина новый
генератор добавится в конец списка активным (`BizGenAppSettingsSoftUpdater`).

Слои внутри core зафиксированы ArchUnit: `utils` <- `generator` <- `generator_info`
(в обратную сторону нельзя).

### 2.2. `ru-bizgen-plugin` — IntelliJ IDEA плагин

**Совместимость:** IntelliJ IDEA 2024.2+ (sinceBuild=242, untilBuild=null), IntelliJ Platform Gradle Plugin 2.18.1.

**Сборка:** Convention plugins из `build-logic` (`kotlin-convention`, `testing-convention`, `dokka-convention`, `kover-convention`) +
`changelog` и `gradleIntelliJPlugin` plugins. Версия IDEA выводится из build number: `1.12.261` → buildNumber `261` → `2026.1`.

**DI-архитектура:** Собственный сервисный слой через `BizGenService` маркер + `getBizGenService<T>()` (`BizGenService.kt:19`), делегирующий
в `ApplicationManager.getApplication().service<T>()`. Сервисы регистрируются в `plugin.xml`.

**Сервисы:**

| Сервис                           | Интерфейс                                      | Реализация                           | Назначение                                                 |
|----------------------------------|------------------------------------------------|--------------------------------------|------------------------------------------------------------|
| `GeneratorActionProvider`        | `GeneratorActionProvider`                      | `GeneratorActionProviderImpl`        | Конвертация `GeneratorInfo` → `BaseGeneratorAction`        |
| `GeneratorActionService`         | `GeneratorActionService`                       | `GeneratorActionServiceImpl`         | Фильтрация активных действий по настройкам                 |
| `AppActionSettingsService`       | `AppActionSettingsService`                     | `AppActionSettingsServiceImpl`       | CRUD настроек (активность, порядок, сброс, переименование) |
| `BizGenAppSettingsRepository`    | `BizGenAppSettingsRepository`                  | `BizGenAppSettingsPersistent`        | PersistentStateComponent (XML)                             |
| `BizGenAppSettingsSoftUpdater`   | `BizGenAppSettingsSoftUpdater` (fun interface) | `BizGenAppSettingsSoftUpdaterImpl`   | Мягкая миграция настроек при обновлении плагина            |
| `NotificationService`            | `NotificationService`                          | `NotificationServiceImpl`            | 3 режима: BELL/HINT/DISABLE                                |
| `BizGenClipboardSettingsService` | `BizGenClipboardSettingsService`               | `BizGenClipboardSettingsServiceImpl` | Управление копированием в буфер                            |
| `EscapeCharSettingsService`      | `EscapeCharSettingsService`                    | `EscapeCharSettingsServiceImpl`      | Символ обрамления при вставке: `"`, `'`, или пусто         |

**Action-система:**

- `BizGenMainAction` (`BizGenMainAction.kt:19`) — главное действие, показывает popup со списком генераторов (Ctrl+Alt+E, Alt+Ins,
  Shift+Shift)
- `BaseGeneratorAction<T>` (`GeneratorAction.kt:36`) — мост между `GeneratorInfo` и `AnAction`: генерация → вставка в редактор через
  `WriteCommandAction` → копирование в буфер → уведомление
- `GeneratorActionProviderImpl` (`GeneratorActionProvider.kt:37`) — конвертация `GeneratorInfo` → анонимный `BaseGeneratorAction`

**Настройки:**

- `BizGenAppSettings` (`BizGenAppSettings.kt`) — конфиг: notificationMode, actualActions (id/position/active/customName), insToClipboard,
  escapeChar
- `BizGenAppSettingsPersistent` (`BizGenAppSettingsPersistent.kt`) — `@State` storage в `bizgen_plugin_settings.xml`, вызывает мягкую
  миграцию при `loadState()`
- `BizGenAppSettingsSoftUpdater` (`BizGenAppSettingsSoftUpdater.kt`) — удаляет устаревшие генераторы, обновляет названия, сохраняет
  `customName`, добавляет новые в конец, пересчитывает позиции
- UI: `AppSettingsConfigurable` → `AppSettingsComponent` (Settings → Tools → Ru BizGen); переименование через `AppActionsSettingComponent`

**Подводные камни:**

- Новый сервис **обязан** быть зарегистрирован как `applicationService` в `plugin.xml` — иначе
  `getBizGenService<T>()` упадёт в рантайме, а не на компиляции.
- Сервисы намеренно разделены на пары «полный интерфейс / `...View`» (`NotificationSettingsService` +
  `NotificationSettingsView`, `BizGenClipboardSettingsService` + `...ServiceView`,
  `EscapeCharSettingsService` + `...ServiceView`) — одна реализация регистрируется под обоими интерфейсами, чтобы код, которому нужно только
  чтение, не получал сеттеры.
- Настройки сохраняются **сразу** при клике через `actionListener`; `AppSettingsConfigurable.isModified()`
  всегда `false`, а `apply()`/`reset()` — заглушки. Не добавляйте логику в `apply()`, не переведя всю форму на обычную модель
  `Configurable`.
- `BizGenSelectedActionEvent` — message-bus топик «в настройках выбран генератор»; подписка через
  `subscribeAsync` всегда доставляет обработчик в EDT (`invokeLater`).
- `BaseIdeaTest` помечен `@RunInEdt`, а EDT-интерцептор платформы не умеет разрешать конструкторы
  `@Nested`-классов — тесты на его основе пишем плоскими.
- Версия IDE **выводится** из версии проекта: `1.12.261` -> buildNumber `261` -> `2026.1`. Смена версии проекта меняет и версию IDE, на
  которой всё компилируется и тестируется.
- **Kotlin stdlib предоставляет платформа** (`kotlin.stdlib.default.dependency=false`), поэтому зависимость на `ru-bizgen-core` объявлена
  с `exclude` на `kotlin-stdlib` и `org.jetbrains:annotations`. Не убирайте этот `exclude`: вторая копия stdlib в `lib/` плагина весит
  1,7 МБ и конфликтует с платформенной на будущих версиях IDE. В самом `ru-bizgen-core` зависимость нужна — ей пользуются `mcp`, `perf`
  и `archunit`.
- Глубокое копирование настроек (`BizGenAppSettings.deepCopy`) сделано round-trip через `com.intellij.util.xmlb.XmlSerializer` — тем же
  сериализатором, которым работает `PersistentStateComponent`. Новые поля и вложенные классы попадают в копию автоматически, сторонняя
  библиотека сериализации для этого не нужна.

**Подпись и публикация:**

- Plugin signing через env vars: `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`
- Publishing через env var: `PUBLISH_TOKEN`
- Plugin verification: каналы RELEASE, RC, PATCH

**Kover:** из покрытия исключены UI-пакет `ru.eda.plgn.bizgen.plugin.ui` и source set `integrationTest` (его классы компилируются вместе с
плагином и иначе считаются непокрытым продакшен-кодом). Фильтр отчёта задаётся дважды — в `ru-bizgen-plugin/build.gradle.kts` для отчёта
модуля и в корневом `build.gradle.kts` для агрегированного отчёта, который и публикуется.

### 2.3. `ru-bizgen-mcp` — MCP-сервер

MCP-сервер на базе **MCP Kotlin SDK 0.15.0** + **Ktor 3.5.1 / Netty**, транспорт — Streamable HTTP.

**Структура:**

- `McpServerApp.kt` — точка входа: парсинг `--host`/`--port`, фабрика `Server`, регистрация 5 категорийных тулов
  `ru-bizgen_generator_<category>`, старт Ktor/Netty + health endpoint `GET /health` (`{"status":"UP"}`)
- `ToolDispatcher.kt` — диспетчер категорийных тулов: lookup `GeneratorInfo` по `(category, type)`, batch-генерация с dedup через
  `Set<String>` + bounded retry, `structuredContent` для `count > 1`, bugfix `CancellationException` (re-throw)
- `TypeKeyResolver.kt` — резолвер type-key: извлекает префикс из `GeneratorInfo.id` (формат `<TypeName>_<UUID>`), CamelCase → snake_case,
  lowercase (например `InnLegal_...` → `inn_legal`)

**5 категорийных тулов** (вместо 31 плоского):

- Каждый тул `ru-bizgen_generator_<category>` принимает `type: enum` (генератор внутри категории) + опциональный `count: int` (1..1000)
- Enum-описания собираются из `GeneratorInfo.detailedDescription` + `example` — без хардкода
- `ToolAnnotations(readOnlyHint=true, idempotentHint=true, destructiveHint=false, openWorldHint=false)` на каждом туле

**Особенности реализации (согласно MCP-спецификации, context7):**

- `ServerCapabilities.Tools(listChanged = false)` — корректно для статического набора из 5 инструментов
- `mcpStreamableHttp { server }` — Streamable HTTP transport (не SSE, не stdio)
- `/health` endpoint — healthcheck
- Версия передаётся через `-Dmcp.version` (JVM property из `applicationDefaultJvmArgs`)
- Docker: multi-stage build (eclipse-temurin:21-jdk → 21-jre), `installDist`, порт 8081
- Тулы объявляют `outputSchema`, поэтому `structuredContent` возвращается в **каждом** успешном ответе, включая `count = 1`; поле `partial`
  присутствует всегда. Ошибочные ответы (`isError = true`)
  структурированный результат не несут — это допускается спецификацией
- `ToolDispatcher` дедуплицирует значения через `LinkedHashSet` с bounded retry (`count * 3` попыток), отказывает при
  `count > uniqueDistance` генератора и **пробрасывает** `CancellationException`
  вместо превращения её в ошибку тула
- Lookup-таблица `(категория, type-key) -> генератор` кэшируется по ссылке на список генераторов
- `ru-bizgen-mcp/Dockerfile` копирует и `ru-bizgen-plugin/`, потому что его `build.gradle.kts` читает
  `description.html` на этапе конфигурации Gradle. Не удаляйте эту копию, не развязав зависимость

### 2.4. `ru-bizgen-perf` — JMH-бенчмарки

- `BaseGeneratorBenchmark<T>` (`BaseGeneratorBenchmark.kt:59`) — базовый класс: `@State(Benchmark)`, `AverageTime` в µs, 3 warmup × 1s, 10
  measurement × 2s, 2 fork, `-Xms1g -Xmx1g -XX:+UseG1GC -XX:+AlwaysPreTouch`, profilers: gc, stack
- `StrGeneratorBenchmark` — обёртка для строковых генераторов
- 31 реализация бенчмарков (по одной на каждый генератор)
- `JmhMarkdownReport.kt` — генерация markdown-отчёта из JSON-результата JMH
- Результаты в README: все генераторы в диапазоне ~0.06–4.0 µs/оп. Таблица снята на JMH 1.36 / JDK 21.0.11; в version catalog сейчас JMH
  1.37 — при обновлении таблицы прогонять `:ru-bizgen-perf:jmhMarkdownReport`

### 2.5. `ru-bizgen-archunit` — архитектурные правила (ArchUnit)

Тестовый модуль (только `src/test`), входит в `check`:

- `ModuleBoundaryArchTest` — границы core/mcp/perf, запрет сторонних deps в ядре, acyclic slices
- `CoreLayerArchTest` — слои `utils` ← `generator` ← `generator_info`
- `BenchmarkNamingArchTest` — 1:1 Generator↔`*Benchmark` + count == `GeneratorInfoProvider` (замена бывшего `ImplementedBenchCheckFormat`)
- JMH-классы через `jmhApiByConf`; plugin-правила (`plugin ↛ mcp/Ktor`) — в `PluginBoundaryArchTest` внутри `ru-bizgen-plugin` (чтобы
  arch-модуль не тянул IntelliJ Platform)
- Интерактивная документация: [`docs/presentations/archunit/index.html`](docs/presentations/archunit/index.html)

## 3. Тестирование

**Фреймворк:** JUnit Jupiter 6.1.0 + Kotest assertions 6.2.1 + MockK 1.14.9 + ArchUnit 1.5.0

**Четыре слоя:**

| Слой                                                                                | Где                                         | Входит в `check`                       |
|-------------------------------------------------------------------------------------|---------------------------------------------|----------------------------------------|
| Unit-тесты ядра (`BaseTest`, `GeneratorBaseTest`, `@TestFactory` на форматы)        | `ru-bizgen-core/src/test`                   | да                                     |
| Тесты уникальности (тег `distanceFinderTests`)                                      | `ru-bizgen-core/src/test/.../find_distance` | нет, только `-PrunDistanceFinderTests` |
| Light UI/сервисные тесты       плагина (`BaseIdeaTest`: `@TestApplication` + `@RunInEdt`) | `ru-bizgen-plugin/src/test`                 | да                                     |
| Driver UI-тесты (IDE Starter + Driver SDK, реальная IDE)                            | `ru-bizgen-plugin/src/integrationTest`      | нет, `-PrunIntegrationTests`, ночью    |

**Структура тестов core:**

- `BaseTest` — базовый класс: `tests()` для dynamic tests, `shouldBeUnique()`
- `GeneratorBaseTest<T>` — базовый для генераторов: проверяет результат на null, уникальность на `uniqueDistance`,
  `testsOnDistanceToClipboard/Editor` для параметризованных проверок формата
- На каждый генератор — свой тест-класс с `@Nested` группами, `@TestFactory` для массовых проверок формата
- `find_distance/` — отдельная категория тестов уникальности (тег `distanceFinderTests`, исключены из быстрого CI через
  `testing-convention`)

**ArchUnit:** `:ru-bizgen-archunit` + `PluginBoundaryArchTest` в plugin

**MCP-тесты:** `ToolDispatcherTest` (успех/ошибка/batch/все генераторы), `TypeKeyResolverTest`

**Plugin unit-тесты:** `BaseIdeaTest`, тесты сервисов (DI, settings, notifications, actions, clipboard, escape char, rename)

**Plugin UI integration-тесты** (source set `integrationTest/`):

- Фреймворк: **JetBrains IDE Starter + Driver SDK** (`TestFrameworkType.Starter`) — запуск реальной IDE, управление UI через Driver
- DI: **Kodein DI 7.33.0** (требуется Starter framework) + `kotlinx-coroutines 1.11.0`
- Структура: `base/_BaseIntegrationTest.kt` (базовый класс с `@EnabledIfSystemProperty(named="runIntegrationTests")`), тесты:
  `BizGenMainActionUITest`, `NotificationAndClipboardSettingsUITest`, `_RuBizGenSettingsUITest`, `_IdeaLifecycleUITest`,
  `AppSettingsComponentUITest`, `AppActionsSettingUITest`, `ActionResultPreviewComponentUITest`
- UI helpers: `base/finder_ext/` — `RadioButtonExt.kt`, `InplaceButtonExt.kt`, `ActionButtomExt.kt`
- Включение: `-PrunIntegrationTests` или `-DrunIntegrationTests=true` (отключены по умолчанию, не входят в `check`)
- Запускаются ночью через `ci-integration.yml`
- Практическое руководство по написанию Driver-тестов: `.agents/skills/driver-ui-tests/SKILL.md` (локальный, не версионируется)

## 4. CI/CD

| Workflow             | Trigger             | Что делает                                                               |
|----------------------|---------------------|--------------------------------------------------------------------------|
| `ci-main.yml`        | push/PR → main      | `check` (fast tests) + `buildPlugin` + `verifyPlugin` + coverage artifact |
| `ci-dev.yml`         | push/PR → dev       | `check` (fast tests) + `buildPlugin` + coverage artifact                 |
| `ci-all.yml`         | `workflow_dispatch` | `check -PrunDistanceFinderTests` (full) + `buildPlugin` + `verifyPlugin` + Dokka |
| `ci-integration.yml` | ночной/schedule     | UI integration-тесты (`-PrunIntegrationTests`): IDE Starter + Driver SDK |
| `docs.yml`           | push → main/dev     | Dokka HTML → GitHub Pages                                                |
| `auto-label.yml`     | PR                  | Авто-лейблинг через labeler                                              |

**Dependabot:** `minor-and-major` group для Gradle deps и GitHub Actions.

**verifyPlugin.** Плагин собирается под одну версию платформы, а совместимым объявлен со всем
диапазоном от `sinceBuild = 242` и выше — несовместимости API на краях диапазона не видны ни на
компиляции, ни в тестах, их ловит только IntelliJ Plugin Verifier. Поэтому он обязателен в
`ci-main.yml` (гейт перед публикацией) и не гоняется в `ci-dev.yml`, чтобы push в dev оставался
быстрым. Список IDE задаётся в `pluginVerification` (`ru-bizgen-plugin/build.gradle.kts`):

- по умолчанию — **края диапазона**, три точки: `MIN_SUPPORTED_IDE` (должна совпадать с
  `sinceBuild`), целевая сборка и `LATEST_IDE` — последний вышедший релиз платформы. Третья точка
  нужна из-за `untilBuild = null`: это заявка на совместимость со всеми будущими версиями, и
  проверять её надо на каждом релизе, а не в момент перехода на него. **`LATEST_IDE` обновляется
  вручную при выходе новой версии IDEA.** Тип IDE берётся тем же правилом, что и платформа в
  `dependencies`: с 2025.3 (253) отдельного Community-дистрибутива нет, остаётся единый
  `IntellijIdea`;
- `-PverifyAllIdes` — **полная матрица** `select { channels = RELEASE, RC, PATCH }`. Тянет по
  дистрибутиву Ultimate на каждую версию диапазона (09.2026 — 8 сборок, ~10 ГБ), поэтому включается
  вручную входом `verifyAllIdes` в `ci-all.yml`, который перед прогоном чистит диск раннера.

Полная матрица использует инсталляторы с `download.jetbrains.com`, а он отдаёт **HTTP 451** из РФ,
так что локально работает только конфигурация по умолчанию: она резолвит дистрибутивы из
intellij-repository.

## 5. Качество и документация

- **Kover 0.9.9** — покрытие кода, HTML+XML отчёты, интегрировано в Dokka footer (исключены UI-пакет `ru.eda.plgn.bizgen.plugin.ui` и source
  set `integrationTest`)
- **Dokka 2.2.0** — HTML-документация с source links на GitHub, деплой на GitHub Pages; Guides: презентации ArchUnit и Gradle
  (`docs/presentations/`)
- **KDoc** — все публичные API задокументированы на русском
- **JetBrains Verified** — плагин верифицирован, плагин-подпись через environment variables

**Презентации.** `dokka-root-convention` копирует в корневой Dokka-сайт отчёт Kover (`images/kover/`)
и презентации. Новая презентация = `docs/presentations/<slug>/index.html` — она автоматически попадает во вкладку Guides через
сгенерированный `guides.json` (`<title>` и `<meta name="description">` берутся из самого HTML). Скрыть презентацию из Guides можно через
`guides: false` во frontmatter её `outline.md`. Контракт шаблона — `docs/presentation-template/PUBLISH_DOKKA.md`.

## 6. Архитектурные наблюдения

**Сильные стороны:**

1. **Чистая сепарация ядра** — `ru-bizgen-core` не зависит от IntelliJ, переиспользуется в MCP
2. **Единый реестр генераторов** — `GeneratorInfoProvider` — единый source of truth для plugin и MCP
3. **Алгоритмическая корректность** — все генераторы используют реальные контрольные суммы
4. **Мягкая миграция настроек** — `BizGenAppSettingsSoftUpdater` сохраняет пользовательские настройки при обновлении
5. **Стабильные MCP type-key** — `TypeKeyResolver` извлекает ключ из UUID-based id, а не из имени класса; имена тулов —
   `ru-bizgen_generator_<category>`
6. **Performance-first** — JMH-бенчмарки на каждый генератор + ArchUnit naming/полнота (`BenchmarkNamingArchTest`)
7. **Convention plugins** — `build-logic` composite build централизует конфигурацию Kotlin/testing/Dokka/Kover
8. **UI integration tests** — JetBrains IDE Starter + Driver SDK для end-to-end тестирования плагина в реальной IDE
9. **ArchUnit** — исполняемые границы модулей и слои core (ядро без IntelliJ/Ktor/MCP/JMH)

**Зоны для внимания:**

1. **`_polygon`** — Maven-модуль-полигон вне Gradle-сборки, не включён в `settings.gradle.kts`, но директория существует. Лучше переместить
   или исключить из репозитория.
2. **Dockerfile** копирует `ru-bizgen-plugin/` при сборке MCP (т.к. его `build.gradle.kts` читает `description.html` на конфигурации) —
   хрупкая связь между модулями.
3. **Тесты `distanceFinderTests`** исключены из CI по умолчанию (только manual `ci-all`) — стоит оценить добавление в ночной/weekly запуск.
4. **Переход на IDEA 2026.2+ упирается в Java 25, а не в API.** 2026.2 (ветка 262) — первая
   платформа, собранная под Java 25 (`dependencies.txt`: `jdkBuild=25.0.2`, классы `lib/util.jar`
   мажор 69). Сборка под неё падает на `AppNotificationComponent.kt` с `Cannot inline bytecode built
   with JVM target 25 into bytecode that is being built with JVM target 21` — инлайновые функции
   Kotlin UI DSL вкомпилируются в классы плагина, поэтому `jvmTarget` обязан совпасть с
   платформенным. Байткод 25 не грузится на JBR 21 (2024.2: `runtimeBuild=21.0.3`), то есть переход
   на 262 стоит `sinceBuild 242 → 262` и обрывает поддержку всех версий ниже 2026.2 разом. При этом
   артефакт, собранный под 2026.1, верификатор признаёт совместимым с 2026.2 — новая IDE работает
   без перехода. Решение отложено: `sinceBuild = 242` и так ограничивает плагин API уровня 2024.2,
   так что переход не даёт ничего, кроме потери аудитории, и со временем только дешевеет.
   Дату брать из статистики установок по версиям IDE в кабинете вендора Marketplace.
5. **Версия MCP** передаётся через JVM property (`-Dmcp.version`) из `applicationDefaultJvmArgs`. При запуске без неё сервер сообщает версию
   `dev` — compile-time альтернатива (генерируемый `McpBuildConfig`) осталась нереализованной.

## 7. Технологический стек (summary)

| Компонент                              | Версия        |
|----------------------------------------|---------------|
| Kotlin                                 | 2.4.0         |
| JDK                                    | 21            |
| IntelliJ Platform Plugin               | 2.18.1        |
| MCP Kotlin SDK                         | 0.15.0        |
| Ktor                                   | 3.5.1         |
| JMH                                    | 1.37          |
| JUnit Jupiter                          | 6.1.0         |
| Kotest                                 | 6.2.1         |
| MockK                                  | 1.14.9        |
| ArchUnit                               | 1.5.0         |
| Dokka                                  | 2.2.0         |
| Kover                                  | 0.9.9         |
| Changelog Plugin                       | 2.5.0         |
| Kodein DI (integration tests)          | 7.33.0        |
| kotlinx-coroutines (integration tests) | 1.11.0        |
| kotlinx-serialization-json             | 1.11.0        |
| slf4j-simple                           | 2.0.17        |
| IDE Starter + Driver SDK               | 261.22158.277 |
| Gradle Configuration Cache             | enabled       |
| Gradle Build Cache                     | enabled       |
| Gradle Parallel                        | enabled       |
