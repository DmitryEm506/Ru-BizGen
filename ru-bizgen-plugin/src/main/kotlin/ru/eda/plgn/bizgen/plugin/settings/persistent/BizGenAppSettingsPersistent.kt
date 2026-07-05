package ru.eda.plgn.bizgen.plugin.settings.persistent

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.di.getBizGenService
import ru.eda.plgn.bizgen.plugin.settings.BizGenAppSettingsRepository
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings

/**
 * Отвечает за сохранение настроек [BizGenAppSettings] на диск.
 *
 * @property settings настройки плагина
 * @author Dmitry_Emelyanenko
 */
@State(
  name = "Ru BizGen",
  storages = [
    Storage("bizgen_plugin_settings.xml"),
  ],
  category = SettingsCategory.PLUGINS,
)
internal class BizGenAppSettingsPersistent(
  var settings: BizGenAppSettings = BizGenAppSettings(),
) : PersistentStateComponent<BizGenAppSettings>, BizGenAppSettingsRepository {

  private val log = Logger.getInstance(this::class.java)

  override fun getState(): BizGenAppSettings = settings

  override fun loadState(bizGenAppSettings: BizGenAppSettings) {
    try {
      // Мягкая миграция
      val updatedActions = getBizGenService<BizGenAppSettingsSoftUpdater>().softUpdateActions(bizGenAppSettings.actualActions)

      bizGenAppSettings.actualActions = updatedActions
    } catch (ex: Exception) {
      log.warn("Failed to soft update actions", ex)
      bizGenAppSettings.restoreFromDefault()
    }

    if (bizGenAppSettings.actualActions.isEmpty()) {
      bizGenAppSettings.restoreFromDefault()
    }

    settings = bizGenAppSettings

    applyCustomNames()
  }

  private fun applyCustomNames() {
    val actions = getBizGenService<GeneratorActionProvider>().getAnActions().associateBy { it.id }
    val infos = getBizGenService<GeneratorActionProvider>().getInfos().associateBy { it.id }
    settings.actualActions.forEach { setting ->
      val action = actions[setting.id] ?: return@forEach
      action.templatePresentation.text = if (setting.customName.isNotBlank()) {
        setting.customName
      } else {
        infos[setting.id]?.name ?: return@forEach
      }
    }
  }

  override fun settings(): BizGenAppSettings {
    return settings
  }
}

