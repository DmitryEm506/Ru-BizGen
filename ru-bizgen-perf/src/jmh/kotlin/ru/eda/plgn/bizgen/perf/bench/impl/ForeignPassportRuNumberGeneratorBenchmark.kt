@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.ForeignPassportRuNumberGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [ForeignPassportRuNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class ForeignPassportRuNumberGeneratorBenchmark : StrGeneratorBenchmark(::ForeignPassportRuNumberGenerator)
