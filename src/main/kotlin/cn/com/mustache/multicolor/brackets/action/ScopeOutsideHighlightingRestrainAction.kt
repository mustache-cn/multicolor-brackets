package cn.com.mustache.multicolor.brackets.action

import cn.com.mustache.multicolor.brackets.MulticolorInfo
import cn.com.mustache.multicolor.brackets.util.alphaBlend
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font
import java.util.*

class ScopeOutsideHighlightingRestrainAction : AbstractScopeHighlightingAction() {

    override fun Editor.addHighlighter(
        highlightManager: HighlightManager,
        multicolorInfo: cn.com.mustache.multicolor.brackets.MulticolorInfo
    ): Collection<RangeHighlighter> {
        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val background = Color.GRAY.alphaBlend(defaultBackground, 0.05f)
        val foreground = Color.GRAY.alphaBlend(defaultBackground, 0.55f)
        val attributes = TextAttributes(foreground, background, background, EffectType.BOXED, Font.PLAIN)
        val highlighters = LinkedList<RangeHighlighter>()

        val startOffset = multicolorInfo.startOffset
        if (startOffset > 0) {
            highlightManager.addRangeHighlight(
                this,
                0,
                startOffset,
                attributes, //create("ScopeOutsideHighlightingRestrainAction", attributes),
                false, //hideByTextChange
                false, //hideByAnyKey
                highlighters
            )
        }

        val endOffset = multicolorInfo.endOffset
        val lastOffset = document.textLength
        if (endOffset < lastOffset) {
            highlightManager.addRangeHighlight(
                this,
                endOffset,
                lastOffset,
                attributes, //create("ScopeOutsideHighlightingRestrainAction", attributes),
                false, //hideByTextChange
                false, //hideByAnyKey
                highlighters
            )
        }

        return highlighters
    }

}