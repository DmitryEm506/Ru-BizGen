@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.BikGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BikGeneratorBenchmark : StrGeneratorBenchmark(::BikGenerator)