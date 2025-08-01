package ru.person.bizgen.generators

/**
 * Ответ генератора.
 *
 * @param T тип генерируемых данных
 * @property toClipboard данные для вставки в буфер
 * @property toEditor значение, для вставки в документ
 */
open class GeneratorResult<T : Any>(
  val toClipboard: T,
  val toEditor: String
)

/**
 * Ответ генератора в котором данные для вставки представляют обрамленные сгенерированные данные символом "
 *
 * @param T тип генерируемых данных
 * @property data сгенерированные данные
 */
class GeneratorResultWithEscape<T : Any>(data: T) : GeneratorResult<T>(data, """"$data"""")

/**
 * Ответ генератора в котором данные для вставки - сгенерированные данные.
 *
 * @param T тип генерируемых данных
 * @property data сгенерированные данные
 */
class GeneratorResultAsIs<T : Any>(data: T) : GeneratorResult<T>(data, data.toString())
