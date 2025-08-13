package ru.person.bizgen.clipboard

import ru.person.bizgen.di.BizGenService
import ru.person.bizgen.di.getBizGenService
import ru.person.bizgen.settings.BizGenAppSettingsRepository

/**
 * Сервис для управления настройкой: признак необходимо вставлять в буфер обмена сгенерированный результат генератора или нет.
 *
 * @author Dmitry_Emelyanenko
 */
interface BizGenClipboardSettingsService : BizGenClipboardSettingsServiceView {


  /**
   * Изменение состояния настройки.
   *
   * @param enabled true - необходимо вставлять, false - нет
   */
  fun setClipboardSetting(enabled: Boolean)
}

/**
 * Сервис получения настройки: признак необходимо вставлять в буфер обмена сгенерированный результат генератора или нет.
 *
 * @author Dmitry_Emelyanenko
 */
interface BizGenClipboardSettingsServiceView : BizGenService {

  /**
   * Получение текущего состояния признака.
   *
   * @return true - необходимо вставлять, false - нет
   */
  fun needToIns(): Boolean
}

internal class BizGenClipboardSettingsServiceImpl : BizGenClipboardSettingsService {
  override fun setClipboardSetting(enabled: Boolean) {
    getBizGenService<BizGenAppSettingsRepository>().settings().insToClipboard = enabled
  }

  override fun needToIns(): Boolean {
    return getBizGenService<BizGenAppSettingsRepository>().settings().insToClipboard
  }
}