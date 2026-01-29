@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.OgrnLegalGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [OgrnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnLegalGeneratorBenchmark : StrGeneratorBenchmark(::OgrnLegalGenerator)