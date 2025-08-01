package ru.person.bizgen.actions

import ru.person.bizgen.actions.impl.AccountCnyActionGenerator
import ru.person.bizgen.actions.impl.AccountRubActionGenerator
import ru.person.bizgen.actions.impl.BankAccountActionGenerator
import ru.person.bizgen.actions.impl.BikActionGenerator
import ru.person.bizgen.actions.impl.IbanRuActionGenerator
import ru.person.bizgen.actions.impl.IbanTurkishActionGenerator
import ru.person.bizgen.actions.impl.InnIndividualActionGenerator
import ru.person.bizgen.actions.impl.InnLegalActionGenerator
import ru.person.bizgen.actions.impl.KppActionGenerator
import ru.person.bizgen.actions.impl.OgrnIpActionGenerator
import ru.person.bizgen.actions.impl.OgrnLegalActionGenerator
import ru.person.bizgen.actions.impl.Oktmo11ActionGenerator
import ru.person.bizgen.actions.impl.Oktmo8ActionGenerator
import ru.person.bizgen.actions.impl.Swift11ActionGenerator
import ru.person.bizgen.actions.impl.Swift8ActionGenerator
import ru.person.bizgen.actions.impl.UuidGeneratorAction
import ru.person.bizgen.actions.impl.UuidInKtTestGeneratorAction
import ru.person.bizgen.di.BizGenService

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
    UuidInKtTestGeneratorAction(),
    AccountRubActionGenerator(),
    AccountCnyActionGenerator(),
    Swift8ActionGenerator(),
    Swift11ActionGenerator(),
    InnIndividualActionGenerator(),
    InnLegalActionGenerator(),
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