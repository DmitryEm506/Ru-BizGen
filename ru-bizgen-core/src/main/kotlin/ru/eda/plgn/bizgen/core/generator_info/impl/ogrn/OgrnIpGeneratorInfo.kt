package ru.eda.plgn.bizgen.core.generator_info.impl.ogrn

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.OgrnIpGenerator

/**
 * Действие, которое использует [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnIpGeneratorInfo : GeneratorStrInfo(
  id = "OgrnIp_c532c151-6187-4e7d-b2c1-5161877e7d9d",
  name = "ОГРН ИП (15)",
  generator = OgrnIpGenerator(),
  category = GeneratorCategory.LEGAL,
  detailedDescription = "ОГРН индивидуального предпринимателя (15 цифр). Контрольная цифра (15-я) — остаток от деления первых 14 цифр на 13.",
  example = "361397062610394",
)