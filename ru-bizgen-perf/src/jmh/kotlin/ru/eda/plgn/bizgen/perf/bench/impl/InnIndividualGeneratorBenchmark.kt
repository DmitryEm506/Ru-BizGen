@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.InnIndividualGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [InnIndividualGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnIndividualGeneratorBenchmark : StrGeneratorBenchmark(::InnIndividualGenerator)