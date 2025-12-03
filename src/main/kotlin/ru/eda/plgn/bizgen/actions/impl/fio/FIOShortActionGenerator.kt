package ru.eda.plgn.bizgen.actions.impl.fio

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.FIOShortGenerator

/**
 * Действие, которое использует [FIOShortGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOShortActionGenerator : BaseGeneratorAction<String>(
  id = "FioShort_3d49bf3a-007d-41e5-89bf-3a007d01e5d6",
  name = "ФИО (Фамилия И.О.)",
  generator = FIOShortGenerator()
)