@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.IbanRuGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [IbanRuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanRuGeneratorBenchmark : StrGeneratorBenchmark(::IbanRuGenerator)