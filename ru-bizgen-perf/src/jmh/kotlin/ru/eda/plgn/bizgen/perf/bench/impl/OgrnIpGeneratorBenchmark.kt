@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.OgrnIpGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnIpGeneratorBenchmark : StrGeneratorBenchmark(::OgrnIpGenerator)