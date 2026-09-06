@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.PhoneNumberRuFormatGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [PhoneNumberRuFormatGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PhoneNumberRuFormatGeneratorBenchmark : StrGeneratorBenchmark(::PhoneNumberRuFormatGenerator)