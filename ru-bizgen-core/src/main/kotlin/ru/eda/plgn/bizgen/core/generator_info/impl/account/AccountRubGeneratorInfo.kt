package ru.eda.plgn.bizgen.core.generator_info.impl.account

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.AccountRubGenerator

/**
 * Действие, которое использует [AccountRubGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountRubGeneratorInfo : GeneratorStrInfo(
  id = "AccountRub_0ecdce94-c1e4-447a-8dce-94c1e4747aeb",
  name = "Расчетный RUB счет (20)",
  generator = AccountRubGenerator()
)