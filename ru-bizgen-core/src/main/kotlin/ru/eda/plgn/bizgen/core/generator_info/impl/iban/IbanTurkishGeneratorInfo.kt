package ru.eda.plgn.bizgen.core.generator_info.impl.iban

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.IbanTurkishGenerator

/**
 * Действие, которое использует [IbanTurkishGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanTurkishGeneratorInfo : GeneratorStrInfo(
  id = "IbanTurkish_cdb0d09c-2263-407b-b0d0-9c2263007b43",
  name = "IBAN TR (26)",
  generator = IbanTurkishGenerator()
)