# Постепенное покрытие UI тестами (Driver/Starter)

> _План постепенного покрытия UI-компонентов плагина integration-тестами на базе JetBrains Starter + Driver SDK_

- **Дата:** 2026-07-22
- **Статус:** In Progress (Этап 1 — Done, Этап 2 — Implementation Complete, Pending CI Verification)
- **Автор:** Dmitry_Emelyanenko
- **Обновлено:** 2026-07-23 — Этап 2: `AppActionsSettingUITest` (6 тестов, `@Remote` для CheckBoxList state, `actionButton` finder, `dialog`/`textField` interaction)

## Контекст

Feasibility-спайк (2026-07-19) подтвердил применимость JetBrains Integration Test Framework (Starter + Driver SDK):
оба теста (`IdeaLifecycleUITest`, `BizGenMainActionUITest`) GREEN на IC 2024.3. Каркас совместим — build, deps,
IDE launch, JMX-канал работают без обходных манёвров.

Текущее покрытие UI:
- **Light-тесты** (`BaseIdeaTest`): сервисный слой (notifications, clipboard, escape-char, settings, persistence,
  soft-updater), структура UI-компонентов (`AppSettingsUiTest`: 6 radio + 1 checkbox exist), `BaseGeneratorAction`.
- **Integration-тесты** (`BaseUIIntegrationTest`): lifecycle smoke, `BizGenMainAction` popup present,
  `NotificationAndClipboardSettingsComponent` (Этап 1 — 4 теста GREEN).

**Не покрыто:** UI-взаимодействия — radio/checkbox → service.update wiring, интерактивный список генераторов
(toggle, move, rename, reset), preview-редактор, popup content + insert to editor.

## Решение

Постепенное покрытие через **Driver/Starter integration-тесты** в `src/integrationTest`. Каждый этап — отдельный
UI-компонент, от простого к сложному. Тесты наследуются от `BaseUIIntegrationTest` (напрямую или через
промежуточную абстракцию `RuBizGenSettingsUITest` для Settings-тестов), запускаются через
`:ru-bizgen-plugin:integrationTest -PrunIntegrationTests=true` (в regular `check` не входят).

## Driver SDK API (ключевые компоненты)

| API | Назначение |
|-----|-----------|
| `ideFrame { }` | Контекст главного окна IDE |
| `ideFrame { openSettingsDialog() }` | Открыть диалог Settings (SDK-хелпер, `invokeAction("ShowSettings", now=false)`) |
| `driver.invokeAction("ShowSettings", now=false)` | Низкоуровневый аналог `openSettingsDialog()` (вне `ideFrame` или нестандартный контекст) |
| `workWithRuBizGenSettings(testName, projectDir) { }` | Бойлерплейн Settings-теста: open + navigate `Tools → Ru BizGen` + `content { }` scope (в `RuBizGenSettingsUITest`) |
| `settingsDialog { openTreeSettingsSectionCompat("Tools", "Ru BizGen"); content { } }` | Навигация по дереву Settings + доступ к панели configurable (compat-обёртка, см. ниже) |
| `radioButtonByVisibleText("...")` | DRY-finder radio по тексту (расширение `Finder`, предпочтительно) |
| `radioButton { and(byType(JRadioButton::class.java), byVisibleText("...")) }` | Поиск radio button по тексту — развёрнутая форма |
| `JRadioButtonUi.isSelected` / `.click()` | Чтение/изменение состояния radio |
| `JRadioButtonUi.shouldBe(message, { isSelected }, timeout)` | Polling-assertion после клика (EDT race fix, см. ниже) |
| `checkBox { byType(JCheckBox::class.java) }` | Поиск checkbox (JBCheckBox через class hierarchy) |
| `JCheckBoxUi.check()` / `.uncheck()` / `.isSelected()` / `.waitSelected(bool)` | Взаимодействие с checkbox |
| `UiComponent.shouldBe(message, present)` / `.should(message, timeout) { }` | Assertions с ожиданием |

### Версия IDE / compat

Тесты идут на **IC 2025.3 (build 253)** — последняя доступная в products-releases API.
Build 253 > 251: нативные `openTreeSettingsSection` (с `replaceCellRendererReader`, добавлен в 251)
и `waitForIndicators` (с `StatusBarEx.getBackgroundProcessModels()`) работают без compat-обёрток.
Driver SDK 261 ↔ runtime 253 — минимальный skew, API совместимы. Compat-слой вырезан.
Подробности миграции: `docs/superpowers/specs/2026-07-25-integration-tests-migrate-to-2026.1-design.md`.

---

## Этап 1: `NotificationAndClipboardSettingsComponent` (стартовый)

**Файл:** `src/integrationTest/kotlin/ru/eda/plgn/bizgen/plugin/settings/NotificationAndClipboardSettingsUITest.kt`
**Базовый класс:** `RuBizGenSettingsUITest` (наследует `BaseUIIntegrationTest`)
**Статус:** ✅ Done — 4 теста GREEN

### Компонент

`NotificationAndClipboardSettingsComponent.createComponent()` (`AppNotificationComponent.kt:24`) содержит:
- **Группа «Уведомления»** (3 radio): BELL, HINT, DISABLE — `actionListener → NotificationSettingsService.updateNotificationMode`
- **Checkbox «Результат генератора копировать в буфер»** — `actionListener + bindSelected → BizGenClipboardSettingsService`
- **Группа «Символ обрамления»** (3 radio): `"`, `'`, `` (none) — `actionListener → EscapeCharSettingsService.setEscapeChar`

### Значения по умолчанию

| Параметр | Default | Текст radio |
|----------|---------|-------------|
| Notification mode | `DISABLE` | `Выключено` |
| Clipboard | `true` (checked) | — |
| Escape char | `"` | `Двойные кавычки (")` |

### Radio button тексты (для `byVisibleText`)

| Группа | Текст | Значение |
|--------|-------|----------|
| Уведомления | `Лог событий "Колокольчик"` | BELL |
| Уведомления | `Всплывающее окно` | HINT |
| Уведомления | `Выключено` | DISABLE (default) |
| Обрамление | `Двойные кавычки (")` | `"` (default) |
| Обрамление | `Одинарные кавычки (')` | `'` |
| Обрамление | `Без обрамления` | `` |

### Тест-сценарии (4 теста)

1. **`Should open Settings dialog and navigate to Ru BizGen page`** — smoke: через `workWithRuBizGenSettings`
   (внутри: `openSettingsDialog()` → `settingsDialog { openTreeSettingsSectionCompat("Tools", "Ru BizGen") }` → `content { }`),
   radio buttons видны через `radioButtonByVisibleText`. Доказывает: Settings открываются, configurable
   регистрирован, контент рендерится.

2. **`Should switch notification mode via radio buttons`** — default DISABLE selected → click HINT → `shouldBe`
   HINT `isSelected`, DISABLE not → click BELL → `shouldBe` BELL selected, HINT not. Polling вместо
   мгновенного `assertTrue` (EDT race fix).

3. **`Should toggle clipboard checkbox`** — default checked → `uncheck()` → `waitSelected(false)` → `check()` →
   `waitSelected(true)`.

4. **`Should switch escape char via radio buttons`** — default `Двойные кавычки (")` selected → click
   `Одинарные кавычки (')` → `shouldBe` selected → click `Без обрамления` → `shouldBe` selected.

### Верификация состояния

Через UI (`shouldBe` с polling для post-click, `waitSelected` для checkbox) — достаточно для Phase 1.
Опционально (Phase 2+): `@Remote` API для чтения `BizGenAppSettingsRepository` из IDE-процесса, или
reopen dialog для проверки persistence.

---

## Этап 1 — Итоги реализации (абстракции)

В ходе реализации Этапа 1 иерархия тестов рефакторена, введены переиспользуемые абстракции.
Все последующие этапы должны на них опираться:

| Абстракция | Файл | Назначение |
|------------|------|-----------|
| `RuBizGenSettingsUITest` | `settings/_RuBizGenSettingsUITest.kt` | База для Settings-тестов. `workWithRuBizGenSettings(testName, projectDir, contentAction)` инкапсулирует `openSettingsDialog()` + `openTreeSettingsSectionCompat` + `content { }` |
| `radioButtonByVisibleText(text)` | `base/finder_ext/RadioButtonExt.kt` | DRY-finder radio по тексту вместо `radioButton { and(byType, byVisibleText) }` |
| `openTreeSettingsSectionCompat` | `settings/ext/SettingsDialogUiComponentExt.kt` | Compat-навигация по дереву Settings (SDK 261 ↔ IC 2024.3) |
| `openSettingsDialog()` | Driver SDK (`IdeaFrameUI`) | `invokeAction("ShowSettings", now=false)` — используется внутри `ideFrame` |

**Правило для Этапов 2–5:** Settings-компоненты (2, 3, 5) наследуют `RuBizGenSettingsUITest`.
Не-Settings (Этап 4 — main action) наследует `BaseUIIntegrationTest` напрямую. Новые
finder-расширения (checkbox/кнопка по тексту) добавлять в `base/finder_ext/`.

---

## Этап 2: `AppActionsSettingComponent` (список генераторов)

**Файл:** `src/integrationTest/kotlin/ru/eda/plgn/bizgen/plugin/settings/AppActionsSettingUITest.kt`
**Базовый класс:** `RuBizGenSettingsUITest` (наследует `BaseUIIntegrationTest`)
**Статус:** ✅ Implementation Complete — `compileIntegrationTestKotlin` GREEN, Pending CI Verification

### Компонент

`AppActionsSettingComponent.createComponent()` (`AppActionsSettingComponent.kt:41`) содержит:
- **`ActionListComponent`** (extends `CheckBoxList<String>`) — список из 31 генератора, каждый элемент: checkbox + текст (`description`)
- **ToolbarDecorator** с 4 кнопками:
  - MoveUp (`setMoveUpAction` → `moveUpOrDown(true)` → `service.moveTo(selectedIndex, UP)`)
  - MoveDown (`setMoveDownAction` → `moveUpOrDown(false)` → `service.moveTo(selectedIndex, DOWN)`)
  - Rename (`addExtraAction` → AnAction "Rename" → `Messages.showInputDialog` → `service.renameAction`)
  - Reset (`addExtraAction` → AnAction "Reset" → `Messages.showYesNoDialog` → `service.restoreByDefault`)
- **Selection listener** → `BizGenSelectedActionEvent.publish(generatorAction)` — публикует выбранный генератор в message bus
- **CheckBoxListListener** → `service.changeActivity(index, value)` — toggle checkbox → изменение активности

### Тест-сценарии (6 тестов)

1. **`Should Driver UI - display generator list in settings`** — smoke: `list { byType(JList::class.java) }` → список присутствует, `items.isNotEmpty()`. Доказывает: Settings открываются, configurable регистрирован, `CheckBoxList` рендерится с генераторами.

2. **`Should Driver UI - update preview when selecting generator`** — `clickItemAtIndex(0)` → `waitOneText` с предикатом: текст начинается с `ActionID: ` и длиннее префикса (т.е. содержит реальный ID). Доказывает: selection → `BizGenSelectedActionEvent.publish` → `ActionResultPreviewComponent` обновил `actionIdLabel`. Верификация через UI (label text), без `@Remote` для message bus.

3. **`Should Driver UI - toggle generator active state via checkbox`** — `@Remote("com.intellij.ui.CheckBoxList")` → `isItemSelected(0)` (default `true`) → `clickItemAtIndex(0, Point(10, cellHeight/2))` (клик по checkbox-области, левая часть ячейки) → `waitFor` state toggle. Первое использование `@Remote` в проекте — чтение состояния `CheckBoxList` из IDE-процесса.

4. **`Should Driver UI - move generator up in list`** — `itemsBefore` → `clickItemAtIndex(1)` (select second item) → `actionButton { byAccessibleName("Up") }.performAction()` → `waitFor { items[0] == itemsBefore[1] }`. Верификация через `JListUiComponent.items` (текущий порядок списка).

5. **`Should Driver UI - rename generator via dialog`** — `clickItemAtIndex(0)` → `actionButton { byAccessibleName("Rename") }.performAction()` → `dialog(title = "Переименование генератора")` → `textField` → Ctrl+A + `typeText("Test Renamed Generator")` → `okButton.click()` → `waitForNoOpenedDialogs()` → `waitFor { items[0] == "Test Renamed Generator" }`. Полный цикл: toolbar button → modal dialog → text input → OK → list update. Использует полный `runIdea` + `ideFrame` pattern (вместо `workWithRuBizGenSettings`) для доступа к dialog вне `content` scope.

6. **`Should Driver UI - reset generators to defaults via dialog`** — rename first item to "Custom Name For Reset" → verify rename → `actionButton { byAccessibleName("Reset") }.performAction()` → `dialog(title = "Сброс Настроек")` → `pressButton("Yes")` → `waitForNoOpenedDialogs()` → `waitFor { items[0] == originalName }`. Комбинирует rename + reset в одном тесте для верификации полного цикла.

### Новые абстракции и API (Этап 2)

| Абстракция / API | Источник | Назначение |
|------------------|----------|-----------|
| `Finder.list { byType(JList::class.java) }` | Driver SDK (`JListUI.kt`) | Поиск `CheckBoxList` (JList) в settings content |
| `JListUiComponent.items` | Driver SDK | Чтение текстов элементов списка (для верификации порядка/содержимого) |
| `JListUiComponent.clickItemAtIndex(index, offset)` | Driver SDK | Клик по элементу: `null` offset → центр ячейки (select); `Point(x, y)` → точная позиция (checkbox toggle) |
| `JListUiComponent.getCellBounds(index)` | Driver SDK | Получение границ ячейки для расчёта offset клика по checkbox |
| `Finder.actionButton { and(byClass("ActionButton"), byAccessibleName("...")) }` | Driver SDK (`ActionButtonUi.kt`) | Поиск ActionButton по accessible name (Rename/Reset/Up/Down). `performAction()` — программный клик |
| `Finder.dialog(title = "...")` | Driver SDK (`Dialog.kt`) | Поиск модального диалога по заголовку (Messages dialogs) |
| `DialogUiComponent.okButton` / `.pressButton("Yes")` | Driver SDK | Взаимодействие с кнопками диалога (OK / Yes) |
| `Finder.textField { byType(JTextField::class.java) }` | Driver SDK (`JTextFieldUI.kt`) | Поиск текстового поля в rename dialog |
| `Finder.waitForNoOpenedDialogs()` | Driver SDK | Ожидание закрытия модального диалога перед продолжением |
| `@Remote("com.intellij.ui.CheckBoxList")` | Driver SDK (`@Remote` mechanism) | Чтение `isItemSelected(index)` — состояния checkbox в `CheckBoxList` из IDE-процесса |
| `UiComponent.waitOneText(message, timeout) { predicate }` | Driver SDK (`UiComponent.kt`) | Polling-ожидание текста с предикатом (для верификации `actionIdLabel` update) |

### Pattern: dialog-тесты (Этап 2+)

Для тестов с модальными диалогами (`Messages.showInputDialog`, `Messages.showYesNoDialog`) используется полный `runIdea` + `ideFrame` pattern вместо `workWithRuBizGenSettings`:

```kotlin
runIdea("testName", projectDir) {
  ideFrame {
    openSettingsDialog()
    settingsDialog {
      openTreeSettingsSectionCompat("Tools", "Ru BizGen")
      content { /* click toolbar button → triggers dialog */ }
    }
    // dialog найден из ideFrame scope (dialog — child of main frame, не settings content)
    dialog(title = "...") { /* interact with dialog */ }
    waitForNoOpenedDialogs()
    settingsDialog {
      content { /* verify result */ }
    }
  }
}
```

**Правило:** `workWithRuBizGenSettings` — для тестов без диалогов (smoke, selection, toggle, move).
Полный `runIdea` + `ideFrame` — для тестов с модальными диалогами (rename, reset).

### ⚠️ Известные риски (Pending CI Verification)

1. **`@Remote("com.intellij.ui.CheckBoxList")` + `isItemSelected`** — метод наследуется из `CheckBoxListBase`. Если `@Remote` dispatch не найдёт inherited method, нужно переключиться на `@Remote("com.intellij.ui.CheckBoxListBase")` или использовать альтернативную верификацию.

2. **`actionButton { byAccessibleName("Up") }` / `"Down"`** — accessible name для MoveUp/MoveDown от `ToolbarDecorator` может отличаться от "Up"/"Down" (зависит от IntelliJ bundle). Если finder не найдёт кнопку, нужно проверить UI hierarchy и скорректировать имя. Rename/Reset — надёжны (явный текст в `AnAction` constructor).

3. **Checkbox toggle click offset** — `Point(10, cellHeight/2)` рассчитан на типичную `CheckBoxList` cell layout (checkbox слева). Если checkbox смещён, нужно скорректировать X offset на основе UI hierarchy.

4. **`pressButton("Yes")` в reset dialog** — кнопка "Yes" от `Messages.showYesNoDialog` использует `CommonBundle.getYesButtonText()`. В English IDE — "Yes". Если CI IDE использует другую локаль, нужно скорректировать.

---

## Roadmap (Этапы 2–5)

| Этап | Компонент | Базовый класс | Сценарии | Сложность | Статус |
|------|-----------|---------------|----------|-----------|--------|
| 1 | `NotificationAndClipboardSettingsComponent` | `RuBizGenSettingsUITest` | radio notif/escape, checkbox clipboard | Низкая | ✅ Done |
| 2 | `AppActionsSettingComponent` | `RuBizGenSettingsUITest` | toggle checkbox→`changeActivity`, move up/down, rename dialog (`Messages.showInputDialog`), reset dialog (`Messages.showYesNoDialog`), selection→`BizGenSelectedActionEvent` publish | Высокая | ✅ Impl Complete (Pending CI) |
| 3 | `ActionResultPreviewComponent` | `RuBizGenSettingsUITest` | selection→preview render в `EditorEx`, Generate refresh, `uniqueDistanceLabel`, dispose | Средняя | Pending |
| 4 | `BizGenMainAction` | `BaseUIIntegrationTest` | popup content (все генераторы в списке), select generator→insert to editor, clipboard copy | Средняя | Pending |
| 5 | `AppSettingsComponent` | `RuBizGenSettingsUITest` | splitter composition, panel layout, dispose | Низкая | Pending |

Каждый этап — отдельный `*UITest.kt` файл в `src/integrationTest` (Settings-этапы — в подпакете `settings`).
Между этапами — ревью и стабилизация.

---

## Build/CI

- Тесты в `src/integrationTest`, наследуются от `BaseUIIntegrationTest` (напрямую или через `RuBizGenSettingsUITest`)
- Запуск: `:ru-bizgen-plugin:integrationTest -PrunIntegrationTests=true`
- Зависимость: `buildPlugin` (собирает plugin zip для установки в тестовую IDE)
- В regular `check` **не входят** (gated by `runIntegrationTests`)
- Ночной CI — `ci-integration.yml`, nightly 03:00 UTC + `workflow_dispatch` (Этап 1 стабилен)
- IDE: IC 2025.3 (default в `BaseUIIntegrationTest.ideVersion`, build 253 > 251 → compat не нужен)

## Критерий успеха (Этап 1) — ✅ Достигнут

- 4 теста GREEN на IC 2025.3 (после разрешения runtime-блокера Starter 261 — см. design-doc миграции)
- Smoke-тест (сценарий 1): Settings dialog → Ru BizGen configurable → контент рендерится
- Сценарии 2–4: radio/checkbox взаимодействие → UI state меняется корректно
- `compileIntegrationTestKotlin` GREEN без ошибок
- Иерархия рефакторена: `RuBizGenSettingsUITest` + `radioButtonByVisibleText` (см. «Итоги реализации»)

## Заметка

Kover-конфигурация в `ru-bizgen-plugin/build.gradle.kts:152-164` исключает пакет `ru.eda.plgn.plugin.bizgen.ui`,
но реальный пакет — `ru.eda.plgn.bizgen.plugin.ui` (сегменты переставлены). UI-пакет фактически НЕ исключён из
покрытия — устаревший конфиг, не влияющий на integration-тесты.
