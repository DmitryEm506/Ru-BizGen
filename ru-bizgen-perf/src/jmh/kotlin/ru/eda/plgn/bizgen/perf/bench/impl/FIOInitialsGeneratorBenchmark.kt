@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.FIOInitialsGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [FIOInitialsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOInitialsGeneratorBenchmark : StrGeneratorBenchmark(::FIOInitialsGenerator)