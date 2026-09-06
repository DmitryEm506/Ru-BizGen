# Политика конфиденциальности Ru BizGen

_Действует с 06.09.2026. Относится к плагину Ru BizGen для IDE на IntelliJ Platform и к MCP-серверу
`ru-bizgen-mcp` из этого же репозитория._

## Коротко

Ru BizGen не собирает, не передаёт и не хранит ваши персональные данные. У плагина нет ни телеметрии,
ни аналитики, ни единого сетевого вызова.

## Что плагин делает с данными

**Генерация происходит локально.** Все значения — ИНН, ОГРН, КПП, СНИЛС, БИК, SWIFT, IBAN, номера
счетов и карт, паспорта, ФИО, адреса, телефоны — вычисляются внутри процесса IDE из встроенных
справочников и генератора случайных чисел. Обращений к внешним сервисам нет.

**Данные синтетические.** Значения не относятся к реальным людям и организациям; любые совпадения
с действительными реквизитами случайны. Использовать их в промышленных системах нельзя.

**Сети нет.** В коде плагина (`ru-bizgen-plugin`) и ядра генерации (`ru-bizgen-core`) отсутствуют
сетевые вызовы: ни HTTP-клиентов, ни сокетов, ни обращений к сервисам JetBrains или сторонним API.

**Телеметрии нет.** Плагин не считает запуски, не отправляет отчёты об ошибках и не регистрируется
в системе сбора статистики IDE.

## Что хранится на вашей машине

Единственный файл, который создаёт плагин, — `bizgen_plugin_settings.xml` в каталоге настроек вашей
IDE. В нём лежат только ваши предпочтения:

- список генераторов, их порядок и признак активности;
- пользовательские названия генераторов, если вы их переименовали;
- режим уведомлений;
- символ обрамления при вставке;
- признак копирования результата в буфер обмена.

Сгенерированные значения в этом файле не сохраняются. Файл не покидает вашу машину — если вы сами
не включили синхронизацию настроек IDE средствами JetBrains, которая работает по правилам JetBrains,
а не этого плагина.

## Буфер обмена и редактор

Плагин вставляет сгенерированное значение в активный редактор. Если включена соответствующая
настройка, значение дополнительно помещается в системный буфер обмена — и дальше живёт по правилам
вашей операционной системы, как любой скопированный текст.

## MCP-сервер

`ru-bizgen-mcp` — отдельный исполняемый файл, который запускаете вы сами. Он использует то же ядро
генерации и так же никуда не ходит: ни телеметрии, ни исходящих запросов.

Сервер принимает входящие HTTP-соединения. По умолчанию он слушает `127.0.0.1:8081`, то есть доступен
только с вашей машины. Запуск с `--host=0.0.0.0` (в том числе в конфигурации Docker из репозитория)
открывает его на всех сетевых интерфейсах — за сетевую доступность в этом случае отвечает тот, кто
его так запустил. Сервер не ведёт журнал запросов с сохранением на диск и не хранит состояние между
вызовами.

## Что вне нашего контроля

Ваша IDE и JetBrains Marketplace собирают собственную статистику — например, факт установки плагина.
Это происходит по политике JetBrains и настраивается в самой IDE; плагин на это не влияет и доступа
к этим данным не имеет.

## Изменения политики

Актуальная версия документа всегда лежит в ветке `main` этого репозитория. История правок доступна
в git.

## Контакты

Вопросы и замечания — через issues репозитория: <https://github.com/DmitryEm506/Ru-BizGen/issues>

---

# Privacy Policy for Ru BizGen

_Effective 2026-09-06. Covers the Ru BizGen plugin for IntelliJ Platform IDEs and the `ru-bizgen-mcp`
server from the same repository._

## In short

Ru BizGen does not collect, transmit, or store your personal data. It has no telemetry, no analytics,
and makes no network calls at all.

## How the plugin handles data

**Generation is local.** Every value — INN, OGRN, KPP, SNILS, BIK, SWIFT, IBAN, bank account and card
numbers, passports, names, addresses, phone numbers — is computed inside the IDE process from bundled
reference data and a random number generator. No external services are contacted.

**The data is synthetic.** Values do not correspond to real people or organizations; any match with
actual identifiers is coincidental. They must not be used in production systems.

**No network.** The plugin (`ru-bizgen-plugin`) and the generation core (`ru-bizgen-core`) contain no
network calls: no HTTP clients, no sockets, no requests to JetBrains or third-party APIs.

**No telemetry.** The plugin does not count invocations, does not send error reports, and does not
register with the IDE's usage statistics framework.

## What is stored on your machine

The only file the plugin creates is `bizgen_plugin_settings.xml` in your IDE configuration directory.
It holds your preferences only:

- the list of generators, their order, and whether each is enabled;
- custom generator names, if you renamed any;
- the notification mode;
- the quoting character used on insertion;
- whether the result is copied to the clipboard.

Generated values are not stored in this file. The file never leaves your machine unless you have
enabled JetBrains IDE settings sync yourself, which operates under JetBrains' rules rather than this
plugin's.

## Clipboard and editor

The plugin inserts the generated value into the active editor. If the corresponding setting is
enabled, the value is also placed on the system clipboard, where it is then governed by your operating
system like any other copied text.

## MCP server

`ru-bizgen-mcp` is a separate executable that you launch yourself. It uses the same generation core
and likewise makes no outbound calls and collects no telemetry.

The server accepts incoming HTTP connections. By default it listens on `127.0.0.1:8081`, reachable
only from your own machine. Running it with `--host=0.0.0.0` (including the Docker configuration in
this repository) exposes it on all network interfaces — in that case network exposure is the
responsibility of whoever started it that way. The server does not write a request log to disk and
keeps no state between calls.

## Outside our control

Your IDE and JetBrains Marketplace collect their own statistics, such as the fact that a plugin was
installed. That happens under JetBrains' policy and is configured in the IDE itself; the plugin does
not influence it and has no access to that data.

## Changes to this policy

The current version of this document always lives on the `main` branch of this repository. The
revision history is available in git.

## Contact

Questions and concerns: <https://github.com/DmitryEm506/Ru-BizGen/issues>
