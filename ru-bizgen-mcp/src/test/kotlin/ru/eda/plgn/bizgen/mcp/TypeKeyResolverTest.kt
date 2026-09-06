package ru.eda.plgn.bizgen.mcp

import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

/**
 * Тесты [TypeKeyResolver].
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("TypeKeyResolver")
internal class TypeKeyResolverTest {

  @Nested
  @DisplayName("6.2 Соответствие type-key ожидаемому")
  inner class TypeKeyCases {

    @Test
    internal fun `Should resolve type-key for all generators to expected value`() {
      val expected = mapOf(
        "UUID_" to "uuid",
        "AccountRub_" to "account_rub",
        "AccountCny_" to "account_cny",
        "BankAccount_" to "bank_account",
        "Bik_" to "bik",
        "SwiftRu8_" to "swift_ru8",
        "SwiftRu11_" to "swift_ru11",
        "IbanRu_" to "iban_ru",
        "IbanTurkish_" to "iban_turkish",
        "InnIndividual_" to "inn_individual",
        "InnLegal_" to "inn_legal",
        "Kpp_" to "kpp",
        "OgrnLegal_" to "ogrn_legal",
        "OgrnIp_" to "ogrn_ip",
        "Oktmo8_" to "oktmo8",
        "Oktmo11_" to "oktmo11",
        "OrgRuName_" to "org_ru_name",
        "OrgEngName_" to "org_eng_name",
        "Address_" to "address",
        "Country_" to "country",
        "CardNumber_" to "card_number",
        "FioFull_" to "fio_full",
        "FioShort_" to "fio_short",
        "FioInitials_" to "fio_initials",
        "PhoneNumberRuFormat_" to "phone_number_ru_format",
        "PhoneNumberRuDigit_" to "phone_number_ru_digit",
        "Snils_" to "snils",
        "PassportRuSpaced_" to "passport_ru_spaced",
        "PassportRuCompact_" to "passport_ru_compact",
        "ForeignPassportRuNumber_" to "foreign_passport_ru_number",
        "ForeignPassportRuMrz_" to "foreign_passport_ru_mrz",
      )

      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val expectedPrefix = expected.keys.first { info.id.startsWith(it) }
        val expectedTypeKey = expected[expectedPrefix]
        TypeKeyResolver.resolve(info) shouldBe expectedTypeKey
      }
    }

    @Test
    internal fun `Should resolve InnLegal to inn_legal`() {
      val info = GeneratorInfoProvider.generatorInfos.first { it.id.startsWith("InnLegal_") }
      TypeKeyResolver.resolve(info) shouldBe "inn_legal"
    }

    @Test
    internal fun `Should resolve UUID to uuid`() {
      val info = GeneratorInfoProvider.generatorInfos.first { it.id.startsWith("UUID_") }
      TypeKeyResolver.resolve(info) shouldBe "uuid"
    }

    @Test
    internal fun `Should resolve IbanRu to iban_ru`() {
      val info = GeneratorInfoProvider.generatorInfos.first { it.id.startsWith("IbanRu_") }
      TypeKeyResolver.resolve(info) shouldBe "iban_ru"
    }
  }

  @Nested
  @DisplayName("6.3 Уникальность type-key")
  inner class UniquenessCases {

    @Test
    internal fun `Should have unique type-keys for all generators`() {
      val typeKeys = GeneratorInfoProvider.generatorInfos.map { TypeKeyResolver.resolve(it) }
      typeKeys.shouldBeUnique()
    }

    @Test
    internal fun `Should have type-key count equal to generator count`() {
      val typeKeys = GeneratorInfoProvider.generatorInfos.map { TypeKeyResolver.resolve(it) }
      typeKeys.size shouldBe GeneratorInfoProvider.generatorInfos.size
    }
  }
}
