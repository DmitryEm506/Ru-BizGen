package ru.eda.plgn.bizgen.core.generator_info.impl.inn

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.InnIndividualGenerator

/**
 * Действие, которое использует [InnIndividualGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class InnIndividualGeneratorInfo : GeneratorStrInfo(
  id = "InnIndividual_3aeaffc7-285e-49c2-aaff-c7285e79c299",
  name = "ИНН ФЛ (12)",
  generator = InnIndividualGenerator()
)
