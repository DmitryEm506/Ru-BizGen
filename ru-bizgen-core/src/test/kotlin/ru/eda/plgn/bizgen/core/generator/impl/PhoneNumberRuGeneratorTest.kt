package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

/**
 * Тест для генераторов номеров российских телефонов:
 * - [PhoneNumberRuFormatGenerator]
 * - [PhoneNumberRuDigitGenerator]
 *
 * @author Dmitry_Emelyanenko
 */
internal class PhoneNumberRuGeneratorTest {
  private companion object {
    private const val AVAILABLE_PREFIXES = "(" +
    // BEELINE
    "900|901|902|903|904|905|906|907|908|909|960|961|962|963|964|965|966|967|968|969|" +
    // MTS
    "910|911|912|913|914|915|916|917|918|919|980|981|982|983|984|985|986|987|988|989|" +
    // MEGA_FON
    "920|921|922|923|924|925|926|927|928|929|930|931|932|933|934|935|936|937|938|939|" +
    // TELE2
    "950|951|952|953|954|955|956|957|958|959|970|971|972|973|974|975|976|977|978|979" +
    ")"

    private const val PHONE_BY_FORMAT_PATTERN = "\\+7 \\($AVAILABLE_PREFIXES\\) \\d{3}-\\d{2}-\\d{2}"
    private const val PHONE_BY_DIGIT_PATTERN = "7$AVAILABLE_PREFIXES\\d{7}"
  }

  @Nested
  @DisplayName("Testing scope: PhoneNumberRuFormatGenerator")
  inner class PhoneNumberRuFormatGeneratorCases : GeneratorStrTest(PhoneNumberRuFormatGenerator()) {

    @TestFactory
    fun `Should generate valid phone numbers by format`() = testsOnDistanceToClipboard { phone ->
      phone shouldMatch Regex("^$PHONE_BY_FORMAT_PATTERN$")
    }

    @TestFactory
    fun `Should generate an editor-escaped valid phone numbers by format`() = testsOnDistanceToEditor { phone ->
      phone shouldMatch Regex("^\"$PHONE_BY_FORMAT_PATTERN\"$")
    }
  }

  @Nested
  @DisplayName("Testing scope: PhoneNumberRuDigitGenerator")
  inner class PhoneNumberRuDigitGeneratorCases : GeneratorStrTest(PhoneNumberRuDigitGenerator()) {
    @TestFactory
    fun `Should generate valid phone numbers by format`() = testsOnDistanceToClipboard { phone ->
      phone shouldMatch Regex("^$PHONE_BY_DIGIT_PATTERN$")
    }

    @TestFactory
    fun `Should generate an editor-escaped valid phone numbers by format`() = testsOnDistanceToEditor { phone ->
      phone shouldMatch Regex("^\"$PHONE_BY_DIGIT_PATTERN\"$")
    }
  }
}