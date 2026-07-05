@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuMrzGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [ForeignPassportRuMrzGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuMrzGeneratorBenchmark : StrGeneratorBenchmark(::ForeignPassportRuMrzGenerator)
