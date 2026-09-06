package ru.eda.plgn.bizgen.plugin.base.finder_ext

import com.intellij.driver.sdk.ui.Finder
import com.intellij.driver.sdk.ui.components.elements.JRadioButtonUi
import com.intellij.driver.sdk.ui.components.elements.radioButton
import javax.swing.JRadioButton

/**
 * Расширение для поиска JRadioButton по их тексту.
 *
 * @param text текст [JRadioButton]
 */
fun Finder.radioButtonByVisibleText(text: String): JRadioButtonUi = radioButton {
  and(byType(JRadioButton::class.java), byVisibleText(text))
}