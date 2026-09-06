package ru.eda.plgn.bizgen.core.generator.impl.find_distance

import ru.eda.plgn.bizgen.core.generator.impl.BankAccountGenerator

/**
 * Определение дистанции для генератора [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BankAccountGeneratorDUTest :
  Percentile95DistanceUniqStrTest(generator = BankAccountGenerator(), distanceLimit = 120..130, minimalUniqueDistance = 130)