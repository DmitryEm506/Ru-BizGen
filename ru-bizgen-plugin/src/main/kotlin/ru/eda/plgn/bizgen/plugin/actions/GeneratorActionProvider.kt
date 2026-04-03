package ru.eda.plgn.bizgen.plugin.actions

import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider
import ru.eda.plgn.bizgen.plugin.di.BizGenService

/**
 * Провайдер действий.
 *
 * @author Dmitry_Emelyanenko
 */
interface GeneratorActionProvider : BizGenService {

  /**
   * Получение всех описаний генераторов.
   *
   * @return перечень описаний генераторов
   */
  fun getInfos(): List<GeneratorInfo<*>>

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
  private val infos: List<GeneratorInfo<*>> = GeneratorInfoProvider.generatorInfos
  private val actions: List<BaseGeneratorAction<*>> = infos
    .map { it.toBaseGeneratorAction() }

  override fun getInfos(): List<GeneratorInfo<*>> {
    return infos
  }

  override fun getAnActions(): List<GeneratorAnAction> {
    return actions
  }

  override fun actionCount(): Int {
    return actions.size
  }

  private companion object {
    fun <T : Any> GeneratorInfo<T>.toBaseGeneratorAction(): BaseGeneratorAction<T> {
      return object : BaseGeneratorAction<T>(id, name, generator) {}
    }
  }
}