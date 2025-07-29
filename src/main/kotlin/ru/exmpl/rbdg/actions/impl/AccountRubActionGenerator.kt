package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.AccountRubGenerator

/**
 * Действие, которое использует [AccountRubGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountRubActionGenerator : BaseGeneratorAction<String>(
  id = "AccountRub_0ecdce94-c1e4-447a-8dce-94c1e4747aeb",
  name = "Расчетный RUN счет (20)",
  generator = AccountRubGenerator()
)