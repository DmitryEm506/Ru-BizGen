@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.OrgRuNameGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [OrgRuNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgRuNameGeneratorBenchmark : StrGeneratorBenchmark(::OrgRuNameGenerator)