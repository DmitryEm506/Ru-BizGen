@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.SnilsGenerator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class SnilsGeneratorBenchmark : StrGeneratorBenchmark(::SnilsGenerator)