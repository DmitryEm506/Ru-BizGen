package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.OgrnLegalGenerator

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