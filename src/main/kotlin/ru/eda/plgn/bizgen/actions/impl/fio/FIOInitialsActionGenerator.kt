package ru.eda.plgn.bizgen.actions.impl.fio

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.FIOInitialsGenerator

/**
 * Действие, которое использует [FIOInitialsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class FIOInitialsActionGenerator : BaseGeneratorAction<String>(
  id = "FioInitials_a7e234d4-f204-4a16-a234-d4f204fa16b9",
  name = "ФИО (И.О. Фамилия)",
  generator = FIOInitialsGenerator()
)