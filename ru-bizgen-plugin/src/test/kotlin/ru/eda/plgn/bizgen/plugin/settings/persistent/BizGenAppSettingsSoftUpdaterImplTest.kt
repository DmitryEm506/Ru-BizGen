package ru.eda.plgn.bizgen.plugin.settings.persistent

import com.intellij.openapi.Disposable
import com.intellij.testFramework.junit5.TestDisposable
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.mockk.every
import org.junit.jupiter.api.Test
import ru.eda.plgn.bizgen.core.generator.GeneratorResult
import ru.eda.plgn.bizgen.core.generator.GeneratorResultAsIs
import ru.eda.plgn.bizgen.core.generator.GeneratorStr
import ru.eda.plgn.bizgen.core.generator_info.GeneratorCategory
import ru.eda.plgn.bizgen.core.generator_info.GeneratorStrInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.BikGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.CountryGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.KppGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.SnilsGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.UuidGeneratorInfo
import ru.eda.plgn.bizgen.core.generator_info.impl.ogrn.OgrnIpGeneratorInfo
import ru.eda.plgn.bizgen.plugin.BaseIdeaTest
import ru.eda.plgn.bizgen.plugin.actions.GeneratorActionProvider
import ru.eda.plgn.bizgen.plugin.settings.model.BizGenAppSettings.PersistenceActionSetting

/**
 * Интеграционные тесты для [BizGenAppSettingsSoftUpdater].
 *
 * @author Dmitry_Emelyanenko
 */
internal class BizGenAppSettingsSoftUpdaterTest : BaseIdeaTest() {
  private val underTest: BizGenAppSettingsSoftUpdater = BizGenAppSettingsSoftUpdaterImpl()

  @Test
  internal fun `Should update saved actions from actual actions`(@TestDisposable disposable: Disposable) {
    // given
    // 0) UUID - не надо обновлять (активно = true), 1) KPP - изменилось имя, 2) Snils - удаляется,
    // 3) BIK - не надо обновлять (активно = false), 4) COUNTRY - удаляется, 5) OGRN - не надо обновлять (активно = false),
    // 6) TEST_1 - новое, 7) TEST_2 - новое
    val oldActionSettings = mutableListOf(kppPAS, snilsPAS, ogrnPAS, uuidPAS, bikPAS, countryPAS)

    // and
    val actioProvider = replaceServiceInApp<GeneratorActionProvider>(disposable)
    every { actioProvider.getInfos() } returns listOf(
      UuidGeneratorInfo(),
      KppGeneratorInfo(),
      BikGeneratorInfo(),
      OgrnIpGeneratorInfo(),
      Test2GeneratorInfo(),
      Test1GeneratorInfo(),
    )

    // when
    val result = underTest.softUpdateActions(oldActionSettings)

    // then
    result shouldContainExactlyInAnyOrder listOf(
      PersistenceActionSetting(id = UuidGeneratorInfo().id, position = 0, description = "UUID как строка", active = true),
      PersistenceActionSetting(id = KppGeneratorInfo().id, position = 1, description = "КПП (9)", active = true),
      PersistenceActionSetting(id = BikGeneratorInfo().id, position = 2, description = "БИК (9)", active = false),
      PersistenceActionSetting(id = OgrnIpGeneratorInfo().id, position = 3, description = "ОГРН ИП (15)", active = false),
      PersistenceActionSetting(id = Test2GeneratorInfo().id, position = 4, description = "TEST2_GENERATOR", active = true),
      PersistenceActionSetting(id = Test1GeneratorInfo().id, position = 5, description = "TEST1_GENERATOR", active = true),
    )
  }

  private val uuidPAS = PersistenceActionSetting(id = UuidGeneratorInfo().id, position = 0, description = "UUID как строка", active = true)
  private val kppPAS = PersistenceActionSetting(id = KppGeneratorInfo().id, position = 1, description = "OLD_KPP", active = true)
  private val snilsPAS = PersistenceActionSetting(id = SnilsGeneratorInfo().id, position = 2, description = "SNILS", active = true)
  private val bikPAS = PersistenceActionSetting(id = BikGeneratorInfo().id, position = 3, description = "БИК (9)", active = false)
  private val countryPAS = PersistenceActionSetting(id = CountryGeneratorInfo().id, position = 4, description = "COUNTRY", active = false)
  private val ogrnPAS = PersistenceActionSetting(id = OgrnIpGeneratorInfo().id, position = 5, description = "ОГРН ИП (15)", active = false)

  private class Test1GeneratorInfo : GeneratorStrInfo(
    id = "TEST_1",
    name = "TEST1_GENERATOR",
    generator = TestGenerator(),
    category = GeneratorCategory.TECHNICAL,
    detailedDescription = "Test generator 1",
    example = "_TEST_"
  )
  private class Test2GeneratorInfo : GeneratorStrInfo(
    id = "TEST_2",
    name = "TEST2_GENERATOR",
    generator = TestGenerator(),
    category = GeneratorCategory.TECHNICAL,
    detailedDescription = "Test generator 2",
    example = "_TEST_"
  )
  private class TestGenerator : GeneratorStr {
    override val uniqueDistance: Int = 10

    override fun generate(): GeneratorResult<String> = GeneratorResultAsIs(data = "_TEST_")
  }
}