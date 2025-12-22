package ru.eda.plgn.bizgen.utils

/**
 * Обрамление исходной строки в кавычки.
 *
 * Например:
 *
 * 207729165200 --> "207729165200"
 */
fun String.withEscape() = "\"$this\""