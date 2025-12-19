package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.generators.impl.OrgEngNameGenerator.OrganizationEngNameGeneratorInner.generateOrganizationName
import kotlin.random.Random

/**
 * Генератор наименований организаций на английском языке.
 *
 * @author Dmitry_Emelyanenko
 */
class OrgEngNameGenerator : Generator<String> {
  override val uniqueDistance: Int = 25

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateOrganizationName())

  private object OrganizationEngNameGeneratorInner {
    private val companyTypes = listOf("LLC", "SP", "PJSC", "FSE") // LLC (ООО), SP (ИП), PJSC (АО), FSE (ФКП)

    private val firstWords = listOf(
// Core industrial and technological
      "Global", "Techno", "Rus", "Metal", "Build", "Trans", "Oil", "Gas",
      "Agro", "Med", "Pharm", "Telecom", "Invest", "Finance", "Energy", "Progress",
      "Mega", "Cosmos", "Alpha", "Omega", "Delta", "Sigma", "Vector", "Quantum",
      "Sib", "Far", "Ural", "North", "South", "East", "West", "Center",
      "Science", "Tech", "Industry", "Chem", "Eco", "Bio", "Nano", "Cyber",

      // Geographical and regional
      "Moscow", "Petersburg", "Siberia", "Baikal", "Volga", "Don", "Amur", "Altai",
      "Kamchatka", "Sakhalin", "Crimea", "Caucasus", "Chernozem", "Neva", "Ob", "Yenisei",
      "Lena", "Angara", "Baltic", "Arctic", "Taimyr", "Primorye", "Transbaikal", "Kursk",
      "Tver", "Ryazan", "Vladimir", "Yaroslavl", "Kostroma", "Vologda", "Pskov", "Novgorod",

      // Technology and innovation
      "Robot", "IT", "Code", "Soft", "Hard", "Chip", "Drive", "Smart",
      "Digital", "Virtu", "Cloud", "Neuro", "Crypto", "Blockchain", "Token", "IP",
      "Sensor", "Detector", "Micro", "Opto", "Laser", "Plasma", "Quantum", "Photon",

      // Construction and real estate
      "Dom", "Quarter", "Zhil", "Comfort", "Facade", "Foundation", "Roof", "Concrete",
      "Brick", "Glass", "Metro", "Bridge", "Tunnel", "Highrise", "Arch", "Project",

      // Transport and logistics
      "Avia", "Auto", "Railway", "RiverFleet", "SeaFleet", "Transit", "Express", "Cargo",
      "Freight", "Container", "Logist", "Transa", "Aero", "Taxi", "Bus", "Trolley",

      // Finance and business
      "Capital", "Asset", "Trust", "Credit", "Deposit", "Leasing", "Factoring", "Audit",
      "Consult", "Market", "Business", "Trade", "Impex", "Expo", "Forum", "Club",

      // Science and education
      "Academy", "Univer", "College", "Lyceum", "Institute", "Laboratory", "Research", "Experiment",
      "Knowledge", "Progress", "Intellect", "Genius", "Talent", "Olympus", "Erudite", "Course",

      // Medicine and pharmaceuticals
      "Medica", "Clinic", "Diagnost", "Therapy", "Dental", "Ophthalmo", "Cardio", "Neuro",
      "Pharma", "Vita", "Genetic", "Immuno", "Bio", "Hygiene", "Sanatorium", "Spa",

      // Retail and services
      "Trade", "Market", "Super", "Hyper", "Mega", "Supermarket", "Grocery", "Product",
      "Service", "Center", "Plus", "Extra", "Premium", "Lux", "Grand", "Elite",

      // Natural resources and ecology
      "Forest", "Water", "Air", "Earth", "Fertility", "Eco", "Resource", "Geo",
      "Petro", "Gas", "Coal", "Ore", "Metallurgy", "Chem", "Polymer", "Energy",

      // Culture and arts
      "Art", "Culture", "Theater", "Cinema", "Museum", "Gallery", "Exhibition", "Festival",
      "Concert", "Philharmonic", "Orchestra", "Choir", "Ballet", "Opera", "Drama", "Circus"
    )

    private val secondWords = listOf(
      "Service", "Group", "Holding", "Trade", "Leasing", "Bank", "Capital", "Resource",
      "Supply", "Retail", "Industry", "Tech", "Industry", "Systems", "Technologies", "Solutions",
      "Consulting", "Management", "Development", "Partners", "Center", "Alliance", "Trust",
      "Corporation", "Company", "Concern", "Syndicate", "Trust", "Association", "Fund",
      "Venture", "Digital", "Innovations", "Logistics", "Market", "Realty", "Oil",
      "Project", "Build", "Transit", "Universal", "Factory", "HiTech", "Center", "Expert",
      "Union", "Yard", "Lab", "Media", "Network", "Online", "Premium", "Quality"
    )

    private val longSuffixes = listOf(
      " named after Academician Petrov-Sokolov",
      " of the Northwestern Federal District",
      " for high-tech equipment production",
      " for oil and gas industry and energy",
      " in information technology and digital transformation",
      " of Krasnoznamensk urban district, Moscow Region",
      " under the Government of the Russian Federation",
      " for innovative AI solutions development",
      " and subsidiaries of Vostok-Zapad group of companies",
      " (former Progress-Electronmash enterprise)"
    )

    fun generateOrganizationName(): String {
      val random = Random.Default
      val type = companyTypes.random()
      val useTwoWords = random.nextBoolean()

      val baseName = buildString {
        append(firstWords.random())
        if (useTwoWords) {
          append(secondWords.random())
        }
      }

      // 30% chance to add long suffix
      val fullName = if (random.nextDouble() < 0.3) {
        val suffix = longSuffixes.random()
        // Ensure total length doesn't exceed 255 chars
        if (type.length + 1 + baseName.length + suffix.length <= 255) {
          "$type $baseName$suffix"
        } else {
          // If too long, truncate suffix
          val maxSuffixLength = 255 - (type.length + 1 + baseName.length)
          "$type $baseName${suffix.take(maxSuffixLength)}"
        }
      } else {
        "$type $baseName"
      }

      return fullName
    }
  }
}