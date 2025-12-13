package ru.eda.plgn.bizgen.generators.impl.find_distance

import ru.eda.plgn.bizgen.generators.impl.AddressGenerator

/**
 * Определение дистанции для генераторов: [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AddressGeneratorDUTest :
  Percentile95DistanceUniqStrTest(AddressGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)