package ru.eda.plgn.bizgen.mcp

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

/**
 * Тесты [ToolDispatcher].
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("ToolDispatcher")
internal class ToolDispatcherTest {

  private val infos = GeneratorInfoProvider.generatorInfos

  @Nested
  @DisplayName("10.1 Одиночное выполнение для всех генераторов")
  inner class SingleExecutionCases {

    @Test
    internal fun `Should execute all generators without errors`() {
      infos.forEach { info ->
        val typeKey = TypeKeyResolver.resolve(info)
        val result = ToolDispatcher.execute(info.category, typeKey, count = 1, infos = infos)

        result.isError shouldNotBe true
        result.content shouldHaveSize 1
        val text = (result.content.first() as TextContent).text
        text.shouldNotBeBlank()
      }
    }

    @Test
    internal fun `Should not return structuredContent for single execution`() {
      val info = infos.first { it.id.startsWith("InnLegal_") }
      val result = ToolDispatcher.execute(info.category, TypeKeyResolver.resolve(info), count = 1, infos = infos)

      result.structuredContent shouldBe null
    }
  }

  @Nested
  @DisplayName("10.2 Batch-генерация")
  inner class BatchCases {

    @Test
    internal fun `Should return 5 unique values when count is 5`() {
      val info = infos.first { it.id.startsWith("UUID_") }
      val result = ToolDispatcher.execute(info.category, TypeKeyResolver.resolve(info), count = 5, infos = infos)

      result.isError shouldNotBe true
      val text = (result.content.first() as TextContent).text
      val values = text.split("\n")
      values shouldHaveSize 5
      values.toSet() shouldHaveSize 5
    }

    @Test
    internal fun `Should return 1 value when count is 1`() {
      val info = infos.first { it.id.startsWith("Bik_") }
      val result = ToolDispatcher.execute(info.category, TypeKeyResolver.resolve(info), count = 1, infos = infos)

      result.isError shouldNotBe true
      val text = (result.content.first() as TextContent).text
      text.split("\n") shouldHaveSize 1
    }
  }

  @Nested
  @DisplayName("10.3 count > uniqueDistance")
  inner class UniqueDistanceCases {

    @Test
    internal fun `Should return isError true when count exceeds uniqueDistance`() {
      val info = infos.first { it.id.startsWith("Bik_") }
      val uniqueDistance = info.generator.uniqueDistance
      val result = ToolDispatcher.execute(
        info.category, TypeKeyResolver.resolve(info),
        count = uniqueDistance + 1, infos = infos,
      )

      result.isError shouldBe true
      val text = (result.content.first() as TextContent).text
      text shouldContain "uniqueDistance"
      text shouldContain uniqueDistance.toString()
    }
  }

  @Nested
  @DisplayName("10.4 count > MAX_COUNT")
  inner class MaxCountCases {

    @Test
    internal fun `Should return isError true when count exceeds MAX_COUNT`() {
      val info = infos.first { it.id.startsWith("UUID_") }
      val result = ToolDispatcher.execute(
        info.category, TypeKeyResolver.resolve(info),
        count = ToolDispatcher.MAX_COUNT + 1, infos = infos,
      )

      result.isError shouldBe true
      val text = (result.content.first() as TextContent).text
      text shouldContain "максимум"
      text shouldContain ToolDispatcher.MAX_COUNT.toString()
    }
  }

  @Nested
  @DisplayName("10.5 Несуществующий type")
  inner class InvalidTypeCases {

    @Test
    internal fun `Should return isError true for non-existent type`() {
      val result = ToolDispatcher.execute(
        GeneratorCategory.LEGAL, type = "nonexistent_type", count = 1, infos = infos,
      )

      result.isError shouldBe true
      val text = (result.content.first() as TextContent).text
      text shouldContain "не найден"
    }
  }

  @Nested
  @DisplayName("10.6 Type из другой категории")
  inner class CrossCategoryCases {

    @Test
    internal fun `Should return isError true for type from different category`() {
      val ibanInfo = infos.first { it.id.startsWith("IbanRu_") }
      val ibanTypeKey = TypeKeyResolver.resolve(ibanInfo)

      val result = ToolDispatcher.execute(
        GeneratorCategory.LEGAL, type = ibanTypeKey, count = 1, infos = infos,
      )

      result.isError shouldBe true
      val text = (result.content.first() as TextContent).text
      text shouldContain "не найден"
    }
  }

  @Nested
  @DisplayName("10.7 structuredContent")
  inner class StructuredContentCases {

    @Test
    internal fun `Should contain structuredContent with type count values when count is 3`() {
      val info = infos.first { it.id.startsWith("UUID_") }
      val typeKey = TypeKeyResolver.resolve(info)
      val result = ToolDispatcher.execute(info.category, typeKey, count = 3, infos = infos)

      result.isError shouldNotBe true
      val structured = result.structuredContent
      structured shouldNotBe null

      structured!!.jsonObject["type"]!!.jsonPrimitive.content shouldBe typeKey
      structured.jsonObject["count"]!!.jsonPrimitive.intOrNull shouldBe 3
      structured.jsonObject["values"]!!.jsonArray shouldHaveSize 3
    }

    @Test
    internal fun `Should not contain structuredContent when count is 1`() {
      val info = infos.first { it.id.startsWith("UUID_") }
      val result = ToolDispatcher.execute(info.category, TypeKeyResolver.resolve(info), count = 1, infos = infos)

      result.structuredContent shouldBe null
    }
  }

  @Nested
  @DisplayName("10.8 partial=true при низкой энтропии")
  inner class PartialCases {

    @Test
    internal fun `Should return partial true for low entropy generator`() {
      val lowEntropyInfo = constantGeneratorInfo()
      val allInfos = infos + lowEntropyInfo
      val typeKey = TypeKeyResolver.resolve(lowEntropyInfo)

      val result = ToolDispatcher.execute(
        lowEntropyInfo.category, typeKey, count = 5, infos = allInfos,
      )

      result.isError shouldNotBe true
      val structured = result.structuredContent
      structured shouldNotBe null

      structured!!.jsonObject["partial"]?.jsonPrimitive?.content shouldBe "true"
      structured.jsonObject["count"]!!.jsonPrimitive.intOrNull shouldBe 1
    }
  }

  @Nested
  @DisplayName("10.9 CancellationException не маскируется")
  inner class CancellationExceptionCases {

    @Test
    internal fun `Should rethrow CancellationException instead of swallowing`() {
      val cancellingInfo = cancellingGeneratorInfo()
      val allInfos = infos + cancellingInfo
      val typeKey = TypeKeyResolver.resolve(cancellingInfo)

      assertThrows<CancellationException> {
        ToolDispatcher.execute(cancellingInfo.category, typeKey, count = 1, infos = allInfos)
      }
    }
  }

  @Nested
  @DisplayName("10.10 Бизнес-исключения")
  inner class BusinessExceptionCases {

    @Test
    internal fun `Should return isError true with message for IllegalStateException`() {
      val failingInfo = failingGeneratorInfo(IllegalStateException("business error"))
      val allInfos = infos + failingInfo
      val typeKey = TypeKeyResolver.resolve(failingInfo)

      val result = ToolDispatcher.execute(failingInfo.category, typeKey, count = 1, infos = allInfos)

      result.isError shouldBe true
      val text = (result.content.first() as TextContent).text
      text shouldContain "business error"
    }
  }

  private fun constantGeneratorInfo(): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = "ConstantGen_aabbccdd-1111-2222-3333-444455556666"
      override val name: String = "Constant Generator"
      override val category: GeneratorCategory = GeneratorCategory.TECHNICAL
      override val detailedDescription: String = "Always returns the same value"
      override val example: String = "CONST"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 130
        override fun generate(): GeneratorResult<String> = GeneratorResult("CONST", "CONST")
      }
    }

  private fun failingGeneratorInfo(exception: RuntimeException): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = "FailingGen_aabbccdd-1111-2222-3333-444455556666"
      override val name: String = "Failing Generator"
      override val category: GeneratorCategory = GeneratorCategory.TECHNICAL
      override val detailedDescription: String = "Always fails"
      override val example: String = "FAIL"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 130
        override fun generate(): GeneratorResult<String> = throw exception
      }
    }

  private fun cancellingGeneratorInfo(): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = "CancellingGen_aabbccdd-1111-2222-3333-444455556666"
      override val name: String = "Cancelling Generator"
      override val category: GeneratorCategory = GeneratorCategory.TECHNICAL
      override val detailedDescription: String = "Throws CancellationException"
      override val example: String = "CANCEL"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 130
        override fun generate(): GeneratorResult<String> = throw CancellationException("cancelled")
      }
    }

  private fun String.shouldNotBeBlank(): String = also {
    require(isNotBlank()) { "String should not be blank" }
  }
}
