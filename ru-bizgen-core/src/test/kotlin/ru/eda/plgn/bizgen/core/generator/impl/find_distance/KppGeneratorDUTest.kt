package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import ru.eda.plgn.bizgen.core.generator.impl.KppGenerator

/**
 * Определение дистанции для генератора [KppGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class KppGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = KppGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)