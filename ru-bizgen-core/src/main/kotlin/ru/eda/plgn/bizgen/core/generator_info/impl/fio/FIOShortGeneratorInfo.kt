package ru.eda.plgn.bizgen.core.generator_info.impl.fio

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.FIOShortGenerator

/**
 * Действие, которое использует [FIOShortGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOShortGeneratorInfo : GeneratorStrInfo(
  id = "FioShort_3d49bf3a-007d-41e5-89bf-3a007d01e5d6",
  name = "ФИО (Фамилия И.О.)",
  generator = FIOShortGenerator()
)