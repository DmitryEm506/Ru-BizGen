package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.BankAccountGenerator

/**
 * Действие, которое использует [BankAccountGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BankAccountGeneratorInfo : GeneratorStrInfo(
  id = "BankAccount_9009b9eb-f622-4599-89b9-ebf622559957",
  name = "Корреспондентский счёт (20)",
  generator = BankAccountGenerator(),
  category = GeneratorCategory.BANKING,
  detailedDescription = "Корреспондентский счёт (20 цифр). Контрольный ключ рассчитывается по Положению ЦБ РФ № 515 с использованием кода РКЦ из БИК.",
  example = "30101810896060000552",
)