package cn.com.mustache.multicolor.brackets

import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly
import java.awt.Color

object MulticolorHighlighter {

    const val NAME_ROUND_BRACKETS = "Round Brackets"
    const val NAME_SQUARE_BRACKETS = "Square Brackets"
    const val NAME_SQUIGGLY_BRACKETS = "Squiggly Brackets"
    const val NAME_ANGLE_BRACKETS = "Angle Brackets"

    private const val KEY_ROUND_BRACKETS = "ROUND_BRACKETS_MULTICOLOR_COLOR"
    private const val KEY_SQUARE_BRACKETS = "SQUARE_BRACKETS_MULTICOLOR_COLOR"
    private const val KEY_SQUIGGLY_BRACKETS = "SQUIGGLY_BRACKETS_MULTICOLOR_COLOR"
    private const val KEY_ANGLE_BRACKETS = "ANGLE_BRACKETS_MULTICOLOR_COLOR"

    private val roundBrackets: CharArray = charArrayOf('(', ')')
    private val squareBrackets: CharArray = charArrayOf('[', ']')
    private val squigglyBrackets: CharArray = charArrayOf('{', '}')
    private val angleBrackets: CharArray = charArrayOf('<', '>')

    private val settings = MulticolorSettings.instance

    private val roundBracketsMulticolorColorKeys: Array<TextAttributesKey> =
        createMulticolorAttributesKeys(KEY_ROUND_BRACKETS, settings.numberOfColors)
    private val squareBracketsMulticolorColorKeys: Array<TextAttributesKey> =
        createMulticolorAttributesKeys(KEY_SQUARE_BRACKETS, settings.numberOfColors)
    private val squigglyBracketsMulticolorColorKeys: Array<TextAttributesKey> =
        createMulticolorAttributesKeys(KEY_SQUIGGLY_BRACKETS, settings.numberOfColors)
    private val angleBracketsMulticolorColorKeys: Array<TextAttributesKey> =
        createMulticolorAttributesKeys(KEY_ANGLE_BRACKETS, settings.numberOfColors)

    private val multicolorElement: HighlightInfoType = HighlightInfoType
        .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    private val PsiElement.isRoundBracket get() = roundBrackets.any { textContains(it) }
    private val PsiElement.isSquareBracket get() = squareBrackets.any { textContains(it) }
    private val PsiElement.isSquigglyBracket get() = squigglyBrackets.any { textContains(it) }
    private val PsiElement.isAngleBracket get() = angleBrackets.any { textContains(it) }

    private fun createMulticolorAttributesKeys(keyName: String, size: Int): Array<TextAttributesKey> {
        return generateSequence(0) { it + 1 }
            .map { TextAttributesKey.createTextAttributesKey("$keyName$it") }
            .take(size)
            .toList()
            .toTypedArray()
    }

    fun getMulticolorAttributesKeys(multicolorName: String): Array<TextAttributesKey> {
        return when (multicolorName) {
            NAME_ROUND_BRACKETS -> roundBracketsMulticolorColorKeys
            NAME_SQUARE_BRACKETS -> squareBracketsMulticolorColorKeys
            NAME_SQUIGGLY_BRACKETS -> squigglyBracketsMulticolorColorKeys
            NAME_ANGLE_BRACKETS -> angleBracketsMulticolorColorKeys
            else -> throw IllegalArgumentException("Unknown multicolor name: $multicolorName")
        }
    }

    // FIXME: Meta properties(SchemeMetaInfo) should be used.
    fun isMulticolorEnabled(multicolorName: String): Boolean {
        return when (multicolorName) {
            NAME_ROUND_BRACKETS -> settings.isEnableMulticolorRoundBrackets
            NAME_SQUARE_BRACKETS -> settings.isEnableMulticolorSquareBrackets
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableMulticolorSquigglyBrackets
            NAME_ANGLE_BRACKETS -> settings.isEnableMulticolorAngleBrackets
            else -> throw IllegalArgumentException("Unknown multicolor name: $multicolorName")
        }
    }

    // FIXME: Meta properties(SchemeMetaInfo) should be used.
    fun setMulticolorEnabled(multicolorName: String, enabled: Boolean) {
        when (multicolorName) {
            NAME_ROUND_BRACKETS -> settings.isEnableMulticolorRoundBrackets = enabled
            NAME_SQUARE_BRACKETS -> settings.isEnableMulticolorSquareBrackets = enabled
            NAME_SQUIGGLY_BRACKETS -> settings.isEnableMulticolorSquigglyBrackets = enabled
            NAME_ANGLE_BRACKETS -> settings.isEnableMulticolorAngleBrackets = enabled
            else -> throw IllegalArgumentException("Unknown multicolor name: $multicolorName")
        }
    }

    fun getMulticolorColorByLevel(multicolorName: String, level: Int): TextAttributesKey? {
        val ind = level % settings.numberOfColors
        return getMulticolorAttributesKeys(multicolorName).getOrNull(ind)
    }

    @TestOnly
    fun getBrackets(): CharArray = roundBrackets + squareBrackets + squigglyBrackets + angleBrackets

    @TestOnly
    fun getMulticolorColor(multicolorName: String, level: Int): Color? {
        return getMulticolorColorByLevel(multicolorName, level)?.defaultAttributes?.foregroundColor
    }

    private fun getTextAttributes(
        element: PsiElement,
        level: Int
    ): TextAttributesKey? {
        if (!settings.isMulticolorEnabled) {
            return null
        }

        val multicolorName = when {
            element.isRoundBracket -> if (settings.isEnableMulticolorRoundBrackets) NAME_ROUND_BRACKETS else null
            element.isSquareBracket -> if (settings.isEnableMulticolorSquareBrackets) NAME_SQUARE_BRACKETS else null
            element.isSquigglyBracket -> if (settings.isEnableMulticolorSquigglyBrackets) NAME_SQUIGGLY_BRACKETS else null
            element.isAngleBracket -> if (settings.isEnableMulticolorAngleBrackets) NAME_ANGLE_BRACKETS else null
            else -> NAME_ROUND_BRACKETS
        } ?: return null

        return getMulticolorColorByLevel(multicolorName, level)
    }

    fun getHighlightInfo(colorsScheme: TextAttributesScheme, element: PsiElement, level: Int)
            : HighlightInfo? = getTextAttributes(element, level)
        ?.let { attr ->
            HighlightInfo
                .newHighlightInfo(multicolorElement)
                .textAttributes(attr)
                .range(element)
                .create()
        }
}
