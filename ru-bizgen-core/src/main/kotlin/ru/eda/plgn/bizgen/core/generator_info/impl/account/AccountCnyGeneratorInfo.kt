package ru.eda.plgn.bizgen.core.generator_info.impl.account

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.AccountCnyGenerator

/**
 * Действие, которое использует [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountCnyGeneratorInfo : GeneratorStrInfo(
  id = "AccountCny_4cedac13-b6fa-4b0a-be68-3503702f1564",
  name = "Расчетный CNY счет (20)",
  generator = AccountCnyGenerator()
)