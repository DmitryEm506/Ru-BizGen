package ru.eda.plgn.bizgen.actions

import ru.eda.plgn.bizgen.actions.impl.AddressActionGenerator
import ru.eda.plgn.bizgen.actions.impl.BankAccountActionGenerator
import ru.eda.plgn.bizgen.actions.impl.BikActionGenerator
import ru.eda.plgn.bizgen.actions.impl.CountryActionGenerator
import ru.eda.plgn.bizgen.actions.impl.KppActionGenerator
import ru.eda.plgn.bizgen.actions.impl.UuidGeneratorAction
import ru.eda.plgn.bizgen.actions.impl.account.AccountCnyActionGenerator
import ru.eda.plgn.bizgen.actions.impl.account.AccountRubActionGenerator
import ru.eda.plgn.bizgen.actions.impl.fio.FIOFullActionGenerator
import ru.eda.plgn.bizgen.actions.impl.fio.FIOInitialsActionGenerator
import ru.eda.plgn.bizgen.actions.impl.fio.FIOShortActionGenerator
import ru.eda.plgn.bizgen.actions.impl.iban.IbanRuActionGenerator
import ru.eda.plgn.bizgen.actions.impl.iban.IbanTurkishActionGenerator
import ru.eda.plgn.bizgen.actions.impl.inn.InnIndividualActionGenerator
import ru.eda.plgn.bizgen.actions.impl.inn.InnLegalActionGenerator
import ru.eda.plgn.bizgen.actions.impl.ogrn.OgrnIpActionGenerator
import ru.eda.plgn.bizgen.actions.impl.ogrn.OgrnLegalActionGenerator
import ru.eda.plgn.bizgen.actions.impl.oktmo.Oktmo11ActionGenerator
import ru.eda.plgn.bizgen.actions.impl.oktmo.Oktmo8ActionGenerator
import ru.eda.plgn.bizgen.actions.impl.org.OrgEngNameActionGenerator
import ru.eda.plgn.bizgen.actions.impl.org.OrgRuNameActionGenerator
import ru.eda.plgn.bizgen.actions.impl.swift.Swift11ActionGenerator
import ru.eda.plgn.bizgen.actions.impl.swift.Swift8ActionGenerator
import ru.eda.plgn.bizgen.di.BizGenService

/**
 * Провайдер действий.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionProvider : BizGenService {

  /**
   * Получение всех описаний действий.
   *
   * @return перечень описаний действий
   */
  fun getActions(): List<GeneratorAction<*>>

  /**
   * Получение всех действия для применения.
   *
   * @return перечень действия для применения
   */
  fun getAnActions(): List<GeneratorAnAction>

  /**
   * Получение общего количества зарегистрированных действий.
   *
   * @return количество зарегистрированных действий в провайдере
   */
  fun actionCount(): Int
}

/** Реализация [GeneratorActionProvider]. */
class GeneratorActionProviderImpl : GeneratorActionProvider {

  /**
   * Основной компонент плагина.
   *
   * Должен содержать все действия, с которыми предполагается работа.
   *
   * ***ВАЖНО***
   * - от порядка добавления действий зависит порядок отображаемых действий, когда вызывается плагин и при отображении настроек
   * - после добавления действия необходимо скинуть настройки через пункт меню всех настроек *Settings --> Tools --> Ru Business Data
   *   Generator --> Список с генераторами --> Иконка "Reset"*
   */
  private val actions: List<BaseGeneratorAction<*>> = listOf(
    UuidGeneratorAction(),
    AccountRubActionGenerator(),
    AccountCnyActionGenerator(),
    Swift8ActionGenerator(),
    Swift11ActionGenerator(),
    InnIndividualActionGenerator(),
    InnLegalActionGenerator(),
    OrgRuNameActionGenerator(),
    OrgEngNameActionGenerator(),
    FIOFullActionGenerator(),
    FIOShortActionGenerator(),
    FIOInitialsActionGenerator(),
    AddressActionGenerator(),
    CountryActionGenerator(),
    BikActionGenerator(),
    BankAccountActionGenerator(),
    KppActionGenerator(),
    OgrnIpActionGenerator(),
    OgrnLegalActionGenerator(),
    Oktmo8ActionGenerator(),
    Oktmo11ActionGenerator(),
    IbanRuActionGenerator(),
    IbanTurkishActionGenerator()
  )

  override fun getActions(): List<GeneratorAction<*>> {
    return actions
  }

  override fun getAnActions(): List<GeneratorAnAction> {
    return actions
  }

  override fun actionCount(): Int {
    return actions.size
  }
}