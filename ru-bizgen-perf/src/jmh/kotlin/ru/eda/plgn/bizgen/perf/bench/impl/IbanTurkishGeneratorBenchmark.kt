@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.IbanTurkishGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [IbanTurkishGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanTurkishGeneratorBenchmark : StrGeneratorBenchmark(::IbanTurkishGenerator)