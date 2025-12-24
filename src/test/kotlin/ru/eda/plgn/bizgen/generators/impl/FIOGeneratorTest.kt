package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.StrGeneratorTest

/**
 * Тесты для генератора полного ФИО - [FIOFullGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class FIOGeneratorTest {

  @Nested
  @DisplayName("Testing scope: FIOFullGeneratorCases")
  inner class FIOFullGeneratorCases : StrGeneratorTest(FIOFullGenerator()) {

    @TestFactory
    internal fun `Should generate a full name in format 'Lastname Firstname Patronymic'`() =
      testsOnDistance { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+$""")
      }

    @Test
    internal fun `Should return an editor-escaped full name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+"$""")
    }
  }

  @Nested
  @DisplayName("Testing scope: FIOShortGeneratorCases")
  inner class FIOShortGeneratorCases : StrGeneratorTest(FIOShortGenerator()) {

    @TestFactory
    internal fun `Should generate a short name in format 'Lastname IO'`() =
      testsOnDistance { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\.$""")
      }

    @Test
    internal fun `Should return an editor-escaped short name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\."$""")
    }
  }

  @Nested
  @DisplayName("Testing scope: FIOInitialsGeneratorCases")
  inner class FIOInitialsGeneratorCases : StrGeneratorTest(FIOInitialsGenerator()) {

    @TestFactory
    internal fun `Should generate initials name in format IO Lastname`() =
      testsOnDistance { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ]\.[А-ЯЁ]\. [А-ЯЁ][а-яё]+$""")
      }

    @Test
    internal fun `Should return an editor-escaped initials name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ]\.[А-ЯЁ]\. [А-ЯЁ][а-яё]+"$""")
    }
  }
}