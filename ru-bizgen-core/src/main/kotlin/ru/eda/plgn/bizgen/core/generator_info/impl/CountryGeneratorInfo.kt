package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.CountryGenerator

/**
 * Действие, которое использует [CountryGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class CountryGeneratorInfo : GeneratorStrInfo(
  id = "Country_d993c531-359d-476e-a1cb-098cc945ea99",
  name = "Страна (код, название рус+англ, alpha2, alpha3)",
  generator = CountryGenerator()
)