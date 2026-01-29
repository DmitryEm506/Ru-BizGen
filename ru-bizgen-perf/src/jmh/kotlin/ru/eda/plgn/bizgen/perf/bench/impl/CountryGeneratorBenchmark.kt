@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.CountryGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CountryGeneratorBenchmark : StrGeneratorBenchmark(::CountryGenerator)