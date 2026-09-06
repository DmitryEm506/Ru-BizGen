@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.PassportRuSpacedGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [PassportRuSpacedGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class PassportRuSpacedGeneratorBenchmark : StrGeneratorBenchmark(::PassportRuSpacedGenerator)
