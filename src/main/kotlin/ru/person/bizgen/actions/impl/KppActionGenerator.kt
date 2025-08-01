package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.KppGenerator

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