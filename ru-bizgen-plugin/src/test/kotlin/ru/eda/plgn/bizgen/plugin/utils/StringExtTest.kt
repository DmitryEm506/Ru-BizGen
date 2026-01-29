package ru.eda.plgn.bizgen.plugin.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.utils.withEscape

/**
 * Модульные тесты для [StringExt.kt].
 *
 * @author Dmitry_Emelyanenko
 */
class StringExtTest {

  @Nested
  @DisplayName("Testing scope: withEscape")
  inner class WithEscapeCases {

    @Test
    internal fun `Should return escaped source`() {
      // expect
      "test text 134".withEscape() shouldBe "\"test text 134\""
    }
  }
}