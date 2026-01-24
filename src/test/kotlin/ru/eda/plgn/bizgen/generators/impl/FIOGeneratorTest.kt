package ru.eda.plgn.bizgen.generators.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.generators.GeneratorStrTest

/**
 * Тесты для генератора полного ФИО - [FIOFullGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class FIOGeneratorTest {

  @Nested
  @DisplayName("Testing scope: FIOFullGenerator")
  inner class FIOFullGeneratorCases : GeneratorStrTest(FIOFullGenerator()) {

    @TestFactory
    internal fun `Should generate a full name in format 'Lastname Firstname Patronymic'`() =
      testsOnDistanceToClipboard { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+$""")
      }

    @Test
    internal fun `Should return an editor-escaped full name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+ [А-ЯЁ][а-яё]+"$""")
    }
  }

  @Nested
  @DisplayName("Testing scope: FIOShortGenerator")
  inner class FIOShortGeneratorCases : GeneratorStrTest(FIOShortGenerator()) {

    @TestFactory
    internal fun `Should generate a short name in format 'Lastname IO'`() =
      testsOnDistanceToClipboard { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\.$""")
      }

    @Test
    internal fun `Should return an editor-escaped short name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\."$""")
    }
  }

  @Nested
  @DisplayName("Testing scope: FIOInitialsGenerator")
  inner class FIOInitialsGeneratorCases : GeneratorStrTest(FIOInitialsGenerator()) {

    @TestFactory
    internal fun `Should generate initials name in format IO Lastname`() =
      testsOnDistanceToClipboard { fio ->
        fio shouldMatch Regex("""^[А-ЯЁ]\.[А-ЯЁ]\. [А-ЯЁ][а-яё]+$""")
      }

    @Test
    internal fun `Should return an editor-escaped initials name`() {
      val result = generator.generate()
      result.toEditor shouldMatch Regex("""^"[А-ЯЁ]\.[А-ЯЁ]\. [А-ЯЁ][а-яё]+"$""")
    }
  }
}