package ru.eda.plgn.bizgen.plugin.base.finder_ext

import com.intellij.driver.sdk.ui.Finder
import com.intellij.driver.sdk.ui.components.UiComponent

/**
 * Поиск [com.intellij.ui.InplaceButton] по accessible name.
 *
 * В отличие от [actionButtonByAccessibleNameAndPerform] (который ищет `ActionButton` из popup/toolbar), [com.intellij.ui.InplaceButton] —
 * отдельный класс, используемый в DSL-панелях (например, кнопка "Generate" в [ru.eda.plgn.bizgen.plugin.ui.ActionResultPreviewComponent]).
 *
 * @param accessibleName наименование кнопки
 * @return [UiComponent] для найденной кнопки
 */
fun Finder.inplaceButtonByAccessibleName(accessibleName: String): UiComponent =
  x { and(byType("com.intellij.ui.InplaceButton"), byAccessibleName(accessibleName)) }

/**
 * Поиск [com.intellij.ui.InplaceButton] по accessible name с последующим кликом.
 *
 * Использует [UiComponent.click] (Robot-клик), аналогично [actionButtonByAccessibleNameAndPerform] — полный Swing event cycle.
 *
 * @param accessibleName наименование кнопки
 */
fun Finder.inplaceButtonByAccessibleNameAndPerform(accessibleName: String): UiComponent =
  inplaceButtonByAccessibleName(accessibleName).apply { click() }
