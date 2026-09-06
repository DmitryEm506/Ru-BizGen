@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.Oktmo11Generator
import ru.eda.plgn.bizgen.perf.bench.StrGeneratorBenchmark

/**
 * Бенчмарк для генератора [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo11GeneratorBenchmark : StrGeneratorBenchmark(::Oktmo11Generator)