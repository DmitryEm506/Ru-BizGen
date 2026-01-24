package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.BankAccountGenerator

/**
 * Действие, которое использует [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BankAccountActionGenerator : StrActionGenerator(
  id = "BankAccount_9009b9eb-f622-4599-89b9-ebf622559957",
  name = "Корреспондентский счёт (20)",
  generator = BankAccountGenerator()
)