package ru.person.bizgen.actions.impl.org

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.OrgRuNameGenerator

/**
 * Действие, которое использует [OrgRuNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgRuNameActionGenerator : BaseGeneratorAction<String>(
  id = "OrgRuName_be05a746-5353-434b-a506-b2541be02550",
  name = "Организация. Русское наименование",
  generator = OrgRuNameGenerator()
)