@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.InnLegalGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [InnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnLegalGeneratorBenchmark : StrGeneratorBenchmark(::InnLegalGenerator)