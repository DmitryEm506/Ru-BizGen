package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.SnilsGenerator

/**
 * Действие, которое использует [SnilsGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class SnilsActionGenerator : StrActionGenerator(
  id = "Snils_f113955e-a439-4231-9f0f-fd13a6d23459",
  name = "СНИЛС (11)",
  generator = SnilsGenerator()
)