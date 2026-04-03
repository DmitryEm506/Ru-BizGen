@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.OrgEngNameGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgEngNameGeneratorBenchmark : StrGeneratorBenchmark(::OrgEngNameGenerator)