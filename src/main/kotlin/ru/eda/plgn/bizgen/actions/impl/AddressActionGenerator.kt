package ru.eda.plgn.bizgen.actions.impl

import ru.eda.plgn.bizgen.actions.BaseGeneratorAction
import ru.eda.plgn.bizgen.generators.impl.AddressGenerator

/**
 * Действие, которое использует [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AddressActionGenerator : BaseGeneratorAction<String>(
  id = "Address_b35bf562-5761-4a5d-ab00-36afca192d7c",
  name = "Адрес (индекс, регион, город и тд)",
  generator = AddressGenerator()
)