package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.TestFactory
import ru.eda.plgn.bizgen.core.generator.GeneratorStrTest

/**
 * Тесты для генератора КПП — [KppGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class KppGeneratorTest : GeneratorStrTest(KppGenerator()) {

  @TestFactory
  internal fun `Should generate a valid KPP format (9 digits)`() = testsOnDistanceToClipboard { kpp ->
    kpp shouldHaveLength 9
    kpp shouldMatch Regex("""^\d{9}$""")
  }

  @TestFactory
  internal fun `Should generate a KPP with valid reason code (01-50)`() = testsOnDistanceToClipboard { kpp ->
    val reasonCode = kpp.substring(4, 6).toInt()
    reasonCode shouldBeInRange 1..50
  }
}
