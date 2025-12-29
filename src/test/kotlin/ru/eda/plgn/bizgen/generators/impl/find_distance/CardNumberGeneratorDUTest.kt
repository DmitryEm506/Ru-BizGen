package ru.eda.plgn.bizgen.generators.impl.find_distance

import ru.eda.plgn.bizgen.generators.impl.CardNumberGenerator

/**
 * Определение дистанции для генератора [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class CardNumberGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = CardNumberGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)