package ru.eda.plgn.bizgen.core.utils

/**
 * Обрамление исходной строки в кавычки.
 *
 * Например:
 *
 * 207729165200 --> "207729165200"
 *
 * @param escapeChar символ обрамления (по умолчанию - двойная кавычка)
 */
fun String.withEscape(escapeChar: String = "\"") = "$escapeChar$this$escapeChar"