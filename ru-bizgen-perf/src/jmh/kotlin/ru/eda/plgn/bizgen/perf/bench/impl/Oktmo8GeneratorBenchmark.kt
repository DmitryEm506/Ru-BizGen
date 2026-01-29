@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.Oktmo8Generator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [Oktmo8Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo8GeneratorBenchmark : StrGeneratorBenchmark(::Oktmo8Generator)