package ru.eda.plgn.bizgen.core.generator_info

import ru.eda.plgn.bizgen.core.generator_info.impl.AddressGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.BankAccountGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.BikGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.CardNumberGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.CountryGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.KppGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.SnilsGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.UuidGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.account.AccountCnyGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.account.AccountRubGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.fio.FIOFullGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.fio.FIOInitialsGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.fio.FIOShortGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.iban.IbanRuGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.iban.IbanTurkishGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.inn.InnIndividualGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.inn.InnLegalGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.ogrn.OgrnIpGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.ogrn.OgrnLegalGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.oktmo.Oktmo11GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.oktmo.Oktmo8GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.org.OrgEngNameGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.org.OrgRuNameGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.phone_number.PhoneNumberRuDigitGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.phone_number.PhoneNumberRuFormatGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.swift.Swift11GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.swift.Swift8GeneratorInfo

/**
 * Провайдер, который содержит список всех реализованных генераторов.
 *
 * @author Dmitry_Emelyanenko
 */
object GeneratorInfoProvider {
  val generatorInfos: List<GeneratorInfo<*>> = listOf(
    // Технические
    UuidGeneratorInfo(),

    // Организации
    AccountRubGeneratorInfo(),
    AccountCnyGeneratorInfo(),
    InnIndividualGeneratorInfo(),
    InnLegalGeneratorInfo(),
    KppGeneratorInfo(),
    OgrnLegalGeneratorInfo(),
    OgrnIpGeneratorInfo(),
    OrgRuNameGeneratorInfo(),
    OrgEngNameGeneratorInfo(),

    // Банки и платёжные реквизиты
    BikGeneratorInfo(),
    BankAccountGeneratorInfo(),
    Swift8GeneratorInfo(),
    Swift11GeneratorInfo(),
    IbanRuGeneratorInfo(),
    IbanTurkishGeneratorInfo(),

    // Адресно-территориальные данные
    AddressGeneratorInfo(),
    CountryGeneratorInfo(),
    Oktmo8GeneratorInfo(),
    Oktmo11GeneratorInfo(),

    // Персональные данные
    CardNumberGeneratorInfo(),
    FIOFullGeneratorInfo(),
    FIOShortGeneratorInfo(),
    FIOInitialsGeneratorInfo(),
    PhoneNumberRuFormatGeneratorInfo(),
    PhoneNumberRuDigitGeneratorInfo(),
    SnilsGeneratorInfo(),
  )
}