package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.OgrnIpGenerator

/**
 * Действие, которое использует [OgrnIpGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnIpActionGenerator : BaseGeneratorAction<String>(
  id = "OgrnIp_c532c151-6187-4e7d-b2c1-5161877e7d9d",
  name = "ОГРН ИП (15)",
  generator = OgrnIpGenerator()
)