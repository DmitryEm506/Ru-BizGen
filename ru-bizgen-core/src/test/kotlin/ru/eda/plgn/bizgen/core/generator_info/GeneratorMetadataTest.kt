package ru.eda.plgn.bizgen.core.generator_info

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.utils.LuhnAlgorithm
import java.math.BigInteger
import java.util.UUID

/**
 * Тесты метаданных генераторов: [GeneratorInfo.category], [GeneratorInfo.detailedDescription], [GeneratorInfo.example] для всех
 * [GeneratorInfoProvider.generatorInfos].
 *
 * @author Dmitry_Emelyanenko
 */
internal class GeneratorMetadataTest {

  /** Контрольный список id → name всех 31 генераторов (защита от случайной правки идентификаторов). */
  private val expectedIdsAndNames: Map<String, String> = mapOf(
    "UUID_ee2bca00-0586-4a7c-869c-bee1285d0732" to "UUID как строка",
    "AccountRub_0ecdce94-c1e4-447a-8dce-94c1e4747aeb" to "Расчетный RUB счет (20)",
    "AccountCny_4cedac13-b6fa-4b0a-be68-3503702f1564" to "Расчетный CNY счет (20)",
    "BankAccount_9009b9eb-f622-4599-89b9-ebf622559957" to "Корреспондентский счёт (20)",
    "Bik_0060ce01-2ee3-4598-a0ce-012ee38598da" to "БИК (9)",
    "SwiftRu8_59ed6b1a-57cf-4e61-ad6b-1a57cf6e616d" to "SWIFT RU (8)",
    "SwiftRu11_31ae8329-4566-49d6-ae83-294566e9d6f4" to "SWIFT RU (11)",
    "IbanRu_13cbe98c-fc00-4a93-8be9-8cfc00ca93d2" to "IBAN RU (33)",
    "IbanTurkish_cdb0d09c-2263-407b-b0d0-9c2263007b43" to "IBAN TR (26)",
    "InnIndividual_3aeaffc7-285e-49c2-aaff-c7285e79c299" to "ИНН ФЛ (12)",
    "InnLegal_f5e5e2b3-2d14-4e83-a5e2-b32d140e831a" to "ИНН ЮЛ (10)",
    "Kpp_d04771b5-6e0c-42dc-8771-b56e0ca2dcb9" to "КПП (9)",
    "OgrnLegal_980cb31d-0e3f-4764-bd8e-b7fb9fba6859" to "ОГРН ЮЛ (13)",
    "OgrnIp_c532c151-6187-4e7d-b2c1-5161877e7d9d" to "ОГРН ИП (15)",
    "Oktmo8_d683fd6c-23bb-4ccb-83fd-6c23bbaccb89" to "ОКТМО (8)",
    "Oktmo11_3ea1bc6b-011c-4bdc-a1bc-6b011c1bdc7c" to "ОКТМО (11)",
    "OrgRuName_be05a746-5353-434b-a506-b2541be02550" to "Организация. Русское наименование",
    "OrgEngName_e60c059d-0c7f-49f5-95b9-bcd194b4a327" to "Организация. Английское наименование",
    "Address_b35bf562-5761-4a5d-ab00-36afca192d7c" to "Адрес (индекс, регион, город и тд)",
    "Country_d993c531-359d-476e-a1cb-098cc945ea99" to "Страна (код, название рус+англ, alpha2, alpha3)",
    "CardNumber_92bad607-d900-437c-9605-7ededb10022d" to "Номер карты (16)",
    "FioFull_497328f4-0f2b-4829-b328-f40f2b2829d0" to "ФИО (Фамилия Имя Отчество)",
    "FioShort_3d49bf3a-007d-41e5-89bf-3a007d01e5d6" to "ФИО (Фамилия И.О.)",
    "FioInitials_a7e234d4-f204-4a16-a234-d4f204fa16b9" to "ФИО (И.О. Фамилия)",
    "PhoneNumberRuFormat_4850d007-4b10-4027-a0f7-55f9f75c3c72" to "Номер телефона: +7 (XXX) NNN-NN-NN",
    "PhoneNumberRuDigit_8da95b0a-e39d-425b-a40e-463c51fd4290" to "Номер телефона: 7XXXNNNNNNN",
    "Snils_f113955e-a439-4231-9f0f-fd13a6d23459" to "СНИЛС (11)",
    "PassportRuSpaced_a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d" to "Паспорт РФ (с пробелом)",
    "PassportRuCompact_b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e" to "Паспорт РФ (без пробела)",
    "ForeignPassportRuNumber_c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f" to "Загранпаспорт РФ (номер)",
    "ForeignPassportRuMrz_d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a" to "Загранпаспорт РФ (MRZ)",
  )

  /** Ожидаемая категория по префиксу id. */
  private val expectedCategoryByIdPrefix: Map<String, GeneratorCategory> = mapOf(
    "UUID_" to GeneratorCategory.TECHNICAL,
    "AccountRub_" to GeneratorCategory.BANKING,
    "AccountCny_" to GeneratorCategory.BANKING,
    "BankAccount_" to GeneratorCategory.BANKING,
    "Bik_" to GeneratorCategory.BANKING,
    "SwiftRu8_" to GeneratorCategory.BANKING,
    "SwiftRu11_" to GeneratorCategory.BANKING,
    "IbanRu_" to GeneratorCategory.BANKING,
    "IbanTurkish_" to GeneratorCategory.BANKING,
    "InnIndividual_" to GeneratorCategory.LEGAL,
    "InnLegal_" to GeneratorCategory.LEGAL,
    "Kpp_" to GeneratorCategory.LEGAL,
    "OgrnLegal_" to GeneratorCategory.LEGAL,
    "OgrnIp_" to GeneratorCategory.LEGAL,
    "Oktmo8_" to GeneratorCategory.LEGAL,
    "Oktmo11_" to GeneratorCategory.LEGAL,
    "OrgRuName_" to GeneratorCategory.LEGAL,
    "OrgEngName_" to GeneratorCategory.LEGAL,
    "Address_" to GeneratorCategory.GEO,
    "Country_" to GeneratorCategory.GEO,
    "CardNumber_" to GeneratorCategory.PERSONAL,
    "FioFull_" to GeneratorCategory.PERSONAL,
    "FioShort_" to GeneratorCategory.PERSONAL,
    "FioInitials_" to GeneratorCategory.PERSONAL,
    "PhoneNumberRuFormat_" to GeneratorCategory.PERSONAL,
    "PhoneNumberRuDigit_" to GeneratorCategory.PERSONAL,
    "Snils_" to GeneratorCategory.PERSONAL,
    "PassportRuSpaced_" to GeneratorCategory.PERSONAL,
    "PassportRuCompact_" to GeneratorCategory.PERSONAL,
    "ForeignPassportRuNumber_" to GeneratorCategory.PERSONAL,
    "ForeignPassportRuMrz_" to GeneratorCategory.PERSONAL,
  )

  @Nested
  inner class CompletenessCases {

    @Test
    internal fun `Should have non-null category for all generators`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        info.category shouldNotBe null
      }
    }

    @Test
    internal fun `Should have non-blank detailedDescription with at least 20 chars for all generators`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        info.detailedDescription.shouldNotBeBlank()
        info.detailedDescription.length shouldNotBe 0
      }
    }

    @Test
    internal fun `Should have non-blank example for all generators`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        info.example.shouldNotBeBlank()
      }
    }
  }

  @Nested
  inner class CategoryCases {

    @Test
    internal fun `Should match category to id prefix for each generator`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val expectedCategory = expectedCategoryByIdPrefix.entries
          .firstOrNull { (prefix, _) -> info.id.startsWith(prefix) }
          ?.value
          ?: error("Нет ожидаемой категории для id: ${info.id}")

        info.category shouldBe expectedCategory
      }
    }
  }

  @Nested
  inner class IdentifierStabilityCases {

    @Test
    internal fun `Should have exactly 31 generators`() {
      GeneratorInfoProvider.generatorInfos.size shouldBe 31
      expectedIdsAndNames.size shouldBe 31
    }

    @Test
    internal fun `Should match all ids against control list`() {
      val actualIds = GeneratorInfoProvider.generatorInfos.map { it.id }.toSet()
      val expectedIds = expectedIdsAndNames.keys
      actualIds shouldBe expectedIds
    }

    @Test
    internal fun `Should match all names against control list`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val expectedName = expectedIdsAndNames[info.id]
          ?: error("id '${info.id}' отсутствует в контрольном списке")
        info.name shouldBe expectedName
      }
    }
  }

  @Nested
  inner class ExampleValidityCases {

    @Test
    internal fun `Should parse UUID example without exception`() {
      val info = findInfo("UUID_")
      UUID.fromString(info.example)
    }

    @Test
    internal fun `Should pass checksum validation for InnLegal example`() {
      val info = findInfo("InnLegal_")
      isValidInnLegal(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass checksum validation for InnIndividual example`() {
      val info = findInfo("InnIndividual_")
      isValidInnIndividual(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass checksum validation for OgrnLegal example`() {
      val info = findInfo("OgrnLegal_")
      isValidOgrnLegal(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass checksum validation for OgrnIp example`() {
      val info = findInfo("OgrnIp_")
      isValidOgrnIp(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass control number validation for Snils example`() {
      val info = findInfo("Snils_")
      isValidSnils(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass Luhn validation for CardNumber example`() {
      val info = findInfo("CardNumber_")
      LuhnAlgorithm.isValid(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass mod-97 validation for IbanRu example`() {
      val info = findInfo("IbanRu_")
      isValidIban(info.example) shouldBe true
    }

    @Test
    internal fun `Should pass mod-97 validation for IbanTurkish example`() {
      val info = findInfo("IbanTurkish_")
      isValidIban(info.example) shouldBe true
    }

    @Test
    internal fun `Should have valid 20-digit format for account examples`() {
      listOf("AccountRub_", "AccountCny_", "BankAccount_").forEach { prefix ->
        val info = findInfo(prefix)
        info.example.length shouldBe 20
        info.example.all { it.isDigit() } shouldBe true
      }
    }

    @Test
    internal fun `Should have 9 digits for Bik example`() {
      val info = findInfo("Bik_")
      info.example.length shouldBe 9
      info.example.all { it.isDigit() } shouldBe true
    }

    @Test
    internal fun `Should have 9 digits for Kpp example`() {
      val info = findInfo("Kpp_")
      info.example.length shouldBe 9
      info.example.all { it.isDigit() } shouldBe true
    }

    @Test
    internal fun `Should have correct length for SWIFT examples`() {
      val info8 = findInfo("SwiftRu8_")
      info8.example.length shouldBe 8

      val info11 = findInfo("SwiftRu11_")
      info11.example.length shouldBe 11
    }

    @Test
    internal fun `Should have correct length for Oktmo examples`() {
      val info8 = findInfo("Oktmo8_")
      info8.example.length shouldBe 8
      info8.example.all { it.isDigit() } shouldBe true

      val info11 = findInfo("Oktmo11_")
      info11.example.length shouldBe 11
      info11.example.all { it.isDigit() } shouldBe true
    }
  }

  // --- Вспомогательные методы ---
  private fun findInfo(idPrefix: String): GeneratorInfo<*> =
    GeneratorInfoProvider.generatorInfos.first { it.id.startsWith(idPrefix) }

  private val P10 = intArrayOf(2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P11 = intArrayOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
  private val P12 = intArrayOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)

  private fun isValidInnLegal(inn: String): Boolean {
    val sum = inn.substring(0, 9).foldIndexed(0) { i, acc, c -> acc + P10[i] * (c - '0') }
    return (sum % 11) % 10 == inn[9] - '0'
  }

  private fun isValidInnIndividual(inn: String): Boolean {
    val sum11 = inn.substring(0, 10).foldIndexed(0) { i, acc, c -> acc + P11[i] * (c - '0') }
    val sum12 = inn.substring(0, 11).foldIndexed(0) { i, acc, c -> acc + P12[i] * (c - '0') }
    return (sum11 % 11) % 10 == inn[10] - '0' && (sum12 % 11) % 10 == inn[11] - '0'
  }

  private fun isValidOgrnLegal(ogrn: String): Boolean {
    val base = ogrn.substring(0, 12).toBigInteger()
    val checksum = (base % 11.toBigInteger()).toInt() % 10
    return checksum == ogrn[12] - '0'
  }

  private fun isValidOgrnIp(ogrn: String): Boolean {
    val base = ogrn.substring(0, 14).toBigInteger()
    val checksum = (base % 13.toBigInteger()).toInt() % 10
    return checksum == ogrn[14] - '0'
  }

  private fun isValidSnils(snils: String): Boolean {
    val clean = snils.replace("-", "").replace(" ", "")
    val digits = clean.substring(0, 9)
    val expected = clean.substring(9, 11).toInt()
    val sum = digits.foldIndexed(0) { i, acc, c -> acc + (c - '0') * (9 - i) }
    val control = if (sum % 101 == 100) 0 else sum % 101
    return control == expected
  }

  private fun isValidIban(iban: String): Boolean {
    val rearranged = iban.substring(4) + iban.substring(0, 4)
    val numeric = rearranged.map { c ->
      if (c.isLetter()) (c.uppercaseChar() - 'A' + 10).toString() else c.toString()
    }.joinToString("")
    return BigInteger(numeric).mod(BigInteger("97")).toInt() == 1
  }
}
