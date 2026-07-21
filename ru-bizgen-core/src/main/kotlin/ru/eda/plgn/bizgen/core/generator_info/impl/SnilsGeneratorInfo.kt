package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.SnilsGenerator

/**
 * Действие, которое использует [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class SnilsGeneratorInfo : GeneratorStrInfo(
  id = "Snils_f113955e-a439-4231-9f0f-fd13a6d23459",
  name = "СНИЛС (11)",
  generator = SnilsGenerator(),
  category = GeneratorCategory.PERSONAL,
  detailedDescription = "СНИЛС (11 символов в формате NNN-NNN-NNN NN). Контрольное число (последние 2 цифры) рассчитывается по алгоритму страхового номера.",
  example = "337-212-737 58",
)