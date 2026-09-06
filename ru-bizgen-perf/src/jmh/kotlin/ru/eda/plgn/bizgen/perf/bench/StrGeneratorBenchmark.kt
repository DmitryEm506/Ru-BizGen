package ru.eda.plgn.bizgen.perf.bench

import ru.eda.plgn.bizgen.core.generator.Generator

/**
 * Обертка над [BaseGeneratorBenchmark], которая принимает генераторы, формирующие строковые данные.
 *
 * @param generatorSupplier функция создания генератора
 * @author Dmitry_Emelyanenko
 */
abstract class StrGeneratorBenchmark(generatorSupplier: () -> Generator<String>) : BaseGeneratorBenchmark<String>(generatorSupplier)