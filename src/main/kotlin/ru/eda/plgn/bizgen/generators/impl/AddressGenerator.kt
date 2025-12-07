package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.utils.withEscape
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
      "MOSCOW OBLAST" to "MOSCOW",
      "MOSCOW OBLAST" to "KHIMKI",
      "MOSCOW OBLAST" to "PODOLSK",
      "MOSCOW OBLAST" to "BALASHIKHA",
      "MOSCOW OBLAST" to "KOROLYOV",
      "MOSCOW OBLAST" to "LYUBERTSY",
      "MOSCOW OBLAST" to "MYTISHCHI",
      "MOSCOW OBLAST" to "ELEKTROSTAL",
      "LENINGRAD OBLAST" to "SAINT PETERSBURG",
      "LENINGRAD OBLAST" to "GATCHINA",
      "LENINGRAD OBLAST" to "VYBORG",
      "LENINGRAD OBLAST" to "TOSNO",
      "LENINGRAD OBLAST" to "KRONSHTADT",
      "LENINGRAD OBLAST" to "SESTRORETSK",
      "LENINGRAD OBLAST" to "LOMONOSOV",
      "LENINGRAD OBLAST" to "PUSHKIN",
      "KRASNODAR KRAI" to "KRASNODAR",
      "KRASNODAR KRAI" to "SOCHI",
      "KRASNODAR KRAI" to "NOVOROSSIYSK",
      "KRASNODAR KRAI" to "ARMAVIR",
      "KRASNODAR KRAI" to "GELENDZHIK",
      "KRASNODAR KRAI" to "ANAPA",
      "KRASNODAR KRAI" to "YEYSK",
      "KRASNODAR KRAI" to "SLAVYANSK-NA-KUBANI",
      "SVERDLOVSK OBLAST" to "YEKATERINBURG",
      "SVERDLOVSK OBLAST" to "NIZHNY TAGIL",
      "SVERDLOVSK OBLAST" to "KAMENSK-URALSKY",
      "SVERDLOVSK OBLAST" to "PERVOURALSK",
      "SVERDLOVSK OBLAST" to "ALAPAYEVSK",
      "SVERDLOVSK OBLAST" to "REVDA",
      "SVERDLOVSK OBLAST" to "ASBEST",
      "SVERDLOVSK OBLAST" to "BERYOZOVSKY",
      "ROSTOV OBLAST" to "ROSTOV-ON-DON",
      "ROSTOV OBLAST" to "TAGANROG",
      "ROSTOV OBLAST" to "SHAKHTY",
      "ROSTOV OBLAST" to "NOVOCHERKASSK",
      "ROSTOV OBLAST" to "VOLGODONSK",
      "ROSTOV OBLAST" to "BATAYSK",
      "ROSTOV OBLAST" to "AZOV",
      "ROSTOV OBLAST" to "KAMENSK-SHAKHTINSKY",
      "REPUBLIC OF TATARSTAN" to "KAZAN",
      "REPUBLIC OF TATARSTAN" to "NABEREZHNYE CHELNY",
      "REPUBLIC OF TATARSTAN" to "NIZHNEKAMSK",
      "REPUBLIC OF TATARSTAN" to "ALMETYEVSK",
      "REPUBLIC OF TATARSTAN" to "ZELENODOLSK",
      "REPUBLIC OF TATARSTAN" to "BUGULMA",
      "REPUBLIC OF TATARSTAN" to "YELABUGA",
      "REPUBLIC OF TATARSTAN" to "CHISTOPOL"
    )

    private val districts = listOf(
      "CENTRAL", "NORTHERN", "SOUTHERN", "WESTERN", "EASTERN",
      "SOVIET", "LENINSKY", "OKTYABRSKY", "KIROVSKY", "ZHELEZNODOROZHNY",
      "ZARECHNY", "PRIMORSKY", "GORNY", "INDUSTRIAL", "SOLNECHNY",
      "ZELYONY", "RECHNOY", "NAGORNY", "SHKOLNY", "PARKOVY",
      "SEVERNY", "YUZHNY", "ZAPADNY", "VOSTOCHNY", "TSENTRALNY",
      "PERVOMAYSKY", "KRASNOARMEYSKY", "KRASNOGVARDEYSKY", "SORMOVSKY", "AVTOZAVODSKY",
      "KALININSKY", "KIROVSKY", "KRASNOSELSKY", "MOSKOVSKY", "NEVSKY",
      "PETROGRADSKY", "PRIMORSKY", "FRUNZENSKY", "ADMIRALTEYSKY", "VASILEOSTROVSKY"
    )

    private val streets = listOf(
      "LENINA", "GAGARINA", "PUSHKINA", "TOLSTOGO", "SOVETSKAYA", "MIRA",
      "KIROVA", "LESNAYA", "SADOVAYA", "NABEREZHNAYA", "TSENTRALNAYA", "MOLODYOZHNAYA",
      "SHKOLNAYA", "ZARECHNAYA", "STROITELEY", "KOMSOMOLSKAYA", "PARKOVAYA",
      "RECHNAYA", "SOLNECHNAYA", "ZELENAYA", "NOVAYA", "SVETLAYA", "LESOPARKOVAYA",
      "YUBILEYNAYA", "POBEDY", "KOSMONAVTOV", "SPORTIVNAYA", "ZAVODSKAYA", "VOKZALNAYA",
      "SADOVAYA", "LESNAYA", "SOVKHOZNAYA", "POLEVAYA", "OZERNAYA", "GORNAYA",
      "SOLNICHNAYA", "VESENNAYA", "ZIMNYAYA", "LETNYAYA", "OSENNYAYA"
    )

    private val buildingNames = listOf(
      "BUILDING 1", "BUILDING 2", "BUILDING 3", "BUILDING 4",
      "BUILDING 5", "BUILDING 6", "BUILDING 7", "BUILDING 8", "BUILDING 9",
      "BUILDING 10", "BUILDING 11", "BUILDING 12", "BUILDING 13", "BUILDING 14",
      "BUILDING 15", "BUILDING A", "BUILDING B", "BUILDING C", "BUILDING D",
      "BUILDING E", "BUILDING F", "BUILDING G", "STRUCTURE 1", "STRUCTURE 2",
      "STRUCTURE 3", "STRUCTURE 4", "STRUCTURE 5", "BLOCK A", "BLOCK B",
      "BLOCK C", "BLOCK D", "WING 1", "WING 2", "WING 3",
      "TOWER 1", "TOWER 2", "PAVILION A", "PAVILION B", "ANNEX 1"
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
        postOfficeBox = "P.O. BOX ${1 + random.nextInt(500)}",
      ).apply {
        fullAddress = buildString {
          append(zipCode).append(", ")
          append(region).append(", ")
          append(city).append(", ")
          append(district).append(" DISTRICT, ")
          append(street).append(" ST., ")
          append("BLD. ").append(houseNum)
          append(", ").append(building)
          append(", APT. ").append(apartment)
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