package ru.exmpl.rbdg.actions.impl

import ru.exmpl.rbdg.actions.BaseGeneratorAction
import ru.exmpl.rbdg.generators.impl.UuidForTestGenerator
import java.util.UUID

/**
 * Действие, которое использует [UuidForTestGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
class UuidForTestGeneratorAction : BaseGeneratorAction<UUID>(
  id = "UUID_KT_tests_5ff2bc21-3f47-4c4f-978b-6335fe3d0c9d",
  name = "UUID для Котлин тестов",
  generator = UuidForTestGenerator()
)