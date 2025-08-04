package ru.person.bizgen.actions.impl.fio

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.FIOFullGenerator

/**
 * Действие, которое использует [FIOFullGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOFullActionGenerator : BaseGeneratorAction<String>(
  id = "FioFull_497328f4-0f2b-4829-b328-f40f2b2829d0",
  name = "ФИО (Фамилия Имя Отчество)",
  generator = FIOFullGenerator()
)