package ru.eda.plgn.bizgen.core.generator_info.impl.ogrn

import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.OgrnLegalGenerator

/**
 * Действие, которое использует [OgrnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnLegalGeneratorInfo : GeneratorStrInfo(
  id = "OgrnLegal_980cb31d-0e3f-4764-bd8e-b7fb9fba6859",
  name = "ОГРН ЮЛ (13)",
  generator = OgrnLegalGenerator()
)