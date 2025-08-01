package ru.person.bizgen.actions.impl

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.IbanTurkishGenerator

/**
 * Действие, которое использует [IbanTurkishGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanTurkishActionGenerator : BaseGeneratorAction<String>(
  id = "IbanTurkish_cdb0d09c-2263-407b-b0d0-9c2263007b43",
  name = "IBAN TR (26)",
  generator = IbanTurkishGenerator()
)