package ru.eda.plgn.bizgen.core.generator_info.impl.org

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.OrgEngNameGenerator

/**
 * Действие, которое использует [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OrgEngNameGeneratorInfo : GeneratorStrInfo(
  id = "OrgEngName_e60c059d-0c7f-49f5-95b9-bcd194b4a327",
  name = "Организация. Английское наименование",
  generator = OrgEngNameGenerator()
)