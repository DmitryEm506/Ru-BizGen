@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.CardNumberGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CardNumberGeneratorBenchmark : StrGeneratorBenchmark(::CardNumberGenerator)