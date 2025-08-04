package ru.person.bizgen.generators.impl

import ru.person.bizgen.generators.Generator
import ru.person.bizgen.generators.GeneratorResult
import ru.person.bizgen.utils.withEscape
import kotlin.random.Random

/**
 * Генератор адреса.
 *
 * @author Dmitry_Emelyanenko
 */
class AddressGenerator : Generator<String> {

  override fun generate(): GeneratorResult<String> {
    val address = AddressGeneratorInner.generateAddress()


    val info = """
      /*
      zipCode = ${address.zipCode.withEscape()}
      region = ${address.region.withEscape()}
      city = ${address.city.withEscape()}
      district = ${address.district.withEscape()}
      street = ${address.street.withEscape()}
      houseNum = ${address.houseNum.withEscape()}
      building = ${address.building.withEscape()}
      floor = ${address.floor.withEscape()}
      apartment = ${address.apartment.withEscape()}
      postOfficeBox = ${address.postOfficeBox.withEscape()}
      fullAddress = ${address.fullAddress?.withEscape()}
      */
    """.trimIndent()

    return GeneratorResult(toClipboard = address.toString(), toEditor = info)
  }

  object AddressGeneratorInner {
    private val regionCityPairs = listOf(
      "Moscow Oblast" to "Moscow",
      "Moscow Oblast" to "Khimki",
      "Moscow Oblast" to "Podolsk",
      "Moscow Oblast" to "Balashikha",
      "Moscow Oblast" to "Korolyov",
      "Moscow Oblast" to "Lyubertsy",
      "Moscow Oblast" to "Mytishchi",
      "Moscow Oblast" to "Elektrostal",
      "Leningrad Oblast" to "Saint Petersburg",
      "Leningrad Oblast" to "Gatchina",
      "Leningrad Oblast" to "Vyborg",
      "Leningrad Oblast" to "Tosno",
      "Leningrad Oblast" to "Kronshtadt",
      "Leningrad Oblast" to "Sestroretsk",
      "Leningrad Oblast" to "Lomonosov",
      "Leningrad Oblast" to "Pushkin",
      "Krasnodar Krai" to "Krasnodar",
      "Krasnodar Krai" to "Sochi",
      "Krasnodar Krai" to "Novorossiysk",
      "Krasnodar Krai" to "Armavir",
      "Krasnodar Krai" to "Gelendzhik",
      "Krasnodar Krai" to "Anapa",
      "Krasnodar Krai" to "Yeysk",
      "Krasnodar Krai" to "Slavyansk-na-Kubani",
      "Sverdlovsk Oblast" to "Yekaterinburg",
      "Sverdlovsk Oblast" to "Nizhny Tagil",
      "Sverdlovsk Oblast" to "Kamensk-Uralsky",
      "Sverdlovsk Oblast" to "Pervouralsk",
      "Sverdlovsk Oblast" to "Alapayevsk",
      "Sverdlovsk Oblast" to "Revda",
      "Sverdlovsk Oblast" to "Asbest",
      "Sverdlovsk Oblast" to "Beryozovsky",
      "Rostov Oblast" to "Rostov-on-Don",
      "Rostov Oblast" to "Taganrog",
      "Rostov Oblast" to "Shakhty",
      "Rostov Oblast" to "Novocherkassk",
      "Rostov Oblast" to "Volgodonsk",
      "Rostov Oblast" to "Bataysk",
      "Rostov Oblast" to "Azov",
      "Rostov Oblast" to "Kamensk-Shakhtinsky",
      "Republic of Tatarstan" to "Kazan",
      "Republic of Tatarstan" to "Naberezhnye Chelny",
      "Republic of Tatarstan" to "Nizhnekamsk",
      "Republic of Tatarstan" to "Almetyevsk",
      "Republic of Tatarstan" to "Zelenodolsk",
      "Republic of Tatarstan" to "Bugulma",
      "Republic of Tatarstan" to "Yelabuga",
      "Republic of Tatarstan" to "Chistopol"
    )

    private val districts = listOf(
      "Central", "Northern", "Southern", "Western", "Eastern",
      "Soviet", "Leninsky", "Oktyabrsky", "Kirovsky", "Zheleznodorozhny",
      "Zarechny", "Primorsky", "Gorny", "Industrial", "Solnechny",
      "Zelyony", "Rechnoy", "Nagorny", "Shkolny", "Parkovy",
      "Severny", "Yuzhny", "Zapadny", "Vostochny", "Tsentralny",
      "Pervomaysky", "Krasnoarmeysky", "Krasnogvardeysky", "Sormovsky", "Avtozavodsky",
      "Kalininsky", "Kirovsky", "Krasnoselsky", "Moskovsky", "Nevsky",
      "Petrogradsky", "Primorsky", "Frunzensky", "Admiralteysky", "Vasileostrovsky"
    )

    private val streets = listOf(
      "Lenina", "Gagarina", "Pushkina", "Tolstogo", "Sovetskaya", "Mira",
      "Kirova", "Lesnaya", "Sadovaya", "Naberezhnaya", "Tsentralnaya", "Molodyozhnaya",
      "Shkolnaya", "Zarechnaya", "Stroiteley", "Komsomolskaya", "Parkovaya",
      "Rechnaya", "Solnechnaya", "Zelenaya", "Novaya", "Svetlaya", "Lesoparkovaya",
      "Yubileynaya", "Pobedy", "Kosmonavtov", "Sportivnaya", "Zavodskaya", "Vokzalnaya",
      "Sadovaya", "Lesnaya", "Sovkhoznaya", "Polevaya", "Ozernaya", "Gornaya",
      "Solnichnaya", "Vesennaya", "Zimnyaya", "Letnyaya", "Osennyaya"
    )

    private val buildingNames = listOf(
      "Building 1", "Building 2", "Building 3", "Building 4",
      "Building 5", "Building 6", "Building 7", "Building 8", "Building 9",
      "Building 10", "Building 11", "Building 12", "Building 13", "Building 14",
      "Building 15", "Building A", "Building B", "Building C", "Building D",
      "Building E", "Building F", "Building G", "Structure 1", "Structure 2",
      "Structure 3", "Structure 4", "Structure 5", "Block A", "Block B",
      "Block C", "Block D", "Wing 1", "Wing 2", "Wing 3",
      "Tower 1", "Tower 2", "Pavilion A", "Pavilion B", "Annex 1"
    )

    internal fun generateAddress(): Address {
      val random = Random.Default
      val (region, city) = regionCityPairs.random()

      return Address(
        zipCode = String.format("%06d", random.nextInt(1000000)),
        region = region,
        city = city,
        district = districts.random(),
        street = streets.random(),
        houseNum = (1 + random.nextInt(200)).toString(),
        building = buildingNames.random(),
        floor = (1 + random.nextInt(20)).toString(),
        apartment = (1 + random.nextInt(300)).toString(),
        postOfficeBox = "P.O. Box ${1 + random.nextInt(500)}",
      ).apply {
        fullAddress = buildString {
          append(zipCode).append(", ")
          append(region).append(", ")
          append(city).append(", ")
          append(district).append(" District, ")
          append(street).append(" St., ")
          append("Bld. ").append(houseNum)
          append(", ").append(building)
          append(", Apt. ").append(apartment)
          append(", ").append(postOfficeBox)
        }
      }
    }
  }

  data class Address(
    var zipCode: String,
    var region: String,
    var city: String,
    var district: String,
    var street: String,
    var houseNum: String,
    var building: String,
    var floor: String,
    var apartment: String,
    var postOfficeBox: String,
    var fullAddress: String? = null,
  )
}