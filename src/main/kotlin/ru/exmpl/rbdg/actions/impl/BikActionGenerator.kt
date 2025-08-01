package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.BikGenerator

/**
 * Действие, которое использует [BikGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class BikActionGenerator: BaseGeneratorAction<String>(
  id = "Bik_0060ce01-2ee3-4598-a0ce-012ee38598da",
  name = "БИК (9)",
  generator = BikGenerator()
)