package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Percentile95DistanceUniqStrTest
import ru.eda.plgn.bizgen.generators.UniqDistanceCategory

/**
 * Определение дистанции для генераторов: [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class AddressGeneratorDistanceUniqueTest : Percentile95DistanceUniqStrTest(AddressGenerator(), UniqDistanceCategory.HIGH)