package cn.com.mustache.multicolor.brackets.settings.form

import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class MulticolorSettingsForm {
    private var panel: JPanel? = null
    private var appearancePanel: JPanel? = null
    private var enableMulticolor: JCheckBox? = null
    private var enableMulticolorRoundBrackets: JCheckBox? = null
    private var enableMulticolorSquigglyBrackets: JCheckBox? = null
    private var enableMulticolorSquareBrackets: JCheckBox? = null
    private var enableMulticolorAngleBrackets: JCheckBox? = null
    private var showMulticolorIndentGuides: JCheckBox? = null

    private var numberOfColors: JTextField? = null

    private var doNOTMulticolorBigFiles: JCheckBox? = null

    private var bigFilesLinesThreshold: JTextField? = null

    private val settings: MulticolorSettings = MulticolorSettings.instance

    fun component(): JComponent? = panel

    fun isMulticolorEnabled() = enableMulticolor?.isSelected

    fun isMulticolorRoundBracketsEnabled() = enableMulticolorRoundBrackets?.isSelected

    fun isMulticolorSquigglyBracketsEnabled() = enableMulticolorSquigglyBrackets?.isSelected

    fun isMulticolorSquareBracketsEnabled() = enableMulticolorSquareBrackets?.isSelected

    fun isMulticolorAngleBracketsEnabled() = enableMulticolorAngleBrackets?.isSelected

    fun isShowMulticolorIndentGuides() = showMulticolorIndentGuides?.isSelected

    fun numberOfColors() = numberOfColors?.text?.toIntOrNull()

    fun doNOTMulticolorBigFiles() = doNOTMulticolorBigFiles?.isSelected

    fun bigFilesLinesThreshold() = bigFilesLinesThreshold?.text?.toIntOrNull()

    val isModified: Boolean
        get() = (isMulticolorEnabled() != settings.isMulticolorEnabled
                || isMulticolorAngleBracketsEnabled() != settings.isEnableMulticolorAngleBrackets
                || isMulticolorRoundBracketsEnabled() != settings.isEnableMulticolorRoundBrackets
                || isMulticolorSquigglyBracketsEnabled() != settings.isEnableMulticolorSquigglyBrackets
                || isMulticolorSquareBracketsEnabled() != settings.isEnableMulticolorSquareBrackets
                || isShowMulticolorIndentGuides() != settings.isShowMulticolorIndentGuides
                || numberOfColors() != settings.numberOfColors
                || doNOTMulticolorBigFiles() != settings.doNOTMulticolorifyBigFiles
                || bigFilesLinesThreshold() != settings.bigFilesLinesThreshold
                )

    init {
        loadSettings()
        doNOTMulticolorBigFiles?.addActionListener({ e ->
            bigFilesLinesThreshold?.setEnabled(
                doNOTMulticolorBigFiles() ?: false
            )
        })
    }

    fun loadSettings() {
        enableMulticolor?.isSelected = settings.isMulticolorEnabled
        enableMulticolorRoundBrackets?.isSelected = settings.isEnableMulticolorRoundBrackets
        enableMulticolorAngleBrackets?.isSelected = settings.isEnableMulticolorAngleBrackets
        enableMulticolorSquigglyBrackets?.isSelected = settings.isEnableMulticolorSquigglyBrackets
        enableMulticolorSquareBrackets?.isSelected = settings.isEnableMulticolorSquareBrackets
        showMulticolorIndentGuides?.isSelected = settings.isShowMulticolorIndentGuides
        numberOfColors?.text = settings.numberOfColors.toString()
        doNOTMulticolorBigFiles?.isSelected = settings.doNOTMulticolorifyBigFiles
        bigFilesLinesThreshold?.text = settings.bigFilesLinesThreshold.toString()
    }
}
