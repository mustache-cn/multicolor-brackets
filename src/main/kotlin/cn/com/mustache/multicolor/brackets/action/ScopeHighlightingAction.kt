package cn.com.mustache.multicolor.brackets.action

import cn.com.mustache.multicolor.brackets.MulticolorInfo
import cn.com.mustache.multicolor.brackets.util.alphaBlend
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Font
import java.util.*


class ScopeHighlightingAction : AbstractScopeHighlightingAction() {

    override fun Editor.addHighlighter(
        highlightManager: HighlightManager,
        multicolorInfo: cn.com.mustache.multicolor.brackets.MulticolorInfo
    ): Collection<RangeHighlighter> {
        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val background = multicolorInfo.color.alphaBlend(defaultBackground, 0.2f)
        val attributes = TextAttributes(null, background, multicolorInfo.color, EffectType.BOXED, Font.PLAIN)
        val highlighters = LinkedList<RangeHighlighter>()
        highlightManager.addRangeHighlight(
            this,
            multicolorInfo.startOffset,
            multicolorInfo.endOffset,
            attributes, //create("ScopeHighlightingAction", attributes),
            false, //hideByTextChange
            false, //hideByAnyKey
            highlighters
        )

        return highlighters
    }

}