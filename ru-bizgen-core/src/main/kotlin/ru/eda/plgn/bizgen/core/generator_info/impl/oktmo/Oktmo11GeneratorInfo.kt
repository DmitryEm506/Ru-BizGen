package ru.eda.plgn.bizgen.core.generator_info.impl.oktmo

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.Oktmo11Generator

/**
 * Действие, которое использует [Oktmo11Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo11GeneratorInfo : GeneratorStrInfo(
  id = "Oktmo11_3ea1bc6b-011c-4bdc-a1bc-6b011c1bdc7c",
  name = "ОКТМО (11)",
  generator = Oktmo11Generator(),
  category = GeneratorCategory.LEGAL,
  detailedDescription = "ОКТМО (11 цифр) — общероссийский классификатор территорий муниципальных образований. Формат для населённых пунктов.",
  example = "46000000169",
)