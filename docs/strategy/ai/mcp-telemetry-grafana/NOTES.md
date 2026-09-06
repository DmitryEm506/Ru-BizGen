# Телеметрия MCP + дашборды (Docker)

Проработка направления. Карточка в общей стратегии: [`../../STRATEGY.md`](../../STRATEGY.md) §3.3.

**Статус:** идея, есть первичный набросок ниже.

При AI-разработке важно видеть не только latency, но и **что агент вызывал / не вызывал** — и по сигналам ошибок понимать, *почему* не вызывал.

## Цели

1. **Usage** — сколько раз вызвали каждый `ru-bizgen_generator_<category>` / `type`.
2. **Gaps** — tools/types без вызовов за сессию/сутки (плохой description или мёртвая поверхность).
3. **Failures** — invalid args, type not found, cancelled, transport 4xx/5xx — подсказки «почему не пользуются».
4. **Latency** — p50/p95 по tool (транспорт + batch); чистый generate по-прежнему в JMH.

## Сигналы (черновик)

| Сигнал | Зачем |
|---|---|
| `tools/list` (или аналог) | Какие tools агент вообще увидел |
| `tools/call` + `type` / category | Usage и top types |
| validation / not found | Почему вызов не состоялся |
| cancel / timeout | Обрывы со стороны клиента |
| dedup / retry counters | Поведение batch |
| duration | e2e latency ≠ JMH µs |

## Docker-контур (opt-in)

- Compose-профиль: MCP + коллектор + **Grafana** (+ Prometheus и/или Tempo/Loki — выбрать минимальный стек в следующей итерации).
- Отдельный target/профиль (`mcp-observability`), не раздувать default-образ.
- Имя файла уточнить: например `docker-compose.observability.yml`.

## Дашборды (минимум)

- RPS / count по tool и `type`
- Error rate и разбивка причин
- p50/p95 latency
- **Вызовы = 0** за период
- Воронка list → call (если удастся коррелировать)

## Приватность

- Не логировать сгенерированные значения / ПДн по умолчанию.
- Метки: type, category, status, duration, error_class.
- Observability выключена в «голом» запуске без профиля.

## Открытые вопросы

- OpenTelemetry metrics vs logs-first (Loki) для gaps.
- Корреляция «модель не вызвала» vs «вызвала с ошибкой» — что реально видно с сервера.
- Нужен ли sample UI/README «как открыть Grafana на localhost».
- Экспорт метрик при stdio-транспорте ([`../mcp-stdio/`](../mcp-stdio/)).
