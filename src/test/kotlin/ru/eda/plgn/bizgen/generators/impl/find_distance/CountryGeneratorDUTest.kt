package ru.eda.plgn.bizgen.generators.impl.find_distance

import ru.eda.plgn.bizgen.generators.impl.CountryGenerator

/**
 * Определение дистанции для генератора [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CountryGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = CountryGenerator(), distanceLimit = 1..10, minimalUniqueDistance = 5)