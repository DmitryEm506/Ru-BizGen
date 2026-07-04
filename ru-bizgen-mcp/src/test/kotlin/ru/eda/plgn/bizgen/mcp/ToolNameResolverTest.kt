package ru.eda.plgn.bizgen.mcp

import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

/**
 * Тесты [ToolNameResolver].
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("ToolNameResolver")
internal class ToolNameResolverTest {

  private val validMcpNameRegex = Regex("""^[a-zA-Z0-9_]{1,64}$""")

  @Nested
  @DisplayName("Все генераторы из GeneratorInfoProvider")
  inner class AllGeneratorsCases {

    @Test
    fun `Имена всех инструментов должны начинаться с generate_`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val name = ToolNameResolver.resolve(info)
        name shouldStartWith "generate_"
      }
    }

    @Test
    fun `Имена всех инструментов должны соответствовать правилам MCP`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val name = ToolNameResolver.resolve(info)
        name shouldMatch validMcpNameRegex
      }
    }

    @Test
    fun `Имена всех инструментов должны быть уникальными`() {
      val names = GeneratorInfoProvider.generatorInfos.map { ToolNameResolver.resolve(it) }
      names.shouldBeUnique()
    }

    @Test
    fun `Имена всех инструментов не должны превышать 64 символа`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val name = ToolNameResolver.resolve(info)
        name.length shouldBeInRange 1..64
      }
    }

    @Test
    fun `resolveAll не должен выбрасывать исключение для всех генераторов`() {
      val result = ToolNameResolver.resolveAll(GeneratorInfoProvider.generatorInfos)
      result.size shouldBe GeneratorInfoProvider.generatorInfos.size
    }
  }

  @Nested
  @DisplayName("Конкретные имена инструментов")
  inner class SpecificNamesCases {

    @Test
    fun `InnLegal должен резолвиться в generate_inn_legal`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("InnLegal_") }
      ToolNameResolver.resolve(info) shouldBe "generate_inn_legal"
    }

    @Test
    fun `InnIndividual должен резолвиться в generate_inn_individual`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("InnIndividual_") }
      ToolNameResolver.resolve(info) shouldBe "generate_inn_individual"
    }

    @Test
    fun `UUID должен резолвиться в generate_uuid`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("UUID_") }
      ToolNameResolver.resolve(info) shouldBe "generate_uuid"
    }

    @Test
    fun `AccountRub должен резолвиться в generate_account_rub`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("AccountRub_") }
      ToolNameResolver.resolve(info) shouldBe "generate_account_rub"
    }

    @Test
    fun `SwiftRu8 должен резолвиться в generate_swift_ru8`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("SwiftRu8_") }
      ToolNameResolver.resolve(info) shouldBe "generate_swift_ru8"
    }

    @Test
    fun `Oktmo8 должен резолвиться в generate_oktmo8`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("Oktmo8_") }
      ToolNameResolver.resolve(info) shouldBe "generate_oktmo8"
    }

    @Test
    fun `PhoneNumberRuFormat должен резолвиться в generate_phone_number_ru_format`() {
      val info = GeneratorInfoProvider.generatorInfos
        .first { it.id.startsWith("PhoneNumberRuFormat_") }
      ToolNameResolver.resolve(info) shouldBe "generate_phone_number_ru_format"
    }
  }

  @Nested
  @DisplayName("Кэширование и стабильность")
  inner class CachingCases {

    @Test
    fun `Повторный вызов resolve должен возвращать тот же результат`() {
      val info = GeneratorInfoProvider.generatorInfos.first()
      val first = ToolNameResolver.resolve(info)
      val second = ToolNameResolver.resolve(info)
      first shouldBe second
    }
  }

  @Nested
  @DisplayName("Обработка ошибок")
  inner class ErrorCases {

    @Test
    fun `Невалидный id без UUID должен выбрасывать исключение`() {
      val info = testInfo("InvalidIdWithoutUuid", "test-value")
      assertThrows<IllegalStateException> {
        ToolNameResolver.resolve(info)
      }
    }

    @Test
    fun `Пустой id должен выбрасывать исключение`() {
      val info = testInfo("", "test-value")
      assertThrows<IllegalStateException> {
        ToolNameResolver.resolve(info)
      }
    }
  }

  @Nested
  @DisplayName("Коллизии имён")
  inner class CollisionCases {

    @Test
    fun `resolveAll должен выбрасывать исключение при дубликатах`() {
      val info1 = testInfo("TestDup_aabbccdd-1111-2222-3333-444455556666", "value1")
      val info2 = testInfo("TestDup_bbccdd11-2222-3333-4444-555566667777", "value2")

      assertThrows<IllegalStateException> {
        ToolNameResolver.resolveAll(listOf(info1, info2))
      }
    }
  }

  private fun testInfo(id: String, value: String): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = id
      override val name: String = "Test"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 1
        override fun generate(): GeneratorResult<String> =
          GeneratorResult(value, value)
      }
    }
}
