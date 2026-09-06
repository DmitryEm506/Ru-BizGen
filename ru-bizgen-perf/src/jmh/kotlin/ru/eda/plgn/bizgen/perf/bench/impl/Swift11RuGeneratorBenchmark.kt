@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.Swift11RuGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [Swift11RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift11RuGeneratorBenchmark : StrGeneratorBenchmark(::Swift11RuGenerator)