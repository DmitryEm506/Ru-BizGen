package ru.eda.plgn.bizgen.core.generator_info.impl.fio

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.FIOInitialsGenerator

/**
 * Действие, которое использует [FIOInitialsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOInitialsGeneratorInfo : GeneratorStrInfo(
  id = "FioInitials_a7e234d4-f204-4a16-a234-d4f204fa16b9",
  name = "ФИО (И.О. Фамилия)",
  generator = FIOInitialsGenerator()
)