@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.Swift8RuGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [Swift8RuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class Swift8RuGeneratorBenchmark : StrGeneratorBenchmark(::Swift8RuGenerator)