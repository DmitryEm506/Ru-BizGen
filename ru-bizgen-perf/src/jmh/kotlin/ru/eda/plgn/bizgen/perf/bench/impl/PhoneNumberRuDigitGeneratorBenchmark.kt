@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuDigitGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [PhoneNumberRuDigitGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuDigitGeneratorBenchmark : StrGeneratorBenchmark(::PhoneNumberRuDigitGenerator)