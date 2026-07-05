package ru.eda.plgn.bizgen.core.generator

import ru.eda.plgn.bizgen.core.utils.withEscape

/**
 * Ответ генератора.
 *
 * @param T тип генерируемых данных
 * @property toClipboard данные для вставки в буфер
 * @property toEditor значение, для вставки в документ
 */
open class GeneratorResult<T : Any>(
  val toClipboard: T,
  val toEditor: String,
) {

  /** Сгенерированное значение. */
  val value: T get() = toClipboard
}

/**
 * Ответ генератора, в котором данные для вставки представляют обрамленные сгенерированные данные символом "
 *
 * @param T тип генерируемых данных
 * @param data сгенерированные данные
 * @param escapeChar символ обрамления (по умолчанию - двойная кавычка)
 */
class GeneratorResultWithEscape<T : Any>(data: T, escapeChar: String = "\"") : GeneratorResult<T>(data, data.toString().withEscape(escapeChar))

/**
 * Ответ генератора в котором данные для вставки - сгенерированные данные.
 *
 * @param T тип генерируемых данных
 * @param data сгенерированные данные
 */
@Suppress("unused")
class GeneratorResultAsIs<T : Any>(data: T) : GeneratorResult<T>(data, data.toString())
