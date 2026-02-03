package ru.eda.plgn.bizgen.plugin.di

import com.intellij.ide.plugins.PluginMainDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest

/**
 * Тест для проверки, что все [BizGenService] зарегистрированы в плагине.
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenServiceTest : BaseIdeaTest() {

  @Test
  @Suppress("UnstableApiUsage")
  internal fun `Should verify that all bizGen services are registered in the plugin`() {
    // given
    val bizGenPlugin = PluginManagerCore.getPlugin(id = PluginId("ru.eda.plgn.bizgen"))
    val registryInPlugin =
      (bizGenPlugin as PluginMainDescriptor).appContainerDescriptor.services.mapNotNull { it.implementation }.toSet()

    // when
    val bizGenServices = findImplemented(BizGenService::class, "ru.eda.plgn.bizgen").map { it.name }

    // then
    registryInPlugin shouldContainAllInAnyOrder bizGenServices
  }
}