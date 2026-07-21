package ru.eda.plgn.bizgen.core.generator_info.impl.oktmo

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.Oktmo8Generator

/**
 * Действие, которое использует [Oktmo8Generator].
 *
 * @author Dmitry_Emelyanenko
 */
class Oktmo8GeneratorInfo : GeneratorStrInfo(
  id = "Oktmo8_d683fd6c-23bb-4ccb-83fd-6c23bbaccb89",
  name = "ОКТМО (8)",
  generator = Oktmo8Generator(),
  category = GeneratorCategory.LEGAL,
  detailedDescription = "ОКТМО (8 цифр) — общероссийский классификатор территорий муниципальных образований. Формат для муниципальных образований.",
  example = "73401000",
)