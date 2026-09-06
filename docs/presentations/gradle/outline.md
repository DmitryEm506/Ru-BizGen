---
title: Gradle в Ru BizGen
subtitle: Как устроена сборка от lifecycle до convention plugins
author: Dmitry Emelyanenko
date: 2026-08-08
language: ru
audience: Kotlin/Java инженеры, onboarding и tech-talk
guides: true
---

<!--
Объём полей и лимиты по типам слайдов:
docs/presentation-template/OUTLINE_CRITERIA.md

ПРАВИЛО ПЛОТНОСТИ (обязательно для HTML):
- Слайд должен визуально заполнять карточку (~80–100% полезной площади),
  а не оставлять пустоту «ради воздуха».
- Использовать верхнюю границу бюджетов OUTLINE_CRITERIA, не нижнюю.
- Если одного layout мало — комбинировать: lead + основной блок + callout /
  второй смысловой пояс (список фактов под диаграммой, checklist под comparison).
- Hero — исключение (титул).
- Если интересного контента не хватает на плотный слайд — ОБЪЕДИНЯТЬ слайды,
  а не оставлять полупустой.
-->

# Общий замысел

Презентация — смесь общего Gradle и конкретной сборки Ru BizGen. После просмотра читатель понимает lifecycle и кэши Gradle, отличие от
Maven, и как в этом репозитории связаны multi-project, composite `build-logic`, convention plugins и version catalog. Сборка IntelliJ
Platform Plugin намеренно не разбирается — для неё отдельная презентация.

# Замечания по плотности (для ревью outline)

Объединено относительно прошлой версии (были полупустыми по отдельности):

| Было                                       | Стало                      | Почему                                      |
|--------------------------------------------|----------------------------|---------------------------------------------|
| Wrapper + Daemon                           | «Запуск: Wrapper и Daemon» | по 3–4 коротких пункта — мало на слайд      |
| Lifecycle + граф задач                     | «Lifecycle и граф задач»   | pipeline из 4 слов + тонкая схема           |
| Configuration Cache + Build Cache/parallel | «Кэши и скорость»          | rule и 4 facts дублировали тему             |
| Commands + CI + Правила                    | «Команды, CI и правила»    | два чеклиста → две плотные колонки/карточки |
| Takeaways + Дальше                         | «Итог и дальше»            | два коротких финала                         |

Под особым контролем при генерации HTML (добить визуальную плотность):

- Gradle vs Maven — comparison + блок «когда выбирать» снизу (раньше ~30% площади);
- Популярность — facts + QR + список выводов, как у ArchUnit history;
- Карта сборки / conventions / code — `deck--fill`, легенда, пояснения на максимуме бюджета.

## Слайд 1 — Gradle в Ru BizGen

Тип: hero

Главный тезис: Как устроена сборка: от lifecycle Gradle до convention plugins в репозитории.

Содержание:

- eyebrow: Ru BizGen Guides · август 2026 · Dmitry Emelyanenko;
- brand: Gradle в Ru BizGen;
- lead: Сначала экосистема и модель Gradle, затем карта модулей, composite `build-logic` и version catalog на коде этого репозитория.

## Слайд 2 — Популярность Gradle

Тип: facts

Главный тезис: Gradle — №2 в Java и де-факто стандарт в Android и Kotlin-проектах.

Содержание:

- Java (JetBrains 2024): ~42% vs Maven ~58% — среди Java-разработчиков;
- Android: ~95% проектов — Gradle как дефолт Android SDK / Android Studio;
- Новые Kotlin-проекты: ~80%+ — оценка по GitHub (Gradle Forums);
- Загрузки: 20M+/мес — оценка Gradle Inc.;
- Jobs/search (BPF 2024): Maven лидирует ~2–3:1 — «дефолт вакансий» всё ещё Maven.

Дополнительный блок (под/рядом с facts, обязательно для плотности):

- Maven шире в классическом enterprise Java и job ads;
- Gradle доминирует там, где Android, Kotlin и сложный multi-module;
- Выбор инструмента = экосистема команды, а не только «кто популярнее»;
- Официальный сайт и docs — точка входа в актуальную документацию.

Визуал: fact-grid (5 фактов) + qr-card (`gradle-official-qr.png` → https://gradle.org) + короткий список выводов; layout как ArchUnit
history (контент + QR), `deck--fill`.

Интерактивность: QR и текстовая ссылка на https://gradle.org.

Обязательные факты:

- Maven всё ещё шире в классическом Java;
- в Android/Kotlin Gradle доминирует;
- QR и URL: https://gradle.org.

## Слайд 3 — Gradle vs Maven

Тип: comparison

Главный тезис: Одна экосистема артефактов — разная модель сборки; плюсы и цена выбора должны быть на одном экране.

Левая колонка (Gradle) — ровно 5 пунктов, развёрнуто:

- Модель: Kotlin DSL, DAG задач, инкрементальность по входам/выходам;
- Скорость: Configuration Cache, Build Cache, parallel из коробки;
- Гибкость: Task API и convention plugins без тяжёлого ритуала Maven plugin;
- Масштаб: multi-project, `includeBuild`, version catalog для крупных репо;
- Цена: круче онбординг, чаще churn major, сложнее отлаживать configuration.

Правая колонка (Maven) — ровно 5 пунктов, развёрнуто:

- Модель: декларативный `pom.xml` и фиксированный lifecycle фаз;
- Предсказуемость: один «стандарт индустрии», меньше сюрпризов между командами;
- Онбординг: проще найти людей и готовые корпоративные шаблоны;
- Экосистема: parent POM, profiles, огромный плагин-каталог;
- Цена: многословный XML и тяжелее выразить нестандартный граф задач.

Нижний блок «Когда выбирать» (обязателен для заполнения слайда, не отдельный слайд):

- Бери Gradle: Android/Kotlin, кастомная сборка, multi-module + caches важны (кейс Ru BizGen);
- Бери Maven: классический Java-сервис, максимум предсказуемости и совпадения с вакансиями;
- Общее: Maven Central и GAV-координаты артефактов работают в обоих мирах;
- Не аргумент «только хайп»: смотри на модель сборки и стоимость поддержки скриптов.

Визуал: `deck--fill` + `.duo-grid` на всю ширину + callout/checklist «Когда выбирать» под колонками. Не оставлять нижнюю половину пустой.

Обязательные факты:

- общий Maven Central / те же координаты артефактов;
- Gradle сильнее в скорости и гибкости multi-module;
- Maven сильнее в дефолтности и стабильности ожиданий.

## Слайд 4 — Модель Gradle и запуск

Тип: cards-2x2

Главный тезис: Gradle — движок графа задач; Wrapper и Daemon делают запуск повторяемым и быстрым.

Содержание (в каждой карточке: абзац + `<ul>`; Модель / GAV / Daemon — плотнее):

1. Kicker: Модель; Title: DAG, не «фаза Maven»;
   Text: DAG (Directed Acyclic Graph) — ориентированный ациклический граф задач: узлы = tasks, рёбра = «сначала A, потом B». Циклов быть не может.
   Буллеты:
   - Execute обходит только нужный подграф от запрошенной задачи (`check`, `buildPlugin`…);
   - рёбра — явный `dependsOn` и неявные от плагинов (compile → test → check);
   - входы/выходы задачи решают up-to-date: неизменные узлы пропускаются;
   - нет жёсткого набора фаз как у Maven — граф собирается под ваш запрос;
   - `--dry-run` печатает план без выполнения — удобно «увидеть» DAG.

2. Kicker: Артефакты; Title: Тот же Maven Central;
   Text: Меняется движок сборки, не формат репозитория. GAV — координаты артефакта в Maven-мире.
   Буллеты:
   - **G**roup — организация/namespace (`org.jetbrains.kotlin`);
   - **A**rtifact — имя библиотеки (`kotlin-stdlib`);
   - **V**ersion — версия (`2.4.0`); вместе: `group:name:version`;
   - в Gradle то же: `implementation("…")` / catalog `libs.*` ходят в Maven Central;
   - миграция Maven→Gradle не требует «другого Central».

3. Kicker: Wrapper; Title: Версия в репозитории;
   Text: `gradlew` + `gradle/wrapper/` фиксируют дистрибутив — CI и ноутбуки совпадают.
   Буллеты: всегда `./gradlew`, не системный `gradle`; обновление Wrapper — осознанный diff; без локальной установки Gradle.

4. Kicker: Daemon; Title: Тёплая JVM;
   Text: Gradle Daemon — долгоживущий фоновый процесс: между запусками не поднимает JVM с нуля.
   Буллеты:
   - переиспользует JIT, загруженные классы плагинов и кэш метаданных;
   - первая сборка после старта дороже (прогрев), следующие — заметно быстрее;
   - ускоряет локальные итерации «поправил → `./gradlew check`»;
   - при странных сбоях/зависших воркерах: `./gradlew --stop` (убить daemon и поднять заново);
   - в CI полезен внутри одного job с несколькими вызовами; между чистыми агентами не шарится;
   - отключить можно (`--no-daemon`), но для повседневной работы обычно оставляют включённым.

Callout: В Ru BizGen один Wrapper обслуживает core, plugin, mcp, perf и archunit — единый контракт команд.

Визуал: `deck--fill` + method-grid 2×2; карточки Модель/GAV/Daemon с 4–6 буллетами.

## Слайд 5 — Lifecycle и граф задач

Тип: diagram

Главный тезис: Init → Configure → Execute строит DAG; up-to-date и кэш решают, что реально бежать.

Визуал: сверху pipeline из 4 шагов с подписями (не одно слово):

1. Init — settings, included builds, Wrapper;
2. Configure — оценка проектов и задач (дорого на multi-project);
3. Execute — выполнение устаревших узлов графа;
4. Result — артефакты, отчёты, код выхода.

Схема графа (ИСПРАВИТЬ layout — прошлый dashed «parallel · jar» уходил в пустоту):

- Ряд 1 (горизонталь, solid): `settings/configure` → `compileKotlin` → `test` → `check`
- Ряд 2 (под compileKotlin, solid вниз): `compileKotlin` → `jar`
- Легенда: solid = dependsOn / implicit; отдельная подпись «ветка jar после compileKotlin» (НЕ рисовать стрелку в пустое место)
- Узлы компактные; длинные подписи в две строки (`settings`/`configure`, `compile`/`Kotlin`), чтобы текст не вылезал;
- Координаты рёбер на границах узлов; короткие стрелки между узлами; viewBox с запасом

Легенда / обязательные факты (блок под схемой):

- рёбра — зависимости задач (`dependsOn` и неявные от плагинов);
- up-to-date пропускает задачу с неизменными входами и выходами;
- `--dry-run` показывает план без выполнения — удобно учить граф;
- `check` — агрегатор проверок, не «магическая фаза» как у Maven;
- Configuration Cache может пропустить повторный Configure, если входы стабильны.

Визуал: `deck--fill` — pipeline + исправленный SVG `.graph` + список легенды.

## Слайд 6 — Кэши и скорость

Тип: facts

Главный тезис: В Ru BizGen скорость сборки завязана на три флага и дисциплину configuration time.

Содержание (каждая плашка: label + value + развёрнутый note, не одна короткая фраза):

- Configuration Cache: true —
  сериализует результат Configure на диск; при неизменных входах следующий запуск чаще сразу в Execute.
  На multi-project это главный выигрыш: не пересчитывать все `build.gradle.kts` снова.
  Ключ в `gradle.properties`: `org.gradle.configuration-cache=true`.

- Build Cache: true —
  кэширует **выходы задач** (классы, отчёты) по ключу входов; hit → задача не пересчитывается.
  Работает локально и может шариться между CI-агентами при remote cache.
  Ключ: `org.gradle.caching=true`. Не путать с Configuration Cache.

- Parallel: true —
  независимые проекты/задачи без рёбер между собой бегут параллельно на нескольких воркерах.
  Выигрыш растёт с числом модулей (у нас core/plugin/mcp/perf/archunit).
  Ключ: `org.gradle.parallel=true`.

- JVM: -Xmx4g —
  запас heap/metaspace под IntelliJ Platform, Dokka, тесты и daemon.
  Без запаса CI/локаль ловят OOM на тяжёлых задачах документации и plugin verify.
  Задано в `org.gradle.jvmargs` в `gradle.properties`.

- Уровни: CC ≠ BC —
  Configuration Cache = фаза Configure (структура задач);
  Build Cache = артефакты Execute (outputs).
  Можно включить оба — они дополняют, а не заменяют друг друга.

Дополнительный блок «Правило» (обязателен):

- Имя: Configuration Cache first;
- Because: на multi-project Configure часто дороже полезной работы Execute;
- Human: не читайте окружение «как попало» на configuration time без declaration;
- Flow: допустимо — сериализуемая конфигурация; запрещено — скрытые side effects при configure;
- Пример из репо (`gradle.properties`):

```properties
org.gradle.configuration-cache=true
org.gradle.caching=true
org.gradle.parallel=true
```

Визуал: `deck--fill` — fact-grid с длинными `fact__note` (2 предложения на плашку) + `.rule` + callout.

Обязательные факты:

- все три флага уже включены в Ru BizGen;
- Build Cache и Configuration Cache — разные механизмы.

## Слайд 7 — Карта сборки Ru BizGen

Тип: diagram

Главный тезис: Root включает пять модулей и composite `build-logic` с convention plugins.

Визуал схемы (ПОЧИНИТЬ — уменьшить квадраты, чтобы стрелки читались):

- Узлы компактнее: модули ~88×36, root/build-logic ~120×36; font чуть меньше, но читаемый;
- Ряд 1: `ru-bizgen` по центру сверху;
- Ряд 2: пять модулей в одну линию с равными зазорами: `core` `plugin` `mcp` `perf` `archunit`;
- Ряд 3: `build-logic` по центру снизу;
- Solid стрелки: root → каждый модуль (конец стрелки на верхнюю грань узла, не в пустоту);
- Dashed стрелки: `build-logic` → каждый модуль (к нижней грани);
- viewBox с padding; marker `refX` согласован с длиной path;
- Не пересекать подписи узлов линиями.

Легенда / факты под схемой:

- `settings.gradle.kts`: `includeBuild("build-logic")` + пять `include(...)`;
- `build-logic` — composite build, не обычный subproject в `include`;
- `core` без IntelliJ/Ktor; `plugin` и `mcp` переиспользуют generators;
- `perf` — JMH; `archunit` — только архитектурные тесты в `check`;
- смена convention в `build-logic` влияет на всех потребителей сразу.

Визуал: `deck--fill` — исправленная компактная SVG + legend list на 5 пунктов.

## Слайд 8 — Composite и conventions

Тип: cards-2x2

Главный тезис: Общая логика сборки живёт в `build-logic` и подключается как `id("ru-bizgen.*-convention")`.

Содержание (текст + `<ul>` в каждой карточке; testing/docs/composite — плотнее):

1. Kicker: Composite; Title: `includeBuild`;
   Text: `settings.gradle.kts` подключает `includeBuild("build-logic")` — отдельный Gradle-билд с convention plugins, видимыми как обычные `id(...)`.
   Буллеты:
   - явный аналог buildSrc без «магии» classpath корня;
   - модули пишут `id("ru-bizgen.kotlin-convention")` и т.п., без копипасты блоков;
   - review «как собираем» = diff в `build-logic/`, а не в пяти `build.gradle.kts`;
   - изменение convention сразу у всех потребителей; проще держать toolchain/тесты одинаковыми.

2. Kicker: kotlin; Title: `kotlin-convention`;
   Text: Kotlin JVM + `jvmToolchain` 21 из version catalog — единый JDK для модулей.
   Буллеты: `id("ru-bizgen.kotlin-convention")`; без локального `jvmToolchain(21)`; смена major JDK через `libs.versions.toml`.

3. Kicker: testing; Title: `testing-convention`;
   Text: Иногда нужны долгие тесты «на критерии» (у нас — уникальность генераторов). Их нельзя гонять на каждый `check`/PR.
   Буллеты:
   - JUnit Platform + теги: дорогие тесты помечают `@Tag("distanceFinderTests")`;
   - по умолчанию `excludeTags("distanceFinderTests")` — быстрый локальный/CI `check`;
   - включение: `-PrunDistanceFinderTests` или `-DrunDistanceFinderTests=true` → `includeTags`;
   - полный прогон — `ci-all`; на push/PR остаётся короткий feedback;
   - единый `testLogging` (FAILED) для всех модулей с convention.

4. Kicker: docs/cov; Title: dokka + kover (+ root);
   Text: Модули отдают Dokka/Kover; root convention собирает презентации в GitHub Pages через Dokka.
   Буллеты:
   - `dokka-convention` — HTML модуля, source links, шаблоны `.config/dokka`;
   - `kover-convention` — XML/HTML coverage onCheck;
   - `copyPresentationsToDokka` — `docs/presentations/*/index.html` (+ медиа) → `dokka/html/images/presentations/`;
   - `generatePresentationsGuides` — `guides.json` для вкладки Guides (без правок `header.ftl`);
   - `guides: false` в outline скрывает черновик из Guides; `syncDocsToDokka` после `dokkaGenerateHtml`.

Callout: Меняйте «как мы собираем» в одном месте — review `build-logic`, а не пять почти одинаковых `build.gradle.kts`.

Визуал: `deck--fill` + плотные 2×2 карточки (абзац + список 3–5 пунктов) + callout.

## Слайд 9 — Пример kotlin-convention

Тип: code

Главный тезис: Convention подключает Kotlin JVM и читает JDK из catalog; рядом — остальные conventions репозитория.

```kotlin
plugins {
  id("org.jetbrains.kotlin.jvm")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
  jvmToolchain(libs.findVersion("java").get().requiredVersion.toInt())
}
```

Пояснения к примеру:

- модулям не нужно писать `jvmToolchain(21)` локально;
- `java = "21"` в `libs.versions.toml` — рычаг major JDK для kotlin-convention;
- подключение: `id("ru-bizgen.kotlin-convention")`.

Блок «Ещё conventions в проекте» (обязателен, отдельный список или mini-cards под кодом):

- `ru-bizgen.testing-convention` — JUnit Platform; режет `distanceFinderTests` из быстрого `check`;
- `ru-bizgen.dokka-convention` — HTML Dokka модуля, source links на GitHub, шаблоны `.config/dokka`;
- `ru-bizgen.kover-convention` — отчёты покрытия XML/HTML при `check`;
- `ru-bizgen.dokka-root-convention` — только root: агрегация Dokka, Guides/`guides.json`, копирование презентаций и Kover в Pages.

Визуал: код (6–10 строк) + пояснения к примеру + плотный блок «ещё conventions»; без пустого низа.

## Слайд 10 — Version Catalog

Тип: code

Главный тезис: `libs.versions.toml` — единый источник версий библиотек, плагинов и bundles.

```toml
[versions]
kotlin = "2.4.0"
java = "21"
dokka = "2.2.0"

[plugins]
kotlin = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
dokka = { id = "org.jetbrains.dokka", version.ref = "dokka" }
```

Пояснения (нагнать объём — список 6–7 пунктов + callout):

- в скриптах: `alias(libs.plugins.kotlin)`, `libs.junit.jupiter.api`, `libs.bundles.tests.unit`;
- `[versions]` — одна цифра на экосистему (Kotlin, JDK, Dokka, Ktor, ArchUnit…);
- `[libraries]` — координаты артефактов с `version.ref`, без «магических» строк по модулям;
- `[bundles]` — готовые наборы (`tests-unit`, `tests-integration`) для одинакового тест-стека;
- `[plugins]` — id плагинов с теми же version.ref, что и у библиотек;
- build-logic тянет marker artifacts плагинов из catalog — conventions не хардкодят версии;
- Dependabot/ручной bump = один файл и один понятный diff на review;
- в этой презентации только Kotlin DSL-доступ к catalog (`libs.*`).

Callout: Смена версии в catalog + зелёный CI важнее локального «у меня уже обновил в одном модуле».

Визуал: код + длинный список пояснений + callout; `deck--fill`.

## Слайд 11 — Роли модулей

Тип: facts

Главный тезис: Пять модулей — разные форм-факторы одного ядра генераторов.

Содержание:

- core: 31 генератор — чистая JVM-логика без IntelliJ и Ktor;
- plugin: IDE actions/settings — IntelliJ Platform; детали сборки плагина — отдельная презентация;
- mcp: Streamable HTTP MCP — Ktor/Netty + SDK, тот же `GeneratorInfoProvider`;
- perf: JMH — бенчмарки 1:1 с генераторами + markdown-отчёты;
- archunit: границы модулей/слоёв — исполняемые правила в `check`.

Дополнительный блок:

- зависимость «вниз к core», а не наоборот;
- Gradle склеивает разнородные артефакты одним Wrapper и общими conventions;
- onboarding: сначала карта модулей, потом `build-logic`, потом флаги CI.

Визуал: `deck--fill` — fact-grid 5 + короткий content/callout блок.

## Слайд 12 — Команды, CI и правила

Тип: comparison

Главный тезис: Один контракт `./gradlew` для локали и CI плюс правила, чтобы сборка оставалась API проекта.

Левая колонка (карточка «Команды и CI») — плотный список:

- Быстрый `./gradlew check` — без distance/integration по умолчанию;
- `./gradlew buildPlugin` — артефакт IDE-плагина;
- `./gradlew dokkaGenerateHtml` — Dokka + презентации в Pages;
- `-PrunDistanceFinderTests` / `ci-all` — полный прогон уникальности;
- `-PrunIntegrationTests` / ночной `ci-integration` — UI Starter + Driver;
- Push/PR `ci-main`/`ci-dev` — `check` + `buildPlugin` для быстрого feedback;
- `docs.yml` — публикация Dokka HTML и Guides;
- тег `distanceFinderTests` режется в `testing-convention`, пока флаг не включён.

Правая колонка (карточка «Правила сборки») — плотный список:

- версии только в `libs.versions.toml`, не хардкод по модулям;
- общая логика в `build-logic` conventions, не copy-paste;
- локально и в CI — только `./gradlew` (Wrapper = версия);
- не отключать configuration/build cache без замера;
- тяжёлые тест-теги — явными флагами, не всегда в `check`;
- configuration time чистый — иначе Configuration Cache мешает;
- новая «магия сборки» сначала в convention, потом в модуле;
- одно место правды дешевле, чем «у меня локально собирается».

Callout: Тяжёлые теги включайте явно — быстрый PR-check важнее полного прогона на каждый коммит.

Визуал: `deck--fill` + `.duo-grid` из двух равноплотных карточек (команды | правила) + callout снизу. Не checklist на весь экран и не два
отдельных слайда.

## Слайд 13 — Итог и дальше

Тип: cards-2x2

Главный тезис: Четыре опоры сборки Ru BizGen и куда идти после этого Guides.

Содержание (абзац + 2–3 буллета в каждой карточке):

1. Kicker: Модель; Title: Lifecycle + DAG; Text: Configure отделён от Execute — сначала поймите граф, потом оптимизируйте. Буллеты:
   up-to-date и `--dry-run`; не мыслите только «фазами Maven»; Wrapper = одна версия у всех.
2. Kicker: Скорость; Title: Cache + parallel; Text: Три флага в `gradle.properties` уже включены — берегите configuration time. Буллеты:
   Configuration Cache ≠ Build Cache; не читайте env «втихаря» при configure; parallel для независимых модулей.
3. Kicker: Структура; Title: Modules + composite; Text: `core` переиспользуется plugin/mcp; логика сборки вынесена в `build-logic`. Буллеты:
   пять модулей — разные форм-факторы; `includeBuild` вместо копипасты; ArchUnit стережёт границы.
4. Kicker: Контракт; Title: Catalog + conventions; Text: Версии и правила в одном контуре; модули пишут `id("ru-bizgen.*-convention")`.
   Буллеты: bump только в `libs.versions.toml`; тяжёлые теги — явными флагами; сборка = API проекта.

Нижний блок «Дальше» (развёрнутый callout/список):

- docs: https://gradle.org — Configuration Cache, Build Cache, Composite Builds;
- User Guide стоит читать точечно под ваши боли (кэш, included builds), не «всё подряд»;
- в репозитории: ArchUnit Guides уже в Dokka; эта презентация тоже в Guides;
- следующая отдельная презентация: IntelliJ Platform Plugin build (signing/publish вне скоупа здесь).

Визуал: `deck--fill` — плотные 2×2 + блок next steps без пустоты.

# Дополнительные материалы

## QR

- Файл: `docs/presentations/gradle/gradle-official-qr.png`
- URL: https://gradle.org
- Генератор: `docs/presentation-template/utils/qr/generate_qr.py`

```bash
pip install -r docs/presentation-template/utils/qr/requirements-qr.txt
python docs/presentation-template/utils/qr/generate_qr.py https://gradle.org \
  -o docs/presentations/gradle/gradle-official-qr.png
```

## Источники популярности (для подписей на слайде 2)

- JetBrains Developer Ecosystem 2024: Java — Maven ~58%, Gradle ~42%
- Android research (open-source sample): Gradle ~95% проектов
- Gradle Forums: ~20M+ downloads/month; новые Kotlin-проекты ~80%+ (оценка)
- Better Projects Faster 2024 Q1: Maven лидирует в jobs/searches примерно 2–3:1

## Код репозитория

- `settings.gradle.kts` — modules + `includeBuild("build-logic")`
- `gradle.properties` — configuration-cache, caching, parallel
- `gradle/libs.versions.toml` — versions / libraries / plugins / bundles
- `build-logic/src/main/kotlin/ru-bizgen.*-convention.gradle.kts`
- `build-logic/.../ru-bizgen.testing-convention.gradle.kts` — тег `distanceFinderTests`
- `build.gradle.kts` (root) — Dokka/Kover wiring

## Вне скоупа этой презентации

- IntelliJ Platform Plugin: signing, publishing, sinceBuild / untilBuild — отдельная тема
- Dockerfile MCP и runtime Ktor — не Gradle-модель
- Groovy DSL — не показываем; только Kotlin DSL
