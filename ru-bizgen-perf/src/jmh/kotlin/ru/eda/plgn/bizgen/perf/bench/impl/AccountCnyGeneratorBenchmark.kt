@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.AccountCnyGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountCnyGeneratorBenchmark : StrGeneratorBenchmark(::AccountCnyGenerator)