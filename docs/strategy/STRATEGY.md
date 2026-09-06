# Стратегия развития Ru BizGen

Бэклог направлений: что имеет смысл **добавить в плагин/MCP**, затем **осветить в Guides**.  
Не roadmap с датами — приоритеты и границы.

**Детали и рассуждения** — не здесь, а во вложенных папках (см. [`README.md`](README.md)). В каждой карточке ниже — ссылка **Проработка**.

Связанные документы: [реестр Guides](../presentation-template/TOPICS.md), [шаблон презентаций](../presentation-template/README.md).

Верхнеуровневое деление папок проработки: **`core`** / **`plugin`** / **`ai`**.

---

## Принципы отбора

1. Усиливает обещания продукта: **корректность**, **мгновенность**, **два форм-фактора** (IDE + MCP).
2. Runtime без внешних сетевых зависимостей для генерации. Телеметрия/дашборды — **opt-in** контур наблюдения.
3. Крупная фича заранее знает тему Guides (новый slug — только если не влезает в границы).

---

## 1. Core

Генераторы, контракты данных, качество ядра (`ru-bizgen-core` и связанные тесты).

### 1.1. Связанные наборы (persona / scenario)

|                |                                                                                   |
|----------------|-----------------------------------------------------------------------------------|
| **Суть**       | Согласованный пакет: ФИО + паспорт + СНИЛС + адрес (+ опц. ИНН) одной «личности». |
| **Где**        | Core → plugin → MCP.                                                              |
| **Guide**      | `generators-core` + хвост в `intellij-platform-plugin` / `ai-mcp`.                |
| **Риски**      | Состав сценария; версионирование.                                                 |
| **Проработка** | [`core/persona-scenarios/`](core/persona-scenarios/)                              |

### 1.2. Property-based / oracle-тесты

|                |                                                                       |
|----------------|-----------------------------------------------------------------------|
| **Суть**       | jqwik / Kotest property: «всегда валидный ИНН/IBAN/…» против эталона. |
| **Где**        | `ru-bizgen-core` tests.                                               |
| **Guide**      | `generators-core`.                                                    |
| **Риски**      | Не путать с JMH (корректность ≠ latency).                             |
| **Проработка** | [`core/property-oracle-tests/`](core/property-oracle-tests/)          |

### 1.3. Mutation testing (PIT)

|                |                                                            |
|----------------|------------------------------------------------------------|
| **Суть**       | Мутации checksum/Luhn — тесты реально ловят поломки.       |
| **Где**        | CI / опциональный job; `ru-bizgen-core`.                   |
| **Guide**      | `generators-core` (или ревизия `gradle`).                  |
| **Проработка** | [`core/mutation-testing-pit/`](core/mutation-testing-pit/) |

### 1.4. Diff-тесты против эталонных валидаторов

|                |                                                                |
|----------------|----------------------------------------------------------------|
| **Суть**       | Oracle в test classpath; runtime генерации локальный.          |
| **Где**        | `ru-bizgen-core` tests.                                        |
| **Guide**      | `generators-core`.                                             |
| **Проработка** | [`core/diff-oracle-validators/`](core/diff-oracle-validators/) |

---

## 2. Plugin

UX и поверхность IntelliJ (`ru-bizgen-plugin`).

### 2.1. Intention: «Replace with generated…»

|                |                                                                                |
|----------------|--------------------------------------------------------------------------------|
| **Суть**       | Intention на плейсхолдере / маркере → подставить сгенерированное значение.     |
| **Где**        | `ru-bizgen-plugin`.                                                            |
| **Guide**      | `intellij-platform-plugin`.                                                    |
| **Риски**      | Не спамить IDE; WriteCommandAction / undo.                                     |
| **Проработка** | [`plugin/intentions-replace-generated/`](plugin/intentions-replace-generated/) |

### 2.2. Составные сценарии в UI

|                |                                                                    |
|----------------|--------------------------------------------------------------------|
| **Суть**       | «Юрлицо / физлицо пакетом» — UI над persona (§1.1).                |
| **Где**        | Plugin actions (+ core scenarios).                                 |
| **Guide**      | `intellij-platform-plugin` + `generators-core`.                    |
| **Риски**      | Куда вставлять пакет (строки / clipboard).                         |
| **Проработка** | [`plugin/composite-scenarios-ui/`](plugin/composite-scenarios-ui/) |

### 2.3. i18n (RU/EN UI)

|                |                                                                |
|----------------|----------------------------------------------------------------|
| **Суть**       | Локализация UI (данные RU/EN уже есть).                        |
| **Где**        | `ru-bizgen-plugin`.                                            |
| **Guide**      | `intellij-platform-plugin`.                                    |
| **Риски**      | Soft migration остаётся id-based, не на локализованных именах. |
| **Проработка** | [`plugin/i18n/`](plugin/i18n/)                                 |

Отложено (не в скоупе): Live Templates / Search Everywhere; import/export настроек; project-level overrides.

---

## 3. AI (MCP)

MCP-сервер, транспорты, AI-facing контракты и наблюдаемость (`ru-bizgen-mcp`).

### 3.1. MCP: Resources + Prompts

|                |                                                                               |
|----------------|-------------------------------------------------------------------------------|
| **Суть**       | Resources — каталог из `GeneratorInfo`; Prompts — сценарии («собери юрлицо»). |
| **Где**        | `ru-bizgen-mcp`.                                                              |
| **Guide**      | `ai-mcp`.                                                                     |
| **Риски**      | Синхрон descriptions с реестром.                                              |
| **Проработка** | [`ai/mcp-resources-prompts/`](ai/mcp-resources-prompts/)                      |

### 3.2. stdio-транспорт MCP

|                |                                                          |
|----------------|----------------------------------------------------------|
| **Суть**       | Тот же server, транспорт stdio рядом со Streamable HTTP. |
| **Где**        | `ru-bizgen-mcp`.                                         |
| **Guide**      | `ai-mcp`.                                                |
| **Риски**      | Два старта не разъехать по capabilities/version.         |
| **Проработка** | [`ai/mcp-stdio/`](ai/mcp-stdio/)                         |

### 3.3. Телеметрия MCP + дашборды (Docker)

|                |                                                                                          |
|----------------|------------------------------------------------------------------------------------------|
| **Суть**       | Opt-in observability: usage / gaps (не вызывали) / failures / latency; Grafana в Docker. |
| **Где**        | `ru-bizgen-mcp` + compose-профиль.                                                       |
| **Guide**      | `ai-mcp`.                                                                                |
| **Риски**      | Не раздуть default DX; не смешать с JMH.                                                 |
| **Проработка** | [`ai/mcp-telemetry-grafana/`](ai/mcp-telemetry-grafana/)                                 |

### 3.4. Контракт plugin ↔ MCP ↔ registry

|                |                                                                        |
|----------------|------------------------------------------------------------------------|
| **Суть**       | Исполняемые проверки согласованности type-key / набора / описаний.     |
| **Где**        | archunit / mcp / plugin tests.                                         |
| **Guide**      | `ai-mcp` + мост `archunit`.                                            |
| **Проработка** | [`ai/contract-registry-mcp-plugin/`](ai/contract-registry-mcp-plugin/) |

---

## 4. Приостановлено

Инициативы, проработанные и сознательно отложенные. Папка темы остаётся в своём домене (`core` / `plugin` / `ai`); статус — здесь и в
`NOTES.md`.

### 4.1. Seeded / воспроизводимая генерация

|                |                                                                                  |
|----------------|----------------------------------------------------------------------------------|
| **Статус**     | **Приостановлено** — пока не делать (тесты + явные багрепорты пользователей).    |
| **Суть**       | Опциональный `seed` → детерминированный результат (багрепорт, повтор MCP batch). |
| **Где**        | `ru-bizgen-core` (+ plugin UI / MCP tool args).                                  |
| **Guide**      | `generators-core`; демо в `ai-mcp`.                                              |
| **Риски**      | Thread-safety; не сломать default «всегда свежий».                               |
| **Проработка** | [`core/seed-reproducible/`](core/seed-reproducible/)                             |

---

## 5. Карта «фича → Guide»

| Направление                                         | Основная преза                         | Мосты                |
|-----------------------------------------------------|----------------------------------------|----------------------|
| Persona, property/oracle, mutation, diff-validators | `generators-core`                      | `ai-mcp`, `jmh`      |
| Seed (приостановлено)                               | `generators-core`                      | `ai-mcp`             |
| Intentions, сценарии UI, i18n                       | `intellij-platform-plugin`             | `generators-core`    |
| Resources/Prompts, stdio, телеметрия+Grafana        | `ai-mcp`                               | `archunit`, `gradle` |
| Контракт registry↔MCP↔plugin                        | `ai-mcp` + ревизия `archunit`          | —                    |
| PIT / CI quality gates                              | `generators-core` или ревизия `gradle` | —                    |

---

## 6. Вне скоупа

- Live Templates / Search Everywhere contributor
- Import/export настроек, project-level overrides
- Custom generators DSL, GraalVM native MCP, Compose/Tool Window «студия»
- Телеметрия *плагина* в IDE (отдельно от MCP observability)
- KMP для core

---

## 7. Как обновлять

1. Новая инициатива → карточка в §1–3 + папка `core|plugin|ai/<тема>/` с `NOTES.md` (см. [`README.md`](README.md)).
2. Приостановка → карточка в §4; папка темы **остаётся** в домене, в `NOTES.md` статус «приостановлено».
3. Берём в работу → заметка в наброске Guides: [`../presentation-template/TOPICS.md`](../presentation-template/TOPICS.md).
4. Сделали в коде → статус/PR в проработке темы и при необходимости усилить outline Guides.
