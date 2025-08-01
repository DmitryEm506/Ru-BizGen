package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.KppGenerator

/**
 * Действие, которое использует [KppGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class KppActionGenerator : BaseGeneratorAction<String>(
  id = "Kpp_d04771b5-6e0c-42dc-8771-b56e0ca2dcb9",
  name = "КПП (9)",
  generator = KppGenerator()
)