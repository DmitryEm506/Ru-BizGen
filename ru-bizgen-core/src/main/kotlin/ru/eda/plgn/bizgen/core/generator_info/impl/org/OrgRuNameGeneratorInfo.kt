package ru.eda.plgn.bizgen.core.generator_info.impl.org

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.OrgRuNameGenerator

/**
 * Действие, которое использует [OrgRuNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgRuNameGeneratorInfo : GeneratorStrInfo(
  id = "OrgRuName_be05a746-5353-434b-a506-b2541be02550",
  name = "Организация. Русское наименование",
  generator = OrgRuNameGenerator()
)