package ru.eda.plgn.bizgen.generators.impl.find_distance

import ru.eda.plgn.bizgen.generators.impl.BikGenerator

/**
 * Определение дистанции для генератора [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BikGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = BikGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)