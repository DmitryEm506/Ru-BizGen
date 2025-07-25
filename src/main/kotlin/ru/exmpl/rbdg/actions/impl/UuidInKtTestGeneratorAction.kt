package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.UuidInKtTestGenerator
import java.util.UUID

/**
 * Действие, которое использует [UuidInKtTestGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidInKtTestGeneratorAction : BaseGeneratorAction<UUID>(
  id = UUID_IN_KT_TEST_GENERATOR_ACTION_ID,
  name = "UUID для Котлин тестов",
  generator = UuidInKtTestGenerator()
) {

  companion object {
    const val UUID_IN_KT_TEST_GENERATOR_ACTION_ID = "UUID_KT_tests_5ff2bc21-3f47-4c4f-978b-6335fe3d0c9d"
  }
}