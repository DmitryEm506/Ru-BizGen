# Глубокий анализ проекта Ru BizGen

## 1. Обзор

**Ru BizGen** — генератор российских (и не только) тестовых данных, реализованный в двух форм-факторах: **IntelliJ IDEA Plugin** и **MCP-сервер**. Все данные генерируются локально, без обращений к внешним сервисам.

- **Версия:** 1.12.261
- **Язык:** Kotlin 2.4.0, JVM 21
- **Сборка:** Gradle (Kotlin DSL), Version Catalog (`libs.versions.toml`), composite build (`build-logic`)
- **Репозиторий:** [GitHub](https://github.com/DmitryEm506/Ru-BizGen), ветки `main`/`dev`
- **Статистика кода:** 170 Kotlin-файлов (~4 671 строк main, ~2 394 строк test, ~352 строк JMH)

## 2. Архитектура модулей

```
ru-bizgen/
├── build-logic              — composite build: convention plugins (kotlin, testing, dokka, kover)
├── ru-bizgen-core           — ядро: генераторы, инфо-описания, утилиты
├── ru-bizgen-plugin          — IntelliJ IDEA плагин (UI, actions, settings, DI, integration tests)
├── ru-bizgen-mcp             — MCP-сервер (Ktor/Netty + MCP Kotlin SDK)
├── ru-bizgen-perf            — JMH-бенчмарки + генерация markdown-отчётов
├── ru-bizgen-archunit        — ArchUnit: границы модулей, слои core, naming Generator↔Benchmark
├── _polygon                  — Maven-полигон для ручного тестирования (Java, вне Gradle-сборки)
└── build.gradle.kts          — root: Dokka, Kover, общая конфигурация
```

### 2.0. `build-logic` — convention plugins

Composite build (`includeBuild("build-logic")` в `settings.gradle.kts`), содержит 4 convention plugin'а в `src/main/kotlin/`:

| Plugin | Назначение |
|---|---|
| `ru-bizgen.kotlin-convention` | `org.jetbrains.kotlin.jvm` + `jvmToolchain(21)` из version catalog |
| `ru-bizgen.testing-convention` | JUnit Platform, управление тегом `distanceFinderTests` (`-PrunDistanceFinderTests`) |
| `ru-bizgen.dokka-convention` | Dokka HTML-документация (модули) |
| `ru-bizgen.dokka-root-convention` | Корневая Dokka: агрегация, Guides/презентации, Kover copy |
| `ru-bizgen.kover-convention` | Kover покрытие кода |

Применяются в модулях через `id("ru-bizgen.<name>-convention")`.

### 2.1. `ru-bizgen-core` — ядро генерации

Центральный модуль, не зависящий от IntelliJ Platform. Содержит:

**Ключевые абстракции:**
- `Generator<T>` (`Generator.kt:9`) — интерфейс генератора: `uniqueDistance` + `generate(): GeneratorResult<T>`
- `GeneratorStr` (`GeneratorStr.kt:8`) — специализация для строковых данных
- `GeneratorResult<T>` (`GeneratorResult.kt:12`) — результат с двумя каналами: `toClipboard` (сырое значение) и `toEditor` (значение для вставки в редактор, обрамлённое кавычками через `withEscape()`)
- `GeneratorInfo<T>` (`GeneratorInfo.kt:15`) — метаданные: `id`, `name`, `generator`, `category`, `detailedDescription`, `example`. Поля `id`/`name` стабильны (ключи персистентного слоя), новые поля — источник данных для MCP enum-описаний.
- `GeneratorStrInfo` (`GeneratorStrInfo.kt:15`) — базовый класс для строковых генераторов
- `GeneratorCategory` (`GeneratorCategory.kt`) — enum категорий: `TECHNICAL`, `BANKING`, `LEGAL`, `GEO`, `PERSONAL` (каждый с `title: String`)
- `GeneratorInfoProvider` (`GeneratorInfoProvider.kt:44`) — реестр всех 31 генераторов (singleton object), сгруппированных по `GeneratorCategory`

**31 генератор** в 5 категориях:

| Категория | Генераторы |
|---|---|
| Технические | UUID |
| Банковские | Расчётный счёт (RUB/CNY), корр. счёт, БИК, SWIFT (8/11), IBAN (RU/TR) |
| Юридические | ИНН (ФЛ/ЮЛ), ОГРН (ИП/ЮЛ), КПП, ОКТМО (8/11), названия организаций (RU/EN) |
| Геоданные | Адрес, страна |
| Персональные | ФИО (полное/сокращённое/инициалы), карта, телефон (формат/цифры), СНИЛС, паспорт РФ (компактный/с пробелом), загранпаспорт РФ (номер/MRZ) |

**Утилиты:**
- `LuhnAlgorithm` (`LuhnAlgorithm.kt:10`) — алгоритм Луна для валидации/расчёта контрольных цифр (карты, счета)
- `StringExt.withEscape()` (`StringExt.kt:10`) — обрамление строки в кавычки

**Алгоритмическая корректность:** Генераторы используют реальные контрольные суммы (ИНН — веса P10/P11/P12, IBAN — mod-97, СНИЛС — контрольное число, карты — Луна). Это гарантирует прохождение валидации в реальных системах.

### 2.2. `ru-bizgen-plugin` — IntelliJ IDEA плагин

**Совместимость:** IntelliJ IDEA 2024.2+ (sinceBuild=242, untilBuild=null), IntelliJ Platform Gradle Plugin 2.17.0.

**Сборка:** Convention plugins из `build-logic` (`kotlin-convention`, `testing-convention`, `dokka-convention`, `kover-convention`) + `changelog` и `gradleIntelliJPlugin` plugins. Версия IDEA выводится из build number: `1.12.261` → buildNumber `261` → `2026.1`.

**DI-архитектура:** Собственный сервисный слой через `BizGenService` маркер + `getBizGenService<T>()` (`BizGenService.kt:19`), делегирующий в `ApplicationManager.getApplication().service<T>()`. Сервисы регистрируются в `plugin.xml`.

**Сервисы:**

| Сервис | Интерфейс | Реализация | Назначение |
|---|---|---|---|
| `GeneratorActionProvider` | `GeneratorActionProvider` | `GeneratorActionProviderImpl` | Конвертация `GeneratorInfo` → `BaseGeneratorAction` |
| `GeneratorActionService` | `GeneratorActionService` | `GeneratorActionServiceImpl` | Фильтрация активных действий по настройкам |
| `AppActionSettingsService` | `AppActionSettingsService` | `AppActionSettingsServiceImpl` | CRUD настроек (активность, порядок, сброс) |
| `BizGenAppSettingsRepository` | `BizGenAppSettingsRepository` | `BizGenAppSettingsPersistent` | PersistentStateComponent (XML) |
| `BizGenAppSettingsSoftUpdater` | `BizGenAppSettingsSoftUpdater` (fun interface) | `BizGenAppSettingsSoftUpdaterImpl` | Мягкая миграция настроек при обновлении плагина |
| `NotificationService` | `NotificationService` | `NotificationServiceImpl` | 3 режима: BELL/HINT/DISABLE |
| `BizGenClipboardSettingsService` | `BizGenClipboardSettingsService` | `BizGenClipboardSettingsServiceImpl` | Управление копированием в буфер |

**Action-система:**
- `BizGenMainAction` (`BizGenMainAction.kt:19`) — главное действие, показывает popup со списком генераторов (Ctrl+Alt+E, Alt+Ins, Shift+Shift)
- `BaseGeneratorAction<T>` (`GeneratorAction.kt:36`) — мост между `GeneratorInfo` и `AnAction`: генерация → вставка в редактор через `WriteCommandAction` → копирование в буфер → уведомление
- `GeneratorActionProviderImpl` (`GeneratorActionProvider.kt:37`) — конвертация `GeneratorInfo` → анонимный `BaseGeneratorAction`

**Настройки:**
- `BizGenAppSettings` (`BizGenAppSettings.kt:12`) — конфиг: notificationMode, actualActions (список с id/position/active), insToClipboard
- `BizGenAppSettingsPersistent` (`BizGenAppSettingsPersistent.kt:25`) — `@State` storage в `bizgen_plugin_settings.xml`, вызывает мягкую миграцию при `loadState()`
- `BizGenAppSettingsSoftUpdater` (`BizGenAppSettingsSoftUpdater.kt:31`) — удаляет устаревшие генераторы, обновляет названия, добавляет новые в конец, пересчитывает позиции
- UI: `AppSettingsConfigurable` → `AppSettingsComponent` (Settings → Tools → Ru BizGen)

**Подпись и публикация:**
- Plugin signing через env vars: `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`
- Publishing через env var: `PUBLISH_TOKEN`
- Plugin verification: каналы RELEASE, RC, PATCH

**Kover:** пакет `ru.eda.plgn.plugin.bizgen.ui` исключён из покрытия.

### 2.3. `ru-bizgen-mcp` — MCP-сервер

MCP-сервер на базе **MCP Kotlin SDK 0.14.0** + **Ktor 3.5.1 / Netty**, транспорт — Streamable HTTP.

**Структура:**
- `McpServerApp.kt` — точка входа: парсинг `--host`/`--port`, фабрика `Server`, регистрация 5 категорийных тулов, старт Ktor/Netty + health endpoint
- `ToolDispatcher.kt` — диспетчер категорийных тулов: lookup `GeneratorInfo` по `(category, type)`, batch-генерация с dedup через `Set<String>` + bounded retry, `structuredContent` для `count > 1`, bugfix `CancellationException` (re-throw)
- `TypeKeyResolver.kt` — резолвер type-key: извлекает префикс из `GeneratorInfo.id` (формат `<TypeName>_<UUID>`), CamelCase → snake_case, lowercase (например `InnLegal_...` → `inn_legal`)

**5 категорийных тулов** (вместо 31 плоского):
- Каждый тул `generate_<category>` принимает `type: enum` (генератор внутри категории) + опциональный `count: int` (1..1000)
- Enum-описания собираются из `GeneratorInfo.detailedDescription` + `example` — без хардкода
- `ToolAnnotations(readOnlyHint=true, idempotentHint=true, destructiveHint=false, openWorldHint=false)` на каждом туле

**Особенности реализации (согласно MCP-спецификации, context7):**
- `ServerCapabilities.Tools(listChanged = false)` — корректно для статического набора из 5 инструментов
- `mcpStreamableHttp { server }` — Streamable HTTP transport (не SSE, не stdio)
- `/health` endpoint — healthcheck
- Версия передаётся через `-Dmcp.version` (JVM property из `applicationDefaultJvmArgs`)
- Docker: multi-stage build (eclipse-temurin:21-jdk → 21-jre), `installDist`, порт 8081

### 2.4. `ru-bizgen-perf` — JMH-бенчмарки

- `BaseGeneratorBenchmark<T>` (`BaseGeneratorBenchmark.kt:59`) — базовый класс: `@State(Benchmark)`, `AverageTime` в µs, 5 warmup × 1s, 10 measurement × 2s, 2 fork, `-Xms2g -Xmx2g -XX:+UseG1GC`, profilers: gc, stack
- `StrGeneratorBenchmark` — обёртка для строковых генераторов
- 26 реализаций бенчмарков (по одной на каждый генератор)
- `JmhMarkdownReport.kt` — генерация markdown-отчёта из JSON-результата JMH
- Результаты в README: все генераторы в диапазоне 0.05–3.75 µs/оп

### 2.5. `ru-bizgen-archunit` — архитектурные правила (ArchUnit)

Тестовый модуль (только `src/test`), входит в `check`:

- `ModuleBoundaryArchTest` — границы core/mcp/perf, запрет сторонних deps в ядре, acyclic slices
- `CoreLayerArchTest` — слои `utils` ← `generator` ← `generator_info`
- `BenchmarkNamingArchTest` — 1:1 Generator↔`*Benchmark` + count == `GeneratorInfoProvider` (замена бывшего `ImplementedBenchCheckFormat`)
- JMH-классы через `jmhApiByConf`; plugin-правила (`plugin ↛ mcp/Ktor`) — в `PluginBoundaryArchTest` внутри `ru-bizgen-plugin` (чтобы arch-модуль не тянул IntelliJ Platform)
- Интерактивная документация: [`docs/presentations/archunit/index.html`](docs/presentations/archunit/index.html)

## 3. Тестирование

**Фреймворк:** JUnit Jupiter 6.1.0 + Kotest assertions 6.2.1 + MockK 1.14.9 + ArchUnit 1.4.2

**Структура тестов core:**
- `BaseTest` — базовый класс: `tests()` для dynamic tests, `shouldBeUnique()`
- `GeneratorBaseTest<T>` — базовый для генераторов: проверяет результат на null, уникальность на `uniqueDistance`, `testsOnDistanceToClipboard/Editor` для параметризованных проверок формата
- На каждый генератор — свой тест-класс с `@Nested` группами, `@TestFactory` для массовых проверок формата
- `find_distance/` — отдельная категория тестов уникальности (тег `distanceFinderTests`, исключены из быстрого CI через `testing-convention`)

**ArchUnit:** `:ru-bizgen-archunit` + `PluginBoundaryArchTest` в plugin

**MCP-тесты:** `ToolExecutorTest` (успех/ошибка/все генераторы), `ToolNameResolverTest`

**Plugin unit-тесты:** `BaseIdeaTest`, тесты сервисов (DI, settings, notifications, actions, clipboard)

**Plugin UI integration-тесты** (source set `integrationTest/`):
- Фреймворк: **JetBrains IDE Starter + Driver SDK** (`TestFrameworkType.Starter`) — запуск реальной IDE, управление UI через Driver
- DI: **Kodein DI 7.20.2** (требуется Starter framework) + `kotlinx-coroutines 1.10.1`
- Структура: `base/_BaseIntegrationTest.kt` (базовый класс с `@EnabledIfSystemProperty(named="runIntegrationTests")`), тесты: `BizGenMainActionUITest`, `NotificationAndClipboardSettingsUITest`, `_RuBizGenSettingsUITest`, `_IdeaLifecycleUITest`
- UI helpers: `base/finder_ext/RadioButtonExt.kt`, `settings/ext/SettingsDialogUiComponentExt.kt`
- Включение: `-PrunIntegrationTests` или `-DrunIntegrationTests=true` (отключены по умолчанию, не входят в `check`)
- Запускаются ночью через `ci-integration.yml`
- Дизайн-спецификации: `docs/superpowers/specs/2026-07-19-integration-tests-feasibility-spike-design.md`, `docs/superpowers/specs/2026-07-22-ui-test-coverage-gradual-plan.md`

## 4. CI/CD

| Workflow | Trigger | Что делает |
|---|---|---|
| `ci-main.yml` | push/PR → main | `check` (fast tests) + `buildPlugin` + coverage artifact |
| `ci-dev.yml` | push/PR → dev | То же, что ci-main |
| `ci-all.yml` | `workflow_dispatch` | `check -PrunDistanceFinderTests` (full) + `buildPlugin` + Dokka |
| `ci-integration.yml` | ночной/schedule | UI integration-тесты (`-PrunIntegrationTests`): IDE Starter + Driver SDK |
| `docs.yml` | push → main/dev | Dokka HTML → GitHub Pages |
| `auto-label.yml` | PR | Авто-лейблинг через labeler |

**Dependabot:** `minor-and-major` group для Gradle deps и GitHub Actions.

## 5. Качество и документация

- **Kover 0.9.9** — покрытие кода, HTML+XML отчёты, интегрировано в Dokka footer (пакет `ru.eda.plgn.plugin.bizgen.ui` исключён)
- **Dokka 2.2.0** — HTML-документация с source links на GitHub, деплой на GitHub Pages
- **KDoc** — все публичные API задокументированы на русском
- **JetBrains Verified** — плагин верифицирован, плагин-подпись через environment variables

## 6. Архитектурные наблюдения

**Сильные стороны:**
1. **Чистая сепарация ядра** — `ru-bizgen-core` не зависит от IntelliJ, переиспользуется в MCP
2. **Единый реестр генераторов** — `GeneratorInfoProvider` — единый source of truth для plugin и MCP
3. **Алгоритмическая корректность** — все генераторы используют реальные контрольные суммы
4. **Мягкая миграция настроек** — `BizGenAppSettingsSoftUpdater` сохраняет пользовательские настройки при обновлении
5. **Стабильные MCP-имена** — `ToolNameResolver` извлекает имя из UUID-based id, а не из имени класса
6. **Performance-first** — JMH-бенчмарки на каждый генератор + ArchUnit naming/полнота (`BenchmarkNamingArchTest`)
7. **Convention plugins** — `build-logic` composite build централизует конфигурацию Kotlin/testing/Dokka/Kover
8. **UI integration tests** — JetBrains IDE Starter + Driver SDK для end-to-end тестирования плагина в реальной IDE
9. **ArchUnit** — исполняемые границы модулей и слои core (ядро без IntelliJ/Ktor/MCP/JMH)

**Зоны для внимания:**
1. **`_polygon`** — Maven-модуль-полигон вне Gradle-сборки, не включён в `settings.gradle.kts`, но директория существует. Лучше переместить или исключить из репозитория.
2. **Dockerfile** копирует `ru-bizgen-plugin/` при сборке MCP (т.к. его `build.gradle.kts` читает `description.html` на конфигурации) — хрупкая связь между модулями.
3. **Тесты `distanceFinderTests`** исключены из CI по умолчанию (только manual `ci-all`) — стоит оценить добавление в ночной/weekly запуск.
4. **Версия MCP** передаётся через JVM property (`-Dmcp.version`), хотя в design-spec (`docs/superpowers/specs/`) предлагался переход на compile-time `McpBuildConfig.kt` — спецификация написана, но реализация использует runtime-подход.

## 7. Технологический стек (summary)

| Компонент | Версия |
|---|---|
| Kotlin | 2.4.0 |
| JDK | 21 |
| IntelliJ Platform Plugin | 2.17.0 |
| MCP Kotlin SDK | 0.14.0 |
| Ktor | 3.5.1 |
| JMH | 1.37 |
| JUnit Jupiter | 6.1.0 |
| Kotest | 6.2.1 |
| MockK | 1.14.9 |
| ArchUnit | 1.4.2 |
| Dokka | 2.2.0 |
| Kover | 0.9.9 |
| Changelog Plugin | 2.5.0 |
| Kodein DI (integration tests) | 7.20.2 |
| kotlinx-coroutines (integration tests) | 1.10.1 |
| kotlinx-serialization-json | 1.11.0 |
| slf4j-simple | 2.0.17 |
| IDE Starter + Driver SDK | 261.22158.277 |
| Gradle Configuration Cache | enabled |
| Gradle Build Cache | enabled |
| Gradle Parallel | enabled |
