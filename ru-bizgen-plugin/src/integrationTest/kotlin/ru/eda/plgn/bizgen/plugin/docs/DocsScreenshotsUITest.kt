package ru.eda.plgn.bizgen.plugin.docs

import com.intellij.driver.client.Driver
import com.intellij.driver.client.Remote
import com.intellij.driver.model.OnDispatcher
import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.openFile
import com.intellij.driver.sdk.ui.components.common.editor
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.list
import com.intellij.driver.sdk.ui.components.elements.popup
import com.intellij.driver.sdk.ui.components.elements.waitForNoOpenedDialogs
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.present
import com.intellij.driver.sdk.ui.remote.ColorRef
import com.intellij.driver.sdk.ui.remote.Component
import com.intellij.driver.sdk.ui.remote.Window
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitFor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import ru.eda.plgn.bizgen.plugin.base.BaseUIIntegrationTest
import javax.swing.JList
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Генератор иллюстраций для README и страницы плагина в Marketplace.
 *
 * Это не проверка поведения, а инструмент документации: поднимает IDE с установленным плагином,
 * открывает нужные экраны и сохраняет их изображения в каталог из system property
 * `bizgen.docs.img.dir` (по умолчанию `../.github/img`).
 *
 * Запуск: `./gradlew :ru-bizgen-plugin:docsScreenshots` (каталог переопределяется через
 * `-PdocsImgDir=<путь>`). В `check` не входит и в ночной `integrationTest` не попадает — задача
 * отдельная, потому что переснимать картинки нужно перед релизом, а не на каждый PR.
 *
 * **Кадр не снимается с экрана.** `Driver.takeScreenshot` фотографирует физический монитор целиком,
 * то есть всё, что в этот момент открыто у разработчика, и промахивается, если окно тестовой IDE
 * не поверх остальных. Вместо этого компонент отрисовывается в offscreen-картинку прямо внутри JVM
 * тестовой IDE (`Component.paint` в EDT) и оттуда же пишется на диск. Отсюда три следствия: машиной
 * во время съёмки можно пользоваться, кадр не зависит от разрешения монитора и содержит ровно
 * нужный компонент без чужих окон.
 *
 * Ограничения: гифки (`main.gif`, `work-sample.gif`) так не снимаются — там анимация. Кадры зависят
 * от системных шрифтов, поэтому картинки для Marketplace снимаем локально, а не на Linux-раннере.
 *
 * @author Dmitry_Emelyanenko
 */
internal class DocsScreenshotsUITest : BaseUIIntegrationTest() {

  /**
   * Снимает оба кадра за один запуск IDE: попап генераторов и экран настроек.
   *
   * Один запуск вместо двух — старт IDE занимает больше времени, чем сами сценарии.
   */
  @Test
  internal fun `Should Driver UI - capture README screenshots`(@TempDir projectDir: Path) {
    Files.writeString(projectDir.resolve(SAMPLE_FILE_NAME), SAMPLE_FILE_CONTENT)

    val outputDir = resolveOutputDir()
    Files.createDirectories(outputDir)

    runIdea("docsScreenshots", projectDir, runTimeout = 5.minutes) {
      ideFrame {
        // Фиксированный размер окна: от него зависят размеры попапа и диалога настроек,
        // иначе композиция кадра едет вслед за монитором, на котором его сняли.
        driver.cast(component, Window::class).setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT)

        driver.openFile(SAMPLE_FILE_NAME, waitForCodeAnalysis = false)
        driver.invokeAction("ru.eda.plgn.bizgen.BizGenMainAction")

        val generatorsPopup = popup()
        generatorsPopup.shouldBe("Ru BizGen popup is not present", present)

        // Проверяем, что в попап поместились все генераторы: высота попапа ограничена окном,
        // и на маленьком экране список ушёл бы под скроллбар с обрезанной последней строкой.
        val generatorsList = generatorsPopup.list { byType(JList::class.java) }
        val listHeight = generatorsList.component.getBounds().height
        val popupHeight = generatorsPopup.component.getBounds().height
        if (listHeight > popupHeight) {
          fail {
            "Popup is too short for the whole generator list ($listHeight > $popupHeight). " +
              "Increase FRAME_HEIGHT or capture the list itself."
          }
        }

        // Кадр собирается из двух слоёв: область редактора и попап поверх неё (попап — отдельное
        // окно, в отрисовку редактора он не попадает). Рамку задаёт попап, редактор обрезается
        // по ней: так в кадр не лезут ни имя временного проекта, ни тулбар с бейджем лицензии,
        // ни панели инструментов, а вокруг попапа остаётся полоска кода.
        driver.paintToFile(
          target = outputDir.resolve("generators.png"),
          focus = generatorsPopup.component,
          margin = POPUP_MARGIN,
          layers = arrayOf(editor().component, generatorsPopup.component),
        )

        keyboard { escape() }

        openSettingsDialog()
        settingsDialog {
          openTreeSettingsSection("Tools", "Ru BizGen")

          // Диалог открывается в размере по умолчанию, в котором список генераторов режется
          // по ширине ("Организация. Русское наименов..."). Расширяем окно перед съёмкой.
          driver.cast(component, Window::class).setBounds(0, 0, DIALOG_WIDTH, DIALOG_HEIGHT)

          // Ширины окна мало: сплиттер отдаёт списку DEFAULT_SPLITTER_PROPORTION = 0.25, и длинные
          // имена всё равно обрезаются. Двигаем разделитель — ровно то же, что пользователь делает
          // мышью; на сохранённые настройки не влияет, диалог закрывается по Cancel.
          // Поиск идёт внутри content: на уровне диалога таких сплиттеров два — свой есть
          // и у самого экрана настроек (дерево слева / страница справа).
          content {
            val generatorList = list { byType(JList::class.java) }
            val widthBeforeSplit = generatorList.component.getBounds().width

            driver.cast(x { byType(SPLITTER_CLASS) }.component, SplitterRef::class)
              .setProportion(DOCS_SPLITTER_PROPORTION)

            waitFor("Generator list should get wider after moving the splitter", 10.seconds) {
              generatorList.component.getBounds().width > widthBeforeSplit
            }
          }

          driver.paintToFile(
            target = outputDir.resolve("settings.png"),
            focus = component,
            margin = PADDING,
            layers = arrayOf(component),
          )
          cancelButton.click()
        }
        waitForNoOpenedDialogs()
      }
    }
  }

  /**
   * Отрисовывает [layers] в offscreen-картинку внутри JVM тестовой IDE и сохраняет её в [target].
   *
   * Всё выполняется на стороне IDE через `@Remote`-прокси: создаётся `BufferedImage`, заливается
   * фоном первого слоя, затем `Component.paint` рисует в него дерево Swing. Рисование идёт в EDT
   * ([OnDispatcher.EDT]) — вне него Swing даёт артефакты.
   *
   * Рамку кадра задаёт [focus] плюс поля [margin]; слои рисуются по своим экранным координатам и
   * обрезаются этой рамкой, поэтому крупный слой (например, весь редактор) попадает в кадр только
   * тем куском, что вокруг фокуса.
   *
   * Масштаб [SCALE] — картинка рисуется крупнее логического размера, чтобы не мылила на HiDPI.
   *
   * @param target путь итогового PNG
   * @param focus компонент, задающий рамку кадра
   * @param margin поля вокруг [focus] в логических пикселях
   * @param layers компоненты снизу вверх
   */
  private fun Driver.paintToFile(target: Path, focus: Component, margin: Int, layers: Array<Component>) {
    val focusBounds = focus.getBounds()
    if (focusBounds.width <= 0 || focusBounds.height <= 0) {
      fail { "Component for ${target.fileName} has empty bounds: ${focusBounds.width}x${focusBounds.height}" }
    }

    val canvasWidth = focusBounds.width + margin * 2
    val canvasHeight = focusBounds.height + margin * 2
    val focusLocation = focus.getLocationOnScreen()
    val originX = focusLocation.x - margin
    val originY = focusLocation.y - margin

    withContext(OnDispatcher.EDT) {
      val image = new(BufferedImageRef::class, canvasWidth * SCALE, canvasHeight * SCALE, TYPE_INT_RGB)
      val graphics = image.createGraphics()

      graphics.scale(SCALE.toDouble(), SCALE.toDouble())
      graphics.setColor(layers.first().getBackground())
      graphics.fillRect(0, 0, canvasWidth, canvasHeight)

      layers.forEach { layer ->
        val location = layer.getLocationOnScreen()
        val offsetX = location.x - originX
        val offsetY = location.y - originY

        graphics.translate(offsetX, offsetY)
        cast(layer, PaintableComponentRef::class).paint(graphics)
        graphics.translate(-offsetX, -offsetY)
      }

      graphics.dispose()

      utility(ImageIORef::class).write(image, "png", new(FileRef::class, target.toAbsolutePath().toString()))
    }

    val size = if (Files.exists(target)) Files.size(target) else 0L
    if (size < MIN_PNG_SIZE_BYTES) {
      fail { "Screenshot $target looks empty ($size bytes)" }
    }
    println("[docs-screenshots] ${target.toAbsolutePath()}: ${canvasWidth * SCALE}x${canvasHeight * SCALE}, $size bytes")
  }

  /**
   * Каталог для готовых картинок: system property `bizgen.docs.img.dir` (выставляется gradle-задачей
   * `docsScreenshots`), fallback — `.github/img` относительно модуля (ручной запуск из IDE).
   */
  private fun resolveOutputDir(): Path =
    Path.of(System.getProperty("bizgen.docs.img.dir") ?: "../.github/img")

  private companion object {
    /** Файл-фикстура за попапом: код на фоне делает кадр узнаваемым. */
    const val SAMPLE_FILE_NAME = "Main.java"

    val SAMPLE_FILE_CONTENT = """
      public class Main {

        public static void main(String[] args) {

        }
      }
    """.trimIndent()

    /** Размер окна IDE в логических пикселях — общий для всех кадров. */
    const val FRAME_WIDTH = 1600
    const val FRAME_HEIGHT = 1300

    /** Размер диалога настроек: шире окна по умолчанию, чтобы имена генераторов не резались. */
    const val DIALOG_WIDTH = 1280
    const val DIALOG_HEIGHT = 860

    /** Сплиттер настроек: в проде 0.25, для кадра список нужен шире. */
    const val SPLITTER_CLASS = "com.intellij.ui.OnePixelSplitter"
    const val DOCS_SPLITTER_PROPORTION = 0.42f

    /** Поля вокруг компонента, чтобы кадр не обрезался впритык. */
    const val PADDING = 12

    /** Поля вокруг попапа: в них видно код редактора под ним. */
    const val POPUP_MARGIN = 90

    /** Во сколько раз картинка крупнее логического размера компонента. */
    const val SCALE = 2

    /** `BufferedImage.TYPE_INT_RGB` — константа передаётся числом, в IDE её негде взять. */
    const val TYPE_INT_RGB = 1

    /** Ниже этого размера PNG заведомо пустой — значит, отрисовка не удалась. */
    const val MIN_PNG_SIZE_BYTES = 4_096L
  }
}

/** `java.awt.image.BufferedImage` в JVM тестовой IDE. */
@Remote("java.awt.image.BufferedImage")
internal interface BufferedImageRef {
  fun createGraphics(): Graphics2DRef
}

/** `java.awt.Graphics2D` в JVM тестовой IDE. */
@Remote("java.awt.Graphics2D")
internal interface Graphics2DRef {
  fun scale(sx: Double, sy: Double)
  fun translate(x: Int, y: Int)
  fun setColor(color: ColorRef)
  fun fillRect(x: Int, y: Int, width: Int, height: Int)
  fun dispose()
}

/**
 * `java.awt.Component.paint` — в `com.intellij.driver.sdk.ui.remote.Component` этот метод
 * не объявлен, поэтому здесь отдельный прокси к тому же классу.
 */
@Remote("java.awt.Component")
internal interface PaintableComponentRef {
  fun paint(graphics: Graphics2DRef)
}

/** `javax.imageio.ImageIO` — запись PNG выполняется в JVM тестовой IDE. */
@Remote("javax.imageio.ImageIO")
internal interface ImageIORef {
  fun write(image: BufferedImageRef, formatName: String, output: FileRef): Boolean
}

/** `java.io.File` в JVM тестовой IDE. */
@Remote("java.io.File")
internal interface FileRef

/** `com.intellij.openapi.ui.Splitter` — разделитель списка генераторов и правой панели настроек. */
@Remote("com.intellij.openapi.ui.Splitter")
internal interface SplitterRef {
  fun setProportion(proportion: Float)
}
