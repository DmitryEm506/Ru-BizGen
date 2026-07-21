package ru.eda.plgn.bizgen.core.generator_info.impl.account

import ru.eda.plgn.bizgen.core.generator.impl.AccountCnyGenerator
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo

/**
 * Действие, которое использует [AccountCnyGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AccountCnyGeneratorInfo : GeneratorStrInfo(
  id = "AccountCny_4cedac13-b6fa-4b0a-be68-3503702f1564",
  name = "Расчетный CNY счет (20)",
  generator = AccountCnyGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "Расчётный счёт в юанях (20 цифр). Контрольный ключ (9-я цифра) рассчитывается по Положению ЦБ РФ № 515 на основе БИК.",
  example = "40701156624359999580",
)