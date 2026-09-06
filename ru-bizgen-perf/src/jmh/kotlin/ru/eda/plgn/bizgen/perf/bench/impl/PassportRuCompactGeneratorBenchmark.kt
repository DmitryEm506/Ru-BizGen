@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.PassportRuCompactGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [PassportRuCompactGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuCompactGeneratorBenchmark : StrGeneratorBenchmark(::PassportRuCompactGenerator)
