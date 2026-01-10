package ru.eda.plgn.bizgen

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Модульный тест для [deepCopyByJson].
 *
 * @author Dmitry_Emelyanenko
 */
internal class ObjectExtTest : BaseTest() {

  internal data class TestData(val name: String, val age: Int, val nested: NestedData? = null)
  internal data class NestedData(val value: String)

  @TestFactory
  fun `Should perform deep copy of an object`() =
    tests(
      listOf(
        TestData("Original", 25, NestedData("Nested")),
        TestData("Another", 30, null)
      ),
      { "Deep copy test: $it" }
    ) { original ->
      val copy = original.deepCopyByJson()

      copy shouldBe original
      copy shouldNotBeSameInstanceAs original
      if (original.nested != null) {
        copy.nested shouldNotBeSameInstanceAs original.nested
      }
    }

  @Test
  fun `Should preserve data after modification of the copy`() {
    val original = TestData("Original", 25, NestedData("Value"))

    val copy = original.deepCopyByJson()
    val modifiedCopy = copy.copy(name = "Modified", nested = NestedData("NewValue"))

    modifiedCopy.name shouldBe "Modified"
    original.name shouldBe "Original"

    modifiedCopy.nested?.value shouldBe "NewValue"
    original.nested?.value shouldBe "Value"
  }
}
