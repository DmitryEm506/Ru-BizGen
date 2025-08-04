package ru.person.bizgen.actions.impl.fio

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.FIOShortGenerator

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