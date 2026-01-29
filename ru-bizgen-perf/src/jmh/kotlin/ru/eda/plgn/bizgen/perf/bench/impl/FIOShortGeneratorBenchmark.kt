@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.FIOShortGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [FIOShortGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOShortGeneratorBenchmark : StrGeneratorBenchmark(::FIOShortGenerator)