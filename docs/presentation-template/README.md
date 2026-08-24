# Шаблон презентаций Ru BizGen

Повторяемый процесс: от идеи до публикации на GitHub Pages через Dokka.
Человек вовлечён в формирование контента; HTML и публикация — по единым контрактам.

Реестр тем (есть / готовить): [`TOPICS.md`](TOPICS.md).  
Стратегия развития (фичи → потом Guides): [`../strategy/`](../strategy/).

## Сквозной поток

| Шаг | Участие | Результат |
|---|---|---|
| 1. Идея | Человек | Тема и `slug` (`gradle`, `mcp-server`, …) |
| 2. Контент | Человек + ИИ | `docs/presentations/<slug>/outline.md` по `OUTLINE_CRITERIA.md` + `DENSITY.md` |
| 3. HTML | ИИ | `docs/presentations/<slug>/index.html` по `TEMPLATE.md` |
| 4. Публикация | Автомат | `syncDocsToDokka` + `docs.yml` → Pages |

Подробности шага 4: [`PUBLISH_DOKKA.md`](PUBLISH_DOKKA.md).

## Итеративный контур (рекомендуемый)

1. Согласовать замысел, аудиторию, `slug`, `guides: false` на черновик.
2. Заполнить `outline.md` → ревью плотности → смержить жидкие слайды
   (`# Замечания по плотности`, см. `DENSITY.md`).
3. Сгенерировать HTML (удобно отдельным агентом/чатом, чтобы не жечь контекст outline).
4. Визуальный проход в браузере → точечные правки outline + HTML.
5. Когда стабильно: `guides: true` (или убрать `guides: false`) и проверить Dokka.

Не публиковать полупустые слайды «на потом добьём» — сначала плотность, потом Guides.

## Как сделать новую презентацию

1. Создать каталог `docs/presentations/<slug>/`.
2. Скопировать `outline.example.md` → `outline.md` и заполнить вместе с ИИ
   (лимиты — `OUTLINE_CRITERIA.md`, антипустота — `DENSITY.md`).
3. При необходимости QR: `utils/qr/generate_qr.py` (раздел ниже).
4. В Agent mode отправить запрос ниже (или итеративно править outline → перегенерировать HTML).
5. Открыть `index.html` локально; после `guides: true` и merge в `main`/`dev`
   презентация попадёт в Dokka/Pages без правок меню и Gradle.

Эталоны:

- структура/компоненты: `docs/presentations/archunit/index.html`;
- плотность/duo/SVG: `docs/presentations/gradle/index.html`.

Формат плана: `docs/presentation-template/outline.example.md`.

## Рекомендуемый запрос модели

```text
Создай HTML-презентацию по плану:
@docs/presentations/<slug>/outline.md

Критерии объёма контента:
@docs/presentation-template/OUTLINE_CRITERIA.md

Шпаргалка плотности (антипустые слайды):
@docs/presentation-template/DENSITY.md

Обязательный дизайн-контракт:
@docs/presentation-template/TEMPLATE.md

HTML-каркас:
@docs/presentation-template/template.html

Визуальные эталоны:
@docs/presentations/archunit/index.html
@docs/presentations/gradle/index.html

Публикация в Dokka (не менять вручную под эту тему):
@docs/presentation-template/PUBLISH_DOKKA.md

Результат сохрани в:
@docs/presentations/<slug>/index.html

Не изменяй общие файлы docs/presentation-template/styles.css и
docs/presentation-template/app.js.
Подключи их через ../../presentation-template/styles.css и
../../presentation-template/app.js.

Сохрани дизайн-систему, навигацию и интерактивность шаблона.
Меняй только содержание, количество слайдов и подходящие layout-компоненты.
Не добавляй новую палитру, шрифты, радиусы, тени или иной визуальный стиль.
Не копируй текст из эталонных презентаций (ArchUnit/Gradle) — только паттерны layout.

Плотность: контентные слайды с deck--fill; цель ~80–100% площади карточки;
hero может быть просторным. Пиши ближе к верхней границе бюджетов.
Не оставляй полупустые слайды: добей блоки того же тезиса или предложи merge
в outline. Один тезис на слайд; несколько компонентов тезиса (duo+callout,
facts+QR+выводы, pipeline+SVG+легенда) — норма.

Соблюдай бюджеты из OUTLINE_CRITERIA.md: при переполнении разделяй слайды,
но нельзя терять обязательные факты.
QR (если в outline): PNG рядом с index.html, компонент .qr-card;
генерация через docs/presentation-template/utils/qr/generate_qr.py.
SVG: компактные узлы, длинные подписи в две строки, стрелки к граням узлов.
Не правь header.ftl, Module.md, footer и Gradle ради одной темы —
презентация сама попадёт в Guides через guides.json.
Перед завершением проверь навигацию, оглавление, переполнение, наложение,
пустые низы слайдов и SVG.
```

Замените `<slug>` коротким именем темы, например `gradle`, `kotlin-coroutines`
или `mcp-server`.

## Локальная проверка публикации

```bash
./gradlew dokkaGenerateHtml
```

Открыть:

- шапку Dokka → **Guides** (автосписок из `guides.json`);
- `build/dokka/html/images/presentations/<slug>/index.html` — конкретная презентация;
- `build/dokka/html/images/presentations/archunit/index.html` — ArchUnit;
- `build/dokka/html/images/presentations/gradle/index.html` — Gradle (если `guides` включён).

## QR-код для слайда

Переиспользуемый скрипт (вместо разовой генерации на каждую тему):

```bash
pip install -r docs/presentation-template/utils/qr/requirements-qr.txt

python docs/presentation-template/utils/qr/generate_qr.py https://example.org \
  -o docs/presentations/<slug>/<name>-qr.png
```

PNG кладётся рядом с `index.html`. В HTML — `.qr-card` (эталоны: ArchUnit, Gradle).

## Файлы

- `TOPICS.md` — реестр презентаций: что есть и что готовить.
- `TEMPLATE.md` — контракт генерации HTML (дизайн, layout, готовность).
- `OUTLINE_CRITERIA.md` — бюджеты объёма и правило плотности для `outline.md`.
- `DENSITY.md` — шпаргалка «антипустые слайды».
- `PUBLISH_DOKKA.md` — универсальная публикация в Dokka/Pages.
- `template.html` — HTML-каркас + каталог плотных layout-паттернов (archunit/gradle).
- `outline.example.md` — формат плана.
- `styles.css` / `app.js` — единый runtime всех презентаций.
- `utils/qr/generate_qr.py` / `utils/qr/requirements-qr.txt` — генерация QR PNG для `.qr-card`.

## Важный принцип

Не создавайте отдельную CSS-тему для каждой презентации. Новая презентация должна
подключать общие `styles.css` и `app.js`. Исправление дизайна применяется ко всем
слайдам сразу; публикация не требует бойлерплейта на тему.
Плотность контента важнее короткого счётчика слайдов: лучше 14 полных, чем 20
полупустых.
