@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.FIOFullGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [FIOFullGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOFullGeneratorBenchmark : StrGeneratorBenchmark(::FIOFullGenerator)