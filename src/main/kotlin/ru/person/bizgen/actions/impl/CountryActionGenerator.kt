package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.CountryGenerator

/**
 * Действие, которое использует [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CountryActionGenerator : BaseGeneratorAction<String>(
  id = "Country_d993c531-359d-476e-a1cb-098cc945ea99",
  name = "Страна (код, название рус+англ, alpha2, alpha3)",
  generator = CountryGenerator()
)