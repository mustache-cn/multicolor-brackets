package cn.com.mustache.multicolor.brackets.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "MulticolorSettings", storages = [(Storage("multicolor_brackets.xml"))])
class MulticolorSettings : PersistentStateComponent<MulticolorSettings> {
    /**
     * default value
     */
    var isMulticolorEnabled = true
    var isEnableMulticolorRoundBrackets = true
    var isEnableMulticolorSquigglyBrackets = true
    var isEnableMulticolorSquareBrackets = true
    var isEnableMulticolorAngleBrackets = true
    var isShowMulticolorIndentGuides = true

    var numberOfColors = 5

    var doNOTMulticolorifyBigFiles = true
    var bigFilesLinesThreshold = 1000


    var suppressDisabledCheck = false
    var suppressBigFileCheck = false
    var suppressBlackListCheck = false

    @Nullable
    override fun getState() = this

    override fun loadState(state: MulticolorSettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: MulticolorSettings
            get() = ApplicationManager.getApplication().getService(MulticolorSettings::class.java)
    }
}