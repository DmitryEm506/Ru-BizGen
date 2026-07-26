package ru.eda.plgn.bizgen.plugin.base

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * UI тест, проверяющий корректность работы UI тестов.
 *
 * @author Dmitry_Emelyanenko
 */
internal class IdeaLifecycleUITest : BaseUIIntegrationTest() {

  @Test
  internal fun `Should correct Starter lifecycle - install plugin, start IDE, shutdown`(@TempDir projectDir: Path) {
    runIdea("spikeLifecycle", projectDir) { }
  }
}