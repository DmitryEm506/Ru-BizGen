## ADDED Requirements

### Requirement: Generator metadata completeness

Каждый `GeneratorInfo` из `GeneratorInfoProvider.generatorInfos` SHALL предоставлять ненулевые метаданные `category`, `detailedDescription` и `example`. Это единый источник истины для MCP enum-описаний и будущей группировки в плагине.

#### Scenario: Все генераторы имеют категорию
- **WHEN** вызывается `GeneratorInfoProvider.generatorInfos`
- **THEN** каждый элемент списка имеет непустое значение `category`, принадлежащее enum `GeneratorCategory`

#### Scenario: Все генераторы имеют расширенное описание
- **WHEN** вызывается `GeneratorInfoProvider.generatorInfos`
- **THEN** каждый элемент списка имеет непустую строку `detailedDescription`, описывающую генерируемое значение (назначение + формат + контрольная сумма где применима)

#### Scenario: Все генераторы имеют стабильный пример
- **WHEN** вызывается `GeneratorInfoProvider.generatorInfos`
- **THEN** каждый элемент списка имеет непустую строку `example`, представляющую конкретное валидное значение (не результат вызова `generate()`)

### Requirement: GeneratorCategory types

Система SHALL определять enum `GeneratorCategory` с пятью значениями: `TECHNICAL`, `BANKING`, `LEGAL`, `GEO`, `PERSONAL`. Каждая категория соответствует группе генераторов, исторически выделенной комментариями в `GeneratorInfoProvider`.

#### Scenario: UUID относится к технической категории
- **WHEN** берётся `GeneratorInfo` с `id`, начинающимся на `UUID_`
- **THEN** его `category` равна `GeneratorCategory.TECHNICAL`

#### Scenario: ИНН относится к юридической категории
- **WHEN** берётся `GeneratorInfo` с `id`, начинающимся на `InnLegal_` или `InnIndividual_`
- **THEN** его `category` равна `GeneratorCategory.LEGAL`

#### Scenario: Расчётный счёт относится к банковской категории
- **WHEN** берётся `GeneratorInfo` с `id`, начинающимся на `AccountRub_` или `AccountCny_`
- **THEN** его `category` равна `GeneratorCategory.BANKING`

### Requirement: Example validity

Значение `example` каждого `GeneratorInfo` SHALL проходить валидацию формата генератора, где формат подразумевает контрольную сумму (ИНН, КПП, ОГРН, СНИЛС, карта, IBAN, счёт).

#### Scenario: Пример ИНН ЮЛ проходит проверку контрольной суммы
- **WHEN** берётся `example` у `InnLegalGeneratorInfo`
- **THEN** значение проходит валидацию по весам P10/P11/P12 (как реальный ИНН юр. лица)

#### Scenario: Пример IBAN RU проходит проверку mod-97
- **WHEN** берётся `example` у `IbanRuGeneratorInfo`
- **THEN** значение проходит валидацию mod-97 (как реальный IBAN)

#### Scenario: Пример UUID имеет корректный формат
- **WHEN** берётся `example` у `UuidGeneratorInfo`
- **THEN** значение парсится `java.util.UUID.fromString` без исключения

### Requirement: Identifier stability

Значения `id` и `name` существующих `GeneratorInfo` SHALL оставаться неизменными после добавления новых полей `category`, `detailedDescription`, `example`. Это обеспечивает совместимость с персистентным слоем плагина (`BizGenAppSettingsSoftUpdater`), ключённым по `id`.

#### Scenario: Идентификаторы генераторов не меняются
- **WHEN** сравниваются `id` всех `GeneratorInfo` до и после добавления метаданных
- **THEN** каждый `id` побайтово совпадает с предыдущей версией

#### Scenario: Имена генераторов не меняются
- **WHEN** сравниваются `name` всех `GeneratorInfo` до и после добавления метаданных
- **THEN** каждая строка `name` побайтово совпадает с предыдущей версией

### Requirement: GeneratorInfoProvider grouping by category

`GeneratorInfoProvider.generatorInfos` SHALL возвращать генераторы, сгруппированные по `category` в порядке `TECHNICAL`, `BANKING`, `LEGAL`, `GEO`, `PERSONAL`. Порядок внутри категории сохраняет исторический следование генераторов (минимизация diff).

#### Scenario: Технические генераторы идут первыми
- **WHEN** берётся `GeneratorInfoProvider.generatorInfos.first()`
- **THEN** его `category` равна `GeneratorCategory.TECHNICAL`

#### Scenario: Персональные генераторы идут последними
- **WHEN** берётся `GeneratorInfoProvider.generatorInfos.last()`
- **THEN** его `category` равна `GeneratorCategory.PERSONAL`

#### Scenario: Генераторы одной категории идут подряд
- **WHEN** фильтруется список по `category == GeneratorCategory.LEGAL`
- **THEN** индексы этих элементов образуют непрерывный диапазон (без разрывов)