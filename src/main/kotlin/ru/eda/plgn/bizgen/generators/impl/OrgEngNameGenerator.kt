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
  override val uniqueDistance: Int = 130

  override fun generate(): GeneratorResult<String> =
    GeneratorResultWithEscape(data = generateOrganizationName())

  private object OrganizationEngNameGeneratorInner {
    private val companyTypes = listOf(
      "LLC",     // Limited Liability Company
      "Ltd.",    // Limited
      "Inc.",    // Incorporated
      "Corp.",   // Corporation
      "PLC",     // Public Limited Company
      "LP",      // Limited Partnership
      "LLP",     // Limited Liability Partnership
      "SP",      // Sole Proprietorship
      "Foundation",
      "Association",
      "Trust"
    )

    private val firstWords = listOf(
      "Global", "Tech", "Industrial", "Advanced", "United", "National", "Prime",
      "Alpha", "Omega", "Delta", "Sigma", "Vector", "Quantum",
      "North", "South", "East", "West", "Central",
      "Atlantic", "Pacific", "Arctic", "Continental",
      "Digital", "Cyber", "Nano", "Bio", "Smart", "Cloud",
      "Energy", "Power", "Dynamics", "Systems", "Solutions",
      "Capital", "Finance", "Investment", "Assets", "Equity",
      "Logistics", "Transport", "Aero", "Marine", "Transit",
      "Medical", "Pharma", "Health", "Clinical", "Diagnostics",
      "Agro", "Food", "Grain", "Harvest", "Farming",
      "Construction", "Build", "Engineering", "Infrastructure",
      "Research", "Innovation", "Labs", "Institute", "Academy",
      "Media", "Creative", "Studio", "Art", "Culture",
      "Spectrum", "Horizon", "Impulse", "Matrix", "Platform",
      "Ecosystem", "Synergy", "Initiative", "Momentum",
      "Vertex", "Polaris", "Apex", "Summit", "Pioneer",
      "Logic", "Algorithm", "Protocol", "Interface", "Architecture",
      "Prime", "Focus", "Status", "Formula", "Axiom"
    )

    private val secondWords = listOf(
      "Group", "Holdings", "Corporation", "Company", "Enterprises",
      "Industries", "Systems", "Technologies", "Solutions",
      "Consulting", "Management", "Development",
      "Partners", "Alliance", "Union", "Consortium",
      "Capital", "Investments", "Ventures",
      "Logistics", "Operations", "Services", "Support",
      "Engineering", "Manufacturing", "Production",
      "Research", "Analytics", "Integration",
      "Automation", "Optimization", "Modernization",
      "Platform", "Ecosystem", "Network",
      "Digital", "Online", "Media", "Labs",
      "Premium", "Quality", "Expertise"
    )

    private val longSuffixes = listOf(
      " operating in international markets",
      " specializing in advanced technological solutions",
      " providing integrated services and products",
      " focused on innovation and sustainable development",
      " serving government and corporate clients",
      " with a global network of subsidiaries",
      " established in accordance with applicable law",
      " delivering end-to-end digital transformation",
      " engaged in research and development activities",
      " with headquarters and regional offices worldwide",
      " operating under international quality standards",
      " with a diversified portfolio of solutions",
      " supporting large-scale infrastructure projects",
      " acting in the interests of strategic partners",
      " with certified processes and methodologies"
    )

    private val separators = listOf("", "-", " ")

    fun generateOrganizationName(): String {
      val random = Random.Default
      val type = companyTypes.random()

      val baseName = buildString {
        append(firstWords.random())
        append(separators.random())
        append(secondWords.random())
      }

      // 50% chance to add long suffix
      val fullName = if (random.nextDouble() < 0.5) {
        val suffix = longSuffixes.random()
        if (type.length + 1 + baseName.length + suffix.length <= 255) {
          "$baseName$suffix $type"
        } else {
          val maxSuffixLength = 255 - (type.length + 1 + baseName.length)
          "$baseName${suffix.take(maxSuffixLength)} $type"
        }
      } else {
        "$baseName $type"
      }

      return fullName
    }
  }
}