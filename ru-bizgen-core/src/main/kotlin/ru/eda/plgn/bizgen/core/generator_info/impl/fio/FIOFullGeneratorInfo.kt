package ru.eda.plgn.bizgen.core.generator_info.impl.fio

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.FIOFullGenerator

/**
 * Действие, которое использует [FIOFullGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOFullGeneratorInfo : GeneratorStrInfo(
  id = "FioFull_497328f4-0f2b-4829-b328-f40f2b2829d0",
  name = "ФИО (Фамилия Имя Отчество)",
  generator = FIOFullGenerator()
)