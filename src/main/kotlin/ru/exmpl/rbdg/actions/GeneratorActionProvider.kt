package ru.exmpl.rbdg.actions

import ru.exmpl.rbdg.actions.impl.AccountCnyActionGenerator
import ru.exmpl.rbdg.actions.impl.AccountRubActionGenerator
import ru.exmpl.rbdg.actions.impl.BankAccountActionGenerator
import ru.exmpl.rbdg.actions.impl.BikActionGenerator
import ru.exmpl.rbdg.actions.impl.IbanRuActionGenerator
import ru.exmpl.rbdg.actions.impl.IbanTurkishActionGenerator
import ru.exmpl.rbdg.actions.impl.InnIndividualActionGenerator
import ru.exmpl.rbdg.actions.impl.InnLegalActionGenerator
import ru.exmpl.rbdg.actions.impl.KppActionGenerator
import ru.exmpl.rbdg.actions.impl.OgrnIpActionGenerator
import ru.exmpl.rbdg.actions.impl.OgrnLegalActionGenerator
import ru.exmpl.rbdg.actions.impl.Oktmo11ActionGenerator
import ru.exmpl.rbdg.actions.impl.Oktmo8ActionGenerator
import ru.exmpl.rbdg.actions.impl.Swift11ActionGenerator
import ru.exmpl.rbdg.actions.impl.Swift8ActionGenerator
import ru.exmpl.rbdg.actions.impl.UuidGeneratorAction
import ru.exmpl.rbdg.actions.impl.UuidInKtTestGeneratorAction
import ru.exmpl.rbdg.di.RbdgService

/**
 * Провайдер действий.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionProvider : RbdgService {

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