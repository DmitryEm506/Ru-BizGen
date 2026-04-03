@file:Suppress("unused")

package ru.eda.plgn.bizgen.perf.bench.impl

import ru.eda.plgn.bizgen.core.generator.impl.UuidAsStrGenerator
import ru.eda.plgn.bizgen.perf.bench.BaseGeneratorBenchmark
import java.util.UUID

/**
 * Бенчмарк для генератора [UuidAsStrGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidAsStrGeneratorBenchmark : BaseGeneratorBenchmark<UUID>(::UuidAsStrGenerator)