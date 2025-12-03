package ru.eda.plgn.bizgen.actions.impl.ogrn

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.OgrnLegalGenerator

/**
 * Действие, которое использует [OgrnLegalGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class OgrnLegalActionGenerator : BaseGeneratorAction<String>(
  id = "OgrnLegal_980cb31d-0e3f-4764-bd8e-b7fb9fba6859",
  name = "ОГРН ЮЛ (13)",
  generator = OgrnLegalGenerator()
)