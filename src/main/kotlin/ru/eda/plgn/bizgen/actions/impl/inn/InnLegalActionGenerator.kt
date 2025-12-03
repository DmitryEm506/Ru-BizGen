package ru.eda.plgn.bizgen.actions.impl.inn

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.InnLegalGenerator

/**
 * Действие, которое использует [InnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnLegalActionGenerator : BaseGeneratorAction<String>(
  id = "InnLegal_f5e5e2b3-2d14-4e83-a5e2-b32d140e831a",
  name = "ИНН ЮЛ (10)",
  generator = InnLegalGenerator()
)
