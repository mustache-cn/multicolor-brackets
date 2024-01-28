package cn.com.mustache.multicolor.brackets.settings

import cn.com.mustache.multicolor.brackets.MulticolorHighlighter
import cn.com.mustache.multicolor.brackets.settings.form.MulticolorOptionsPanel
import com.intellij.application.options.colors.*
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable


class MulticolorColorsPageFactory : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider, DisplayPrioritySortable {

    override fun getDisplayName(): String = MULTICOLOR_BRACKETS_GROUP

    override fun getPanelDisplayName(): String = MULTICOLOR_BRACKETS_GROUP

    override fun getPriority(): DisplayPriority = DisplayPriority.COMMON_SETTINGS

    override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
        val emptyPreview = PreviewPanel.Empty()
        val schemesPanel = SchemesPanel(options)
        val optionsPanel = MulticolorOptionsPanel(options, schemesPanel, MULTICOLOR_BRACKETS_GROUP)

        schemesPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun schemeChanged(source: Any) {
                optionsPanel.updateOptionsList()
            }
        })

        return NewColorAndFontPanel(schemesPanel, optionsPanel, emptyPreview, MULTICOLOR_BRACKETS_GROUP, null, null)
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()

    companion object {
        private const val MULTICOLOR_BRACKETS_GROUP = "Multicolor Brackets"
        private val ATTRIBUTE_DESCRIPTORS: Array<AttributesDescriptor> by lazy {
            createDescriptors(MulticolorHighlighter.NAME_ROUND_BRACKETS) +
                    createDescriptors(MulticolorHighlighter.NAME_SQUARE_BRACKETS) +
                    createDescriptors(MulticolorHighlighter.NAME_SQUIGGLY_BRACKETS) +
                    createDescriptors(MulticolorHighlighter.NAME_ANGLE_BRACKETS)
        }

        private fun createDescriptors(name: String): Array<AttributesDescriptor> {
            return MulticolorHighlighter.getMulticolorAttributesKeys(name)
                .map { key -> AttributesDescriptor("$name:$key", key) }
                .toTypedArray()
        }
    }

}