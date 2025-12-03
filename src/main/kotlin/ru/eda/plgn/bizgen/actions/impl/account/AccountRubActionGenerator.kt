package ru.eda.plgn.bizgen.actions.impl.account

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.AccountRubGenerator

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