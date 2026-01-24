package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.BikGenerator

/**
 * Действие, которое использует [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BikActionGenerator: StrActionGenerator(
  id = "Bik_0060ce01-2ee3-4598-a0ce-012ee38598da",
  name = "БИК (9)",
  generator = BikGenerator()
)