@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.AccountRubGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [AccountRubGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountRubGeneratorBenchmark : StrGeneratorBenchmark(::AccountRubGenerator)