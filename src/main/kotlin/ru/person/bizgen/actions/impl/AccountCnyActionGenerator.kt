package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.AccountCnyGenerator

/**
 * Действие, которое использует [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountCnyActionGenerator : BaseGeneratorAction<String>(
  id = "AccountCny_4cedac13-b6fa-4b0a-be68-3503702f1564",
  name = "Расчетный CNY счет (20)",
  generator = AccountCnyGenerator()
)