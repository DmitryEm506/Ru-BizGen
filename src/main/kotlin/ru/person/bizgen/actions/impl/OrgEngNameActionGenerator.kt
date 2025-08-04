package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.OrgEngNameGenerator

/**
 * Действие, которое использует [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgEngNameActionGenerator : BaseGeneratorAction<String>(
  id = "OrgEngName_e60c059d-0c7f-49f5-95b9-bcd194b4a327",
  name = "Организация. Английское наименование",
  generator = OrgEngNameGenerator()
)