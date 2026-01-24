package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.StrActionGenerator
import ru.eda.plgn.bizgen.generators.impl.CountryGenerator

/**
 * Действие, которое использует [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CountryActionGenerator : StrActionGenerator(
  id = "Country_d993c531-359d-476e-a1cb-098cc945ea99",
  name = "Страна (код, название рус+англ, alpha2, alpha3)",
  generator = CountryGenerator()
)