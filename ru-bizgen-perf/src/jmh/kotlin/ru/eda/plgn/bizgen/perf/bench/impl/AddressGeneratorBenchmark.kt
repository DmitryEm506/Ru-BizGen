@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.AddressGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AddressGeneratorBenchmark : StrGeneratorBenchmark(::AddressGenerator)