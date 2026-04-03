package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.CardNumberGenerator

/**
 * Действие, которое использует [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CardNumberGeneratorInfo : GeneratorStrInfo(
  id = "CardNumber_92bad607-d900-437c-9605-7ededb10022d",
  name = "Номер карты (16)",
  generator = CardNumberGenerator()
)