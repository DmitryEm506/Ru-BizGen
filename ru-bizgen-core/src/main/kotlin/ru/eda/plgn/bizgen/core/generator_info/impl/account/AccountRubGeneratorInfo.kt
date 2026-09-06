package ru.eda.plgn.bizgen.core.generator_info.impl.account

import ru.eda.plgn.bizgen.core.generator.impl.AccountRubGenerator
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo

/**
 * Действие, которое использует [AccountRubGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountRubGeneratorInfo : GeneratorStrInfo(
  id = "AccountRub_0ecdce94-c1e4-447a-8dce-94c1e4747aeb",
  name = "Расчетный RUB счет (20)",
  generator = AccountRubGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "Расчётный счёт в рублях (20 цифр). Контрольный ключ (9-я цифра) рассчитывается по Положению ЦБ РФ № 515 на основе БИК.",
  example = "40701810112987191913",
)