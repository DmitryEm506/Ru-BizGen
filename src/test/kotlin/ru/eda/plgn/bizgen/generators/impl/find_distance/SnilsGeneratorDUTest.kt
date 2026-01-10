package ru.eda.plgn.bizgen.generators.impl.find_distance

import ru.eda.plgn.bizgen.generators.impl.SnilsGenerator

/**
 * Определение дистанции для генератора [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class SnilsGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = SnilsGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)