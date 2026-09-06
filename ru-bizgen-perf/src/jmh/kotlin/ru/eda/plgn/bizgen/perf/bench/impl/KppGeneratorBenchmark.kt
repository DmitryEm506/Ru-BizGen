@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.KppGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [KppGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class KppGeneratorBenchmark : StrGeneratorBenchmark(::KppGenerator)