package ru.eda.plgn.bizgen.actions.impl.inn

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.InnIndividualGenerator

/**
 * Действие, которое использует [InnIndividualGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnIndividualActionGenerator : BaseGeneratorAction<String>(
  id = "InnIndividual_3aeaffc7-285e-49c2-aaff-c7285e79c299",
  name = "ИНН ФЛ (12)",
  generator = InnIndividualGenerator()
)
