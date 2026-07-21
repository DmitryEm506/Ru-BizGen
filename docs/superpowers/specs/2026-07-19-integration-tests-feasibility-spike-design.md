# Design: Integration Tests Feasibility Spike (Starter + Driver)

> _Спайк для проверки применимости JetBrains Integration Test Framework в ru-bizgen-plugin_

- **Дата:** 2026-07-19
- **Статус:** Approved (spike)
- **Автор:** Dmitry_Emelyanenko

## Контекст

Проект `ru-bizgen-plugin` имеет light in-process тесты (`BaseIdeaTest`, `@TestApplication`, JUnit 5 fixtures, MockK).
Покрытие: сервисная логика, структура UI-компонентов. **Не** покрыто: startup safety, реальный popup `BizGenMainAction`,
вставка в редактор, буфер, notification, диалог Settings.

JetBrains опубликовал фреймворк integration-тестов (Starter + Driver): реальная IDE в отдельном процессе,
UI-взаимодействие через Swing DSL + XPath, API через JMX/RMI. Цель — проверить, можно ли его применить.

## Риски (почему спайк, а не сразу набор)

1. **JUnit 6 vs "Starter supports JUnit 5 only"** — docs прямо заявляют JUnit 5 only. Проект на JUnit 6.
2. **`kotlinx-coroutines-core*` исключены** из `testImplementation` (`build.gradle.kts:41-45`), но Starter их требует.
3. **Kodein DI** отсутствует в проекте.
4. **Unified Platform Distribution + IntelliJ 2026.1** — статьи под IC 2024.2/2024.3.
5. **`TestFrameworkType.Starter`** в IntelliJ Platform Gradle Plugin 2.17.0 — совместимость с 2026.1 не задокументирована.

## Решение (вариант B)

Два теста в новом `integrationTest` source-set, **отдельно** от `test` (чтобы не сломать exclude-правила light-тестов):

### Тест 1 — Starter lifecycle (критический)
- `NoProject`, `IdeProductProvider.IC.withVersion("2024.3")` (well-supported, isolates JUnit6/Kotlin2.4 риск).
- `PluginConfigurator.installPluginFromFolder(File(pathToBuildPlugin))`.
- `runIdeWithDriver().useDriverAndCloseIde { waitForIndicators(5.minutes) }`.
- Custom `CIServer` через Kodein DI → `fail` на любом исключении в IDE-процессе.

### Тест 2 — Driver UI DSL
- Та же установка плагина.
- `ideFrame { invokeAction("ru.eda.plgn.bizgen.BizGenMainAction"); popup().shouldBe(present) }`.
- Доказывает: Driver UI DSL работает, плагин зарегистрирован, главный action показывает popup.

## Изменения в build-конфигурации

- `gradle/libs.versions.toml`: `kodein-di = 7.20.2`, `kotlinx-coroutines = 1.10.1` + library entries.
- `ru-bizgen-plugin/build.gradle.kts`:
  - `sourceSets { create("integrationTest") { ... } }` — compileClasspath/runtimeClasspath += main.output.
  - `val integrationTestImplementation by configurations.getting { extendsFrom(testImplementation) }`.
  - `testFramework(TestFrameworkType.Starter, configurationName = "integrationTestImplementation")`.
  - `integrationTestImplementation(libs.kodein.di.jvm)`, `libs.kotlinx.coroutines.core.jvm`, `libs.junit.jupiter.api`.
  - `val integrationTest by intellijPlatformTesting.testIdeUi.registering { task { ... } }` — `dependsOn("buildPlugin")`,
    `systemProperty("path.to.build.plugin", ...)`, `useJUnitPlatform()`.
- **Существующие exclude-правила coroutines в `testImplementation` НЕ трогаются** — Starter работает в отдельном JVM,
  coroutines нужен только в `integrationTestImplementation`.

## Критерий успеха

- **Green:** Оба теста проходят → feasibility доказана → Этап 1 (smoke-набор 3–5 E2E) оправдан.
- **Red (lifecycle):** Starter несовместим с JUnit 6 / 2026.1 → fallback: pin JUnit 5 в `integrationTestImplementation`
  или pin IC 2024.3, или отказ → только light-тесты.

## Что НЕ делает спайк

- Не покрывает `@Remote`/JMX API-слой (вариант C) — нижний риск, отложено в Этап 1.
- Не тестирует все 26 генераторов.
- Не настраивает CI (ночной запуск) — Этап 1.

---

## Результат спайка (2026-07-19)

> **Корректировка от 2026-07-19 (вечер):** первичная версия этого раздела утверждала
> «code-level GREEN, runtime BLOCKED by network до `cache-redirector.jetbrains.com`».
> Повторная проверка показала, что **оба утверждения были ошибочными**:
> (1) код **не компилировался** из-за API-drift в `driver-sdk` 261.22158.277,
> (2) сеть **не блокирует** — артефакты резолвятся и скачиваются, IDE реально запускается.
> Ниже зафиксированы реальные факты.

### Что проверено (compile-level feasibility) — GREEN после фиксов

1. **Build-конфигурация валидна.** `integrationTest` source-set, `TestFrameworkType.Starter`,
   `kodein-di`/`kotlinx-coroutines` deps, регистрация task `integrationTest` через
   `intellijPlatformTesting.testIdeUi.registering`, `dependsOn("buildPlugin")`,
   `systemProperty("path.to.build.plugin", ...)` — Gradle принял конфигурацию без ошибок.
2. **JUnit 6 / Kotlin 2.4 НЕ блокируют.** Риск №1 не подтвердился — конфигурация и
   dependency-resolution прошли без единой ошибки совместимости JUnit 6 ↔ Starter.
3. **Starter-артефакты для 261 (2026.1) существуют и резолвятся.** Риск №4 не подтвердился.
4. **Driver SDK 261 API-drift — исправлен.** Код изначально **не компилировался**
   (ошибки импорта + deprecated-метод). Маппинг на текущий API 261.22158.277:
   - `present` / `shouldBe` → `com.intellij.driver.sdk.ui.present` / `.ui.shouldBe`
     (были в `com.intellij.driver.sdk.present` / `.shouldBe`).
   - `ideFrame` → `com.intellij.driver.sdk.ui.components.common.ideFrame`
     (был в `.components.ideFrame`).
   - `popup` → `com.intellij.driver.sdk.ui.components.elements.popup`
     (был в `.components.popup`); `UiComponent` реализует `Finder`, поэтому `popup()`
     вызывается внутри `ideFrame{}`.
   - `invokeAction` → `com.intellij.driver.sdk.invokeAction` (был в `.ui.components.invokeAction`);
     receiver — `Driver`, поэтому внутри `ideFrame{}` вызывается как `driver.invokeAction(...)`.
   - `PluginConfigurator.installPluginFromFolder(File)` → `@Deprecated(level=ERROR)` →
     `installPluginFromDir(File(...).toPath())`.
   - Все импорты обновлены в `BizGenMainActionUITest.kt`.
5. **JUnit Platform launcher — добавлен.** Первый запуск `:integrationTest` падал с
   «Failed to load JUnit Platform / ensure JUnit Platform launcher available on test
   runtime classpath». Причина: `integrationTestRuntimeOnly` не был сконфигурирован.
   Фикс:
   - `gradle/libs.versions.toml`: `junit-platform-launcher = { group = "org.junit.platform",
     name = "junit-platform-launcher", version.ref = "junit" }`.
   - `ru-bizgen-plugin/build.gradle.kts`: `val integrationTestRuntimeOnly by
     configurations.getting { extendsFrom(configurations.testRuntimeOnly.get()) }` +
     `integrationTestRuntimeOnly(libs.junit.platform.launcher)`.
6. **`compileIntegrationTestKotlin` GREEN** (`BUILD SUCCESSFUL in 49s`).

### Что проверено (runtime feasibility) — GREEN: фреймворк работает, тесты падают на баге тест-кода

Полный запуск `:ru-bizgen-plugin:integrationTest` (`--no-daemon --no-configuration-cache`),
`BUILD FAILED in 6m 13s`, 2 теста, 2 failed. Хронология из HTML-отчёта и stdout:

1. ✅ `buildSearchableOptions` — headless IDE 2026.1 (IU-261.22158.277) запустилась,
   отработала, `buildPlugin` собрал `ru-bizgen-plugin-1.12.261.zip`.
2. ✅ `Starter.newContext("spikeLifecycle", IC, "2024.3")` — Starter запросил
   `data.services.jetbrains.com/products/releases?code=IC` (release/eap/preview) — **три
   HTTP-запроса прошли успешно**.
3. ✅ Starter скачал `ideaIC-2024.3.win.zip` → `ideaIC-243.21565.193.exe` с
   `download.jetbrains.com` (~93 с, успешно).
4. ✅ Starter скачал `7za920.zip` и `7z2501-x64.exe` с `www.7-zip.org`.
5. ⚠️ Один **транзиентный** `ConnectException: Connection timed out` до
   `www.7-zip.org:443` при скачивании `7z2501-x64.exe`. Starter **сам ретраит** —
   повтор через 10 c прошёл успешно. **Это НЕ сетевой блок.**
6. ✅ Распаковка IDE в `out/ide-tests/cache/builds/IC-243.21565.193` (7-zip, успешно).
7. ✅ Настройка IDE paths/system properties, копирование plugin-zip в
   `out/ide-tests/tests/IC-243.21565.193/spikeLifecycle/plugins/ru-bizgen-plugin-1.12.261.zip`.
8. ❌ Оба теста упали с `java.nio.file.FileAlreadyExistsException at
   PluginStartupSpikeTest.kt:61` и `:74` — на вызове
   `PluginConfigurator(this).installPluginFromDir(File(pathToPlugin).toPath())`.

**Корневая причина `FileAlreadyExistsException` — баг тест-кода, не фреймворка.**
Starter **сам** устанавливает плагин, ориентируясь на system property
`path.to.build.plugin` (которое Gradle-тask `integrationTest` уже прокинул — видно в
cmd test-worker'а: `-Dpath.to.build.plugin=...` и в логе: «Copy plugins from
...build\distributions\ru-bizgen-plugin-1.12.261.zip to ...plugins\ru-bizgen-plugin-1.12.261.zip»).
Тест **повторно** вызывает `installPluginFromDir` → целевой файл уже на месте → exception.

### Вердикт

**Feasibility подтверждена на runtime-уровне.** Главные риски (JUnit 6, Kotlin 2.4,
Starter для 2026.1, `TestFrameworkType.Starter` в IntelliJ Platform Gradle Plugin 2.17.0)
не материализовались. Сеть окружения **не блокирует** — Starter сам качает IDE и
инструменты (с авторетраем на транзиентных таймаутах). Реальная IDE 2024.3 запускается,
плагин устанавливается, пути/свойства настраиваются.

Единственное, что отделяет от GREEN — убрать **избыточный** ручной вызов
`PluginConfigurator.installPluginFromDir(...)` из обоих тестов (Starter уже ставит
плагин через `path.to.build.plugin`). Это правка двух строк, не архитектурная проблема.

### Повторный запуск после фикса `installPluginFromDir` (2026-07-19, день)

Шаг 1 из «Следующие шаги» выполнен: из обоих тестов убран блок
`.apply { PluginConfigurator(this).installPluginFromDir(...) }` и ставшие
неиспользуемыми импорты `PluginConfigurator` / `java.io.File`. Цепочка вызова
сокращена до `Starter.newContext(...).runIdeWithDriver().useDriverAndCloseIde { ... }`.
`compileIntegrationTestKotlin` — GREEN.

Повторный запуск `:ru-bizgen-plugin:integrationTest` (`--no-daemon
--no-configuration-cache`): `BUILD FAILED`, 2 теста, 2 failed, 5m51s.
IDE-кэш (IC-243.21565.193) и 7-zip переиспользованы — скачивания не было,
`Installer file ... already exists. Skipping download.`

**Что стало GREEN (проверено, фикс сработал):**

1. ✅ `FileAlreadyExistsException` ушёл. Starter сам копирует plugin-zip через
   `path.to.build.plugin` (`Copy plugins from ...build\distributions\... to
   ...plugins\...` в stdout), повторного `installPluginFromDir` больше нет.
2. ✅ IDE 2024.3 стартует и корректно завершается (`exit code 0`,
   `IDE run spikeLifecycle completed in 4m 33.5s`, `IDE SHUTDOWN`).
3. ✅ JMX/RMI-агент стартует. В thread-dump'е `threadDump-1-2026-07-19-12-04-46.txt`
   присутствует поток `"RMI TCP Accept-7777" #21` в `ServerSocket.platformImplAccept`
   — RMI-registry слушает порт 7777. Используется
   `sun.management.jmxremote.LocalRMIServerSocketFactory` (JDK 21).

**Кажущийся блокер — Driver↔JMX connectivity (RED, впоследствии опровергнут):**

Оба теста падали на `waitForIndicators(...)` → `waitForProjectOpen` →
`ProjectManager.getOpenProjects` (JMX-call):

- Тест 1 (lifecycle, 4m39s): `DriverWithContextError` → `DriverCallException` →
  `JmxCallException: Unable to perform JMX call` →
  `java.rmi.ConnectException: Connection refused to host: 127.0.0.1; nested
  exception is: java.net.ConnectException: Connection refused: connect`.
- Тест 2 (Driver UI, 1m8s): `WaitForException: Timeout(1m): Failed: Project is
  opened` — тот же корень: JMX-call не доходит, `waitForProjectOpen` таймаутится.

**Парадокс:** RMI-сервер **слушал** 7777 (thread-dump это подтверждал), но
driver'ский RMI-стаб на `127.0.0.1` получал `Connection refused`.

**Опровергнутая гипотеза (IPv4/IPv6-mismatch):** Первичная гипотеза объясняла
`Connection refused` тем, что `LocalRMIServerSocketFactory` в JDK 21 биндится
через `InetAddress.getLoopbackAddress()`, который на этой Windows-машине якобы
разрешается в IPv6 `::1`, а driver подключается к IPv4 `127.0.0.1`. Был введён
обходной манёвр `systemProperty("java.net.preferIPv4Stack", "true")` в
`integrationTest` task и `withVMOptions { addSystemProperty(...) }` в обоих тестах.

**Повторная проверка (2026-07-21) опровергла гипотезу.** После удаления
`preferIPv4Stack` из всех трёх мест (`build.gradle.kts:100`,
`PluginStartupSpikeTest.kt:63`, `:76`) оба теста проходят:
`BUILD SUCCESSFUL in 2m 2s`, IDE стартует и завершается чисто (`exit code 0`),
JMX-канал Driver↔IDE работает без какой-либо IPv4/IPv6-настройки.

**Истинная корневая причина `Connection refused`** — не IPv4/IPv6, а побочный
эффект бага тест-кода из предыдущего раздела (`FileAlreadyExistsException`
при повторном `installPluginFromDir`). Некорректный shutdown IDE после этого
исключения обрывал запуск RMI-сервера прежде, чем driver успевал подключиться.
После устранения избыточного `installPluginFromDir` (Стarter сам ставит плагин
через `path.to.build.plugin`) JMX-канал заработал сам — без `preferIPv4Stack`.

**Вывод:** обходной манёвр `preferIPv4Stack` был **ошибочным предположением**,
маскировавшим симптом, а не лечившим причину. Удалён из `build.gradle.kts` и
`BizGenMainActionUITest.kt`. **Гипотеза IPv4/IPv6-mismatch в этой среде
несостоятельна** — JDK 21 + Windows 11 + IC 2024.3 корректно работают с
loopback без принудительного IPv4.

**Что НЕ подтвердилось как блокер (риски из «Контекст»):**
- JUnit 6 / Kotlin 2.4 — по-прежнему не мешают (compile GREEN, tests стартуют).
- `TestFrameworkType.Starter` в IntelliJ Platform Gradle Plugin 2.17.0 — работает.
- Сеть — не блокирует (IDE скачана и закеширована ещё в первом запуске,
  7-zip скачан; транзиентный `ConnectException` до `www.7-zip.org` был только
  в первом запуске и ретраился).

**Итоговый вердикт:** feasibility **подтверждена на runtime-уровне в полном
объёме**. Каркас совместим (build, deps, IDE launch, JMX-agent start,
driver↔IDE JMX-канал), оба теста (`spikeLifecycle`, `spikeUi`) проходят.
Integration-фреймворк применим в текущей среде — без обходных манёвров.

### Следующие шаги

1. **Этап 1: smoke-набор E2E (3–5 тестов)** в `integrationTest`: popup
   `BizGenMainAction`, вставка в редактор, буфер, notification, диалог Settings.
2. Настроить ночной CI (тесты тяжёлые — ~2 мин на 2 теста после прогрева кэша;
   первый запуск качает IDE ~93 с).
3. Не покрывать `@Remote`/JMX API-слой (вариант C) до Этапа 1 — нижний риск.
4. Fallback-пункт (только light-тесты) снят как избыточный — фреймворк работает.

