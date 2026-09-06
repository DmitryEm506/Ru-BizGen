package ru.eda.plgn.bizgen.core.generator_info.impl

import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator.impl.AddressGenerator

/**
 * Действие, которое использует [AddressGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class AddressGeneratorInfo : GeneratorStrInfo(
  id = "Address_b35bf562-5761-4a5d-ab00-36afca192d7c",
  name = "Адрес (индекс, регион, город и тд)",
  generator = AddressGenerator(),
  category = GeneratorCategory.GEO,
  detailedDescription = "Почтовый адрес (индекс, регион, город, район, улица, дом, корпус, квартира, абонентский ящик). Все поля заполняются случайно.",
  example = "125009, Москва, ул. Тверская, д. 7, кв. 15",
)