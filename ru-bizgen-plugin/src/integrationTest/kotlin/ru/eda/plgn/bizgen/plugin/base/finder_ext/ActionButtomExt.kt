package ru.eda.plgn.bizgen.plugin.base.finder_ext

import com.intellij.driver.sdk.ui.Finder
import com.intellij.driver.sdk.ui.components.elements.actionButton

/**
 * Поиск [com.intellij.driver.sdk.ui.components.elements.ActionButtonUi] по наименованию
 * с последующим кликом.
 *
 * Используется [UiComponent.click] (Robot-клик), а не [ActionButtonUi.performAction]:
 * Robot-клик проходит полный Swing event cycle (mouse press → release → mouse handler →
 * `actionPerformed`) и сохраняет выделение в `CheckBoxList`.
 *
 * @param accessibleName Наименование кнопки
 */
fun Finder.actionButtonByAccessibleNameAndPerform(accessibleName: String) =
  actionButton { and(byClass("ActionButton"), byAccessibleName(accessibleName)) }
    .apply { click() }
