package cn.com.mustache.multicolor.brackets.settings

import cn.com.mustache.multicolor.brackets.settings.form.MulticolorSettingsForm
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class MulticolorConfigurable : SearchableConfigurable {
    private var settingsForm: MulticolorSettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: MulticolorSettingsForm()
        return settingsForm?.component()
    }

    override fun isModified(): Boolean {
        return settingsForm?.isModified ?: return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = MulticolorSettings.instance
        settings.isMulticolorEnabled = settingsForm?.isMulticolorEnabled() ?: true
        settings.isEnableMulticolorRoundBrackets = settingsForm?.isMulticolorRoundBracketsEnabled() ?: true
        settings.isEnableMulticolorAngleBrackets = settingsForm?.isMulticolorAngleBracketsEnabled() ?: true
        settings.isEnableMulticolorSquigglyBrackets = settingsForm?.isMulticolorSquigglyBracketsEnabled() ?: true
        settings.isEnableMulticolorSquareBrackets = settingsForm?.isMulticolorSquareBracketsEnabled() ?: true
        settings.isShowMulticolorIndentGuides = settingsForm?.isShowMulticolorIndentGuides() ?: false
        settings.numberOfColors = settingsForm?.numberOfColors() ?: 5
        settings.doNOTMulticolorifyBigFiles = settingsForm?.doNOTMulticolorBigFiles() ?: true
        settings.bigFilesLinesThreshold = settingsForm?.bigFilesLinesThreshold() ?: 1000
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Multicolor Brackets"

    override fun getId(): String = ID

    companion object {
        val ID = "preferences.multicolor.brackets"
    }
}