package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генераторов названий организаций — [OrgRuNameGenerator], [OrgEngNameGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class OrgNameGeneratorTest {

  @Nested
  @DisplayName("Testing scope: OrgRuNameGeneratorCases")
  inner class OrgRuNameGeneratorCases : StrGeneratorTest(OrgRuNameGenerator()) {

    @TestFactory
    internal fun `Should generate a valid Russian organization name`() = testsOnDistance { name ->
      name shouldMatch Regex("^(ООО|ИП|АО|ФКП) .+")
    }

    @Test
    internal fun `Should return an editor-escaped organization name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"(ООО|ИП|АО|ФКП) .+\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: OrgEngNameGeneratorCases")
  inner class OrgEngNameGeneratorCases : StrGeneratorTest(OrgEngNameGenerator(), 10) {

    @TestFactory
    internal fun `Should generate a valid English organization name`() = testsOnDistance { name ->
      name shouldMatch Regex("^(LLC|SP|PJSC|FSE) .+")
    }

    @Test
    internal fun `Should return an editor-escaped organization name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("^\"(LLC|SP|PJSC|FSE) .+\"$")
    }
  }
}