# Публикация презентаций в Dokka

Этот файл — контракт **универсальной публикации** слайдов на GitHub Pages
через Dokka. Генерация HTML описана в `TEMPLATE.md` и `OUTLINE_CRITERIA.md`;
здесь только путь от готового `index.html` до публичного URL.

## 1. Целевой поток (минимальное участие человека)

| Шаг | Кто | Что происходит |
|---|---|---|
| 1. Идея | Человек | Тема презентации |
| 2. Контент | Человек + ИИ | Совместно заполняется `docs/presentations/<slug>/outline.md` |
| 3. HTML | ИИ | `docs/presentations/<slug>/index.html` по `TEMPLATE.md` |
| 4. Публикация | Автомат | `syncDocsToDokka` копирует слайды и обновляет манифест Guides; `docs.yml` деплоит Pages |

Человек участвует только в шаге 2 (и ревью HTML при необходимости).
Шаги 3–4 не требуют правок `header.ftl`, footer или Module.md на каждую тему:
презентация сама появляется во вкладке **Guides**.

## 2. Guides вместо отдельного hub

Отдельная страница `presentations/index.html` **не используется**.

| Механизм | Назначение |
|---|---|
| `generatePresentationsGuides` | Пишет `images/presentations/guides.json` |
| `header.ftl` | Читает JSON и заполняет dropdown **Guides** |
| Copy презентаций | `docs/presentations/<slug>/` → `images/presentations/<slug>/` |

Сейчас в репозитории в Guides попадают презентации без `guides: false`
в `outline.md` (например ArchUnit, Gradle). Новые темы добавляются тем же путём.

Опционально в front matter `outline.md`:

```yaml
guides: false   # не показывать во вкладке Guides (черновик)
```

По умолчанию (нет поля / нет outline) — презентация **попадает** в Guides.

## 3. Раскладка путей

```text
docs/presentations/<slug>/index.html
  → ../../presentation-template/{styles.css,app.js}

build/dokka/html/images/
  presentation-template/   styles.css, app.js
  presentations/
    guides.json            ← манифест для Guides
    <slug>/index.html
  kover/…
```

## 4. Gradle-задачи

Скрипт: `build-logic` → `ru-bizgen.dokka-root-convention`
(подключается из корневого `build.gradle.kts`).

| Задача | Назначение |
|---|---|
| `copyPresentationRuntimeToDokka` | CSS/JS шаблона |
| `copyPresentationsToDokka` | все `*/index.html` (+ медиа) |
| `generatePresentationsGuides` | `guides.json` + удаление устаревшего hub |
| `syncDocsToDokka` | три задачи выше |

`DokkaGenerateTask` → `finalizedBy(copyKoverToDokka, syncDocsToDokka)`.

```bash
./gradlew dokkaGenerateHtml
# Guides в шапке Dokka → пункты из guides.json
# build/dokka/html/images/presentations/<slug>/index.html
```

## 5. Навигация Dokka

| Место | Содержимое |
|---|---|
| Guides | Автосписок презентаций из `guides.json` |
| Footer | Code Coverage · ArchUnit (быстрый pin) |
| Module.md | Краткое описание модуля + как работают Guides (без списка тем) |

`header.ftl` и `Module.md` правятся только при смене UX/описания процесса,
не при добавлении каждой новой презентации.

## 6. Конвенция `<slug>`

- `[a-z0-9]+(-[a-z0-9]+)*`
- Имя каталога = сегмент URL
- Один `index.html` на каталог
- `outline.md` в Pages не копируется

## 7. Критерии готовности

После `./gradlew dokkaGenerateHtml`:

- [ ] есть `images/presentations/guides.json`;
- [ ] нет `images/presentations/index.html` (hub);
- [ ] Guides в шапке показывает все опубликованные презентации;
- [ ] каждая презентация открывается со стилями;
- [ ] footer / Module.md не ссылаются на hub.

## 8. Связанные файлы

| Файл | Роль |
|---|---|
| `TEMPLATE.md` | Генерация HTML |
| `OUTLINE_CRITERIA.md` | Объём outline |
| `PUBLISH_DOKKA.md` | Этот контракт |
| `ru-bizgen.dokka-root-convention` | `syncDocsToDokka`, Guides JSON, footer/Dokka HTML |
| `.config/dokka/templates/includes/header.ftl` | Рендер Guides из JSON |
| `.github/workflows/docs.yml` | Deploy Pages |
