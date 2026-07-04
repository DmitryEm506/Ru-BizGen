package ru.eda.plgn.bizgen.mcp

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator.Generator
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.GeneratorInfoProvider

/**
 * Тесты [ToolExecutor].
 *
 * @author Dmitry_Emelyanenko
 */
@DisplayName("ToolExecutor")
internal class ToolExecutorTest {

  @Nested
  @DisplayName("Успешное выполнение")
  inner class SuccessCases {

    @Test
    fun `Должен вернуть результат без ошибки для рабочего генератора`() {
      val info = workingGeneratorInfo()

      val result = ToolExecutor.execute(info)

      result.isError shouldNotBe true
      result.content.size shouldBe 1
      val text = (result.content.first() as TextContent).text
      text.shouldNotBeBlank()
    }

    @Test
    fun `Должен вернуть сгенерированное значение в content`() {
      val info = workingGeneratorInfo("expected-value")

      val result = ToolExecutor.execute(info)

      val text = (result.content.first() as TextContent).text
      text shouldBe "expected-value"
    }

    @Test
    fun `Все генераторы из GeneratorInfoProvider выполняются без ошибок`() {
      GeneratorInfoProvider.generatorInfos.forEach { info ->
        val result = ToolExecutor.execute(info)

        result.isError shouldNotBe true
        result.content.size shouldBe 1
        (result.content.first() as TextContent).text.shouldNotBeBlank()
      }
    }
  }

  @Nested
  @DisplayName("Обработка ошибок")
  inner class ErrorCases {

    @Test
    fun `Должен вернуть isError=true при выбросе исключения генератором`() {
      val info = failingGeneratorInfo(IllegalArgumentException("test error message"))

      val result = ToolExecutor.execute(info)

      result.isError shouldBe true
    }

    @Test
    fun `Должен вернуть сообщение об ошибке в content`() {
      val info = failingGeneratorInfo(IllegalArgumentException("test error message"))

      val result = ToolExecutor.execute(info)

      val text = (result.content.first() as TextContent).text
      text shouldContain "test error message"
    }

    @Test
    fun `Должен вернуть имя класса исключения при null message`() {
      val info = failingGeneratorInfo(RuntimeException())

      val result = ToolExecutor.execute(info)

      val text = (result.content.first() as TextContent).text
      text shouldContain "RuntimeException"
    }

    @Test
    fun `Должен вернуть content размером 1 при ошибке`() {
      val info = failingGeneratorInfo(IllegalStateException("fail"))

      val result = ToolExecutor.execute(info)

      result.content.size shouldBe 1
    }
  }

  private fun workingGeneratorInfo(value: String = "test-value"): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = "TestWorking_aabbccdd-1111-2222-3333-444455556666"
      override val name: String = "Test Working"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 1
        override fun generate(): GeneratorResult<String> =
          GeneratorResult(value, value)
      }
    }

  private fun failingGeneratorInfo(exception: RuntimeException): GeneratorInfo<String> =
    object : GeneratorInfo<String> {
      override val id: String = "TestFailing_aabbccdd-1111-2222-3333-444455556666"
      override val name: String = "Test Failing"
      override val generator: Generator<String> = object : Generator<String> {
        override val uniqueDistance: Int = 1
        override fun generate(): GeneratorResult<String> = throw exception
      }
    }
}
