package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.CardNumberGenerator

/**
 * Действие, которое использует [CardNumberGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CardNumberActionGenerator : StrActionGenerator(
  id = "CardNumber_92bad607-d900-437c-9605-7ededb10022d",
  name = "Номер карты (16)",
  generator = CardNumberGenerator()
)