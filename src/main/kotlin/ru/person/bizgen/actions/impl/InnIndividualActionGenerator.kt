package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.InnIndividualGenerator

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
