# Стратегия развития Ru BizGen

Каталог продуктовых и инженерных инициатив: общая картина в одном файле, детали — во вложенных папках.

## Как устроено

| Файл / папка                 | Назначение                                                                                    |
|------------------------------|-----------------------------------------------------------------------------------------------|
| [`STRATEGY.md`](STRATEGY.md) | **Общая стратегия**: карточки по доменам, приостановленные, карта «фича → Guide», вне скоупа. |
| [`core/`](core/)             | Ядро генерации и качество core (`ru-bizgen-core`).                                            |
| [`plugin/`](plugin/)         | IntelliJ UX и поверхность плагина (`ru-bizgen-plugin`).                                       |
| [`ai/`](ai/)                 | MCP / AI-facing инициативы (`ru-bizgen-mcp` и связанные контракты).                           |
| `<domain>/<тема>/`           | Проработка одной темы (`NOTES.md`, при росте — `adr/`, `diagrams/` и т.п.).                   |

Правила:

1. В [`STRATEGY.md`](STRATEGY.md) — только сжатый срез и **ссылка** на папку темы. Длинные рассуждения сюда не разрастаются.
2. Проработка темы → файлы **внутри** её папки (обычно начать с `NOTES.md`).
3. Новая инициатива: карточка в `STRATEGY.md` §1–3 + папка `core|plugin|ai/<тема>/` со стартовым `NOTES.md`.
4. Приостановка: карточка в §4; папка остаётся в домене, в `NOTES.md` — статус «приостановлено».
5. Связь с Guides: реестр — [`../presentation-template/TOPICS.md`](../presentation-template/TOPICS.md); шаблон — [
   `../presentation-template/README.md`](../presentation-template/README.md).

## Темы

### Core

- [`core/persona-scenarios/`](core/persona-scenarios/)
- [`core/property-oracle-tests/`](core/property-oracle-tests/)
- [`core/mutation-testing-pit/`](core/mutation-testing-pit/)
- [`core/diff-oracle-validators/`](core/diff-oracle-validators/)
- [`core/seed-reproducible/`](core/seed-reproducible/) *(приостановлено)*

### Plugin

- [`plugin/intentions-replace-generated/`](plugin/intentions-replace-generated/)
- [`plugin/composite-scenarios-ui/`](plugin/composite-scenarios-ui/)
- [`plugin/i18n/`](plugin/i18n/)

### AI

- [`ai/mcp-resources-prompts/`](ai/mcp-resources-prompts/)
- [`ai/mcp-stdio/`](ai/mcp-stdio/)
- [`ai/mcp-telemetry-grafana/`](ai/mcp-telemetry-grafana/)
- [`ai/contract-registry-mcp-plugin/`](ai/contract-registry-mcp-plugin/)
