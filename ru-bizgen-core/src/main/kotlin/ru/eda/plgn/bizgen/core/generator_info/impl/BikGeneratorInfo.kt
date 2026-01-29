package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.BikGenerator

/**
 * Действие, которое использует [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BikGeneratorInfo: GeneratorStrInfo(
  id = "Bik_0060ce01-2ee3-4598-a0ce-012ee38598da",
  name = "БИК (9)",
  generator = BikGenerator()
)