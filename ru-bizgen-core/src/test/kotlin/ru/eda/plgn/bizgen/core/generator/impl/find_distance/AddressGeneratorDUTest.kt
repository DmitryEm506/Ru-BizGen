package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import ru.eda.plgn.bizgen.core.generator.impl.AddressGenerator

/**
 * Определение дистанции для генераторов: [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AddressGeneratorDUTest :
  Percentile95DistanceUniqStrTest(AddressGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)