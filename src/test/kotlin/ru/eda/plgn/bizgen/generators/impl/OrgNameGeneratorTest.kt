package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.GeneratorStrTest

/**
 * Тесты для генераторов названий организаций — [OrgRuNameGenerator], [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OrgNameGeneratorTest {

  @Nested
  @DisplayName("Testing scope: OrgRuNameGenerator")
  inner class OrgRuNameGeneratorCases : GeneratorStrTest(OrgRuNameGenerator()) {
    private val orgTypePattern = "(ООО|АО|ПАО|НАО|ИП|ФКП|ФГУП|ГУП|МУП|ГБУ|МБУ|МАУ|ГАУ|АНО|НКО|Фонд|Ассоциация)"

    @TestFactory
    internal fun `Should generate a valid Russian organization name`() = testsOnDistanceToClipboard { name ->
      name shouldMatch Regex("^$orgTypePattern.+")
    }

    @TestFactory
    internal fun `Should return an editor-escaped organization name`() = testsOnDistanceToEditor { toEditor ->
      toEditor shouldMatch Regex("^\"$orgTypePattern .+\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: OrgEngNameGenerator")
  inner class OrgEngNameGeneratorCases : GeneratorStrTest(OrgEngNameGenerator()) {
    private val orgTypePattern = "(LLC|SP|Ltd\\.|Inc\\.|Corp\\.|PLC|LP|LLP|Foundation|Association|Trust)"

    @TestFactory
    internal fun `Should generate a valid English organization name`() = testsOnDistanceToClipboard { name ->
      name shouldMatch Regex(".*$orgTypePattern$")
    }

    @Test
    internal fun `Should return an editor-escaped organization name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex(".*$orgTypePattern\"$")
    }
  }
}