package ru.eda.plgn.bizgen.core.generator.impl

import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator.GeneratorBaseTest
import java.util.UUID

/**
 * Tests for UUID string generator — [UuidAsStrGenerator].
 *
 * @author Dmitry_Emelyanenko
 */
internal class UuidAsStrGeneratorTest : GeneratorBaseTest<UUID>(UuidAsStrGenerator()) {

  @Test
  internal fun `Should return an editor-escaped UUID`() {
    val result = generator.generate()
    result.toEditor shouldMatch Regex("^\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"$")
  }
}