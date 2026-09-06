package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.KppGenerator

/**
 * Действие, которое использует [KppGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class KppGeneratorInfo : GeneratorStrInfo(
  id = "Kpp_d04771b5-6e0c-42dc-8771-b56e0ca2dcb9",
  name = "КПП (9)",
  generator = KppGenerator(),
  category = GeneratorCategory.LEGAL,
  detailedDescription = "КПП — код причины постановки на учёт (9 цифр). Первые 4 цифры совпадают с кодом налогового органа из ИНН.",
  example = "516532600",
)