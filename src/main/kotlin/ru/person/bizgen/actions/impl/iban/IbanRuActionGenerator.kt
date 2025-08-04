package ru.person.bizgen.actions.impl.iban

import ru.person.bizgen.actions.BaseGeneratorAction
import ru.person.bizgen.generators.impl.IbanRuGenerator

/**
 * Действие, которое использует [IbanRuGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class IbanRuActionGenerator : BaseGeneratorAction<String>(
  id = "IbanRu_13cbe98c-fc00-4a93-8be9-8cfc00ca93d2",
  name = "IBAN RU (33)",
  generator = IbanRuGenerator()
)