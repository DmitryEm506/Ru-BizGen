@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.BankAccountGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BankAccountGeneratorBenchmark : StrGeneratorBenchmark(::BankAccountGenerator)