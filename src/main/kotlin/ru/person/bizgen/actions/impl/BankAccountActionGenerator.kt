package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.BankAccountGenerator

/**
 * Действие, которое использует [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BankAccountActionGenerator : BaseGeneratorAction<String>(
  id = "BankAccount_9009b9eb-f622-4599-89b9-ebf622559957",
  name = "Корреспондентский счёт (20)",
  generator = BankAccountGenerator()
)