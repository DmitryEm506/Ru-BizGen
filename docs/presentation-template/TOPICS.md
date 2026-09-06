# Реестр презентаций Ru BizGen

Список тем для вкладки **Guides** в Dokka: что уже есть и что планируем. Как делать новую тему — [`README.md`](README.md). Публикация — [
`PUBLISH_DOKKA.md`](PUBLISH_DOKKA.md).

Продуктовый бэклог и стратегия развития → [`../strategy/`](../strategy/) ([`STRATEGY.md`](../strategy/STRATEGY.md)).

Статусы:

| Статус      | Значение                                                   |
|-------------|------------------------------------------------------------|
| `published` | Есть `index.html`, в Guides (`guides: true` / без `false`) |
| `draft`     | Есть outline/HTML, скрыто из Guides (`guides: false`)      |
| `planned`   | Темы ещё нет в `docs/presentations/<slug>/`                |

---

## Есть

| Тема     | Slug       | Статус    | Кратко                                                                           |
|----------|------------|-----------|----------------------------------------------------------------------------------|
| ArchUnit | `archunit` | published | Исполняемая архитектура: границы модулей, слои core, Generator ↔ Benchmark       |
| Gradle   | `gradle`   | published | Сборка Ru BizGen: lifecycle, кэши, composite `build-logic`, conventions, catalog |

Пути:

- `docs/presentations/archunit/index.html`
- `docs/presentations/gradle/index.html` (+ `outline.md`)

### Дополнения к `archunit` (при ревизии / следующем проходе)

Уже в теме: границы модулей, слои core, Generator ↔ Benchmark.

Не забыть связать явно (если ещё слабо на слайдах):

- **Пирамида качества** — ArchUnit как нижний «структурный» слой рядом с unit/JMH/UI (мост к `generators-core`, `jmh`, `intellij-platform-plugin`, без их пересказа).
- **`BenchmarkNamingArchTest`** — не только naming, а **полнота** 1:1 с `GeneratorInfoProvider` (мост к `jmh`).
- **`PluginBoundaryArchTest` внутри plugin** — почему arch-модуль не тянет IntelliJ Platform.

### Дополнения к `gradle` (при ревизии / следующем проходе)

Уже в теме: lifecycle, кэши, `build-logic`, conventions, catalog.

Не забыть вшить в дугу (CI — про гонку сборки, не отдельная преза):

- **CI/CD GitHub Actions** — `ci-main` / `ci-dev` / `ci-all` (`-PrunDistanceFinderTests`) / `ci-integration` / `docs.yml`; Dependabot (`minor-and-major`); `auto-label`.
- **Convention-хвосты** — testing-convention (тег `distanceFinderTests`), kover-convention, dokka-convention → как Gradle-задачи кормят качество и Docs (детали Kover-в-footer — в `generators-core`).
- **Гейты пропсами** — `-PrunDistanceFinderTests`, `-PrunIntegrationTests` / `-DrunIntegrationTests` (куда какой workflow).

---

## Подготовить

Приоритет сверху вниз — ориентир, не жёсткий backlog.

| Тема                     | Предлагаемый slug          | Зачем                                                            | Заметки                                                                                          |
|--------------------------|----------------------------|------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| IntelliJ Platform Plugin | `intellij-platform-plugin` | Сборка плагина + runtime в IDE + UI-тесты + настройки/publish    | Дуга ниже: Platform → DI/actions → settings → notifications → Starter/Driver → signing/changelog |
| Ядро генераторов         | `generators-core`          | Контракт генераторов + корректность + уникальность + покрытие    | Дуга ниже: API → checksums → dual channel → distance → Kover/Dokka                               |
| JMH / perf               | `jmh`                      | Зачем микробенчмарки + настройки + внедрение + отчёты            | Цель продукта: генераторы практически мгновенны (~µs)                                            |
| ИИ вокруг Ru BizGen      | `ai-mcp`                   | ИИ в разработке → что есть для ИИ → plugin → MCP (+ Docker)      | Бывший `mcp-server`; техника MCP — финал, не отдельная тема «про Ktor»                           |

### Набросок дуги `intellij-platform-plugin`

1. **Сборка и совместимость** — IntelliJ Platform Gradle Plugin, `buildPlugin`, `sinceBuild`/`untilBuild`, версия плагина ↔ IDEA build (`1.12.261` → build `261` → `2026.1`).
2. **Changelog Plugin** — релизная заметка как часть контура плагина (JetBrains changelog).
3. **Свой DI поверх Platform** — маркер `BizGenService` + `getBizGenService()` → `ApplicationManager.service`; сервисы в `plugin.xml` без Spring/Kodein в runtime (Kodein — только integration).
4. **Action-пайплайн** — `BizGenMainAction` (popup) → `BaseGeneratorAction`: generate → `WriteCommandAction` в редактор → clipboard → notification. Мост к dual channel результата (детали `GeneratorResult` — в `generators-core`).
5. **Настройки и soft migration** — `PersistentStateComponent` / `bizgen_plugin_settings.xml`; `BizGenAppSettingsSoftUpdater` (удалить устаревшие, обновить имена, добавить новые, пересчитать позиции); custom names на actions.
6. **Уведомления и смежные настройки** — strategy BELL / HINT / DISABLE в `NotificationService`; clipboard / escape-char settings как часть UX после генерации.
7. **UI integration: IDE Starter + Driver SDK** — source set `integrationTest`, `@EnabledIfSystemProperty(runIntegrationTests)`, Kodein для Starter, skill `driver-ui-tests`; ночной `ci-integration` (детали CI-матрицы — в `gradle`).
8. **Подпись и публикация** — env `CERTIFICATE_CHAIN` / `PRIVATE_KEY` / `PRIVATE_KEY_PASSWORD`, `PUBLISH_TOKEN`, plugin verifier (RELEASE/RC/PATCH).

Границы: алгоритмы/реестр генераторов — `generators-core`; MCP/Docker — `ai-mcp`; Gradle lifecycle/кэши/CI overview — `gradle`.

### Набросок дуги `generators-core`

1. **Контракт** — `Generator` / `GeneratorStr`, `GeneratorInfo` / `GeneratorStrInfo`, категории, единый реестр `GeneratorInfoProvider` (source of truth для plugin и MCP).
2. **Алгоритмическая корректность** — контрольные суммы, которые реально используются: ИНН (P10/P11/P12), IBAN mod-97, СНИЛС, карты/счета — Луна (`LuhnAlgorithm`). Зачем: проходят валидацию во внешних системах.
3. **Два канала результата** — `GeneratorResult`: `toClipboard` (сырое) vs `toEditor` (`withEscape` / кавычки). Зачем плагину два представления одного значения.
4. **Уникальность** — `uniqueDistance`, тесты `find_distance/` / тег `distanceFinderTests`, `-PrunDistanceFinderTests`; философия «не коллизия в разумном окне», не только «как включить тег».
5. **Пирамида unit-слоя** — JUnit Jupiter 6 + Kotest assertions + MockK; `GeneratorBaseTest` / `testsOnDistanceToClipboard|Editor`; где заканчивается unit и начинается integration/JMH/ArchUnit (мосты, без пересказа).
6. **Kover + Dokka** — покрытие HTML/XML, исключение `ru.eda.plgn.plugin.bizgen.ui`, интеграция отчёта в Dokka footer; как это стыкуется с kover/dokka conventions (сборка — намёк на `gradle`).

Границы: Action/DI/settings UI — `intellij-platform-plugin`; µs и JMH — `jmh`; MCP tools — `ai-mcp`.

### Набросок дуги `jmh`

1. **Зачем JMH** — микробенчмарки на JVM: почему «на глаз» / `System.nanoTime` в тесте врут (warmup, JIT, GC, dead-code elimination). Цель для BizGen: **удостовериться, что генераторы работают практически мгновенно** (UX плагина / MCP batch).
2. **История и модель** — откуда JMH (OpenJDK / Oracle), режимы (AverageTime, Throughput, …), цикл: forks → warmup → measurement → profilers.
3. **Настройки, которые важны** — units (µs), warmup/measurement, forks, JVM args (`-Xms/-Xmx`, G1), profilers (`gc`, `stack`). Что зафиксировано в `BaseGeneratorBenchmark`.
4. **Внедрение в проект** — `ru-bizgen-perf`: `BaseGeneratorBenchmark` / `StrGeneratorBenchmark`, 1 бенчмарк на генератор; мост к ArchUnit (`BenchmarkNamingArchTest` — полнота 1:1).
5. **Доп. контур отчётов** — `JmhMarkdownReport` (JSON → markdown), порядок величин в README (~0.06–4.0 µs/оп).

Границы: структура модулей / naming rules — `archunit`; сами генераторы — `generators-core`.

### Набросок дуги `ai-mcp`

1. **ИИ помогает разрабатывать** — `AGENTS.md`, skills (в т.ч. `driver-ui-tests`), агенты Cursor; **шаблон презентаций / Dokka Guides** (outline → HTML → `syncDocsToDokka` → Pages) как пример ИИ-контура в репо.
2. **Что уже есть для ИИ** — локальные генераторы без внешних API; тот же `GeneratorInfoProvider`; стабильные type-key; категорийные tools + annotations; `/health`. «Плагин для человека в IDE» vs «tools для агента».
3. **Из плагина — отдельный MCP** — ядро без IntelliJ → `ru-bizgen-mcp`: Streamable HTTP, 5 category tools (не 31 flat), Ktor/Netty, batch + dedup + retry, `CancellationException` re-throw.
4. **Упаковка для агентов** — Docker multi-stage (`eclipse-temurin` 21 JDK → JRE, `installDist`), порт, version via `-Dmcp.version`; зачем образ рядом с plugin-дистрибутивом.

Границы: IntelliJ/signing/UI-тесты — `intellij-platform-plugin`; checksums/реестр API — `generators-core`; Gradle/CI docs job — `gradle`.

---

## Как обновлять этот файл

1. Новая идея → сразу в **набросок** подходящей темы из таблицы (Есть / Подготовить); отдельный slug — только если не влезает в границы.
2. Начали работу → каталог `docs/presentations/<slug>/`, статус `draft`.
3. Готово к Guides → `guides: true`, статус `published`, перенос строки в **Есть**.
4. Не дублировать полный список тем в `.config/dokka/Module.md` — там только отсылка к меню **Guides**.
