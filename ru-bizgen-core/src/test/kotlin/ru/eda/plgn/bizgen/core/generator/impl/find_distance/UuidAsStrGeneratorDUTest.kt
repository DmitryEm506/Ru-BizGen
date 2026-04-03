package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import ru.eda.plgn.bizgen.core.generator.impl.UuidAsStrGenerator
import java.util.UUID

/**
 * Определение дистанции для генератора [UuidAsStrGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class UuidAsStrGeneratorDUTest :
  Percentile95DistanceUniqTest<UUID>(generator = UuidAsStrGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)