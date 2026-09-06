package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import ru.eda.plgn.bizgen.core.generator.impl.SnilsGenerator

/**
 * Определение дистанции для генератора [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class SnilsGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = SnilsGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)