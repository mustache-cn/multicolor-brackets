package cn.com.mustache.multicolor.brackets.annotator

import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.NAME_ANGLE_BRACKETS
import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.NAME_ROUND_BRACKETS
import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.NAME_SQUARE_BRACKETS
import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.NAME_SQUIGGLY_BRACKETS
import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.getMulticolorColorByLevel
import cn.com.mustache.multicolor.brackets.annotator.MulticolorUtils.annotateUtil
import cn.com.mustache.multicolor.brackets.annotator.MulticolorUtils.settings
import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement

class MulticolorAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (settings.isMulticolorEnabled && element is LeafPsiElement) {
            if (settings.isEnableMulticolorRoundBrackets) annotateUtil(element, holder, "(", ")", NAME_ROUND_BRACKETS)
            if (settings.isEnableMulticolorSquareBrackets) annotateUtil(
                element,
                holder,
                "[",
                "]",
                NAME_SQUARE_BRACKETS
            )
            if (settings.isEnableMulticolorSquigglyBrackets) annotateUtil(
                element,
                holder,
                "{",
                "}",
                NAME_SQUIGGLY_BRACKETS
            )
            if (settings.isEnableMulticolorAngleBrackets) annotateUtil(element, holder, "<", ">", NAME_ANGLE_BRACKETS)
        }
    }
}


object MulticolorUtils {

    private val leftBracketsSet = setOf("(", "[", "{", "<")
    private val rightBracketsSet = setOf(")", "]", "}", ">")

    val settings = MulticolorSettings.instance

    private tailrec fun iterateChildren(
        LEFT: String,
        RIGHT: String,
        currentNode: PsiElement,
        currentLevel: Int,
        currentChild: PsiElement
    ): Int {
        val calculatedLevel = if (currentChild is LeafPsiElement) {
            //Using `currentChild.elementType.toString()` if we didn't want add more dependencies.
            when {
                leftBracketsSet.contains(currentChild.text) -> currentLevel + 1
                rightBracketsSet.contains(currentChild.text) -> currentLevel - 1
                else -> currentLevel
            }
        } else currentLevel

        return if ((currentChild != currentNode) && (currentChild != currentNode.parent.lastChild))
            iterateChildren(LEFT, RIGHT, currentNode, calculatedLevel, currentChild.nextSibling)
        else
            calculatedLevel
    }

    private tailrec fun iterateParents(
        LEFT: String,
        RIGHT: String,
        currentNode: PsiElement,
        currentLevel: Int
    ): Int = if (currentNode.parent !is PsiFile) {
        val calculatedLevel = iterateChildren(LEFT, RIGHT, currentNode, currentLevel, currentNode.parent.firstChild)
        iterateParents(LEFT, RIGHT, currentNode.parent, calculatedLevel)
    } else currentLevel

    private fun getBracketLevel(element: LeafPsiElement, LEFT: String, RIGHT: String): Int {
        //Using `element.elementType.toString()` if we didn't want add more dependencies.
        val startLevel = if (element.text == RIGHT) 0 else -1
        return iterateParents(LEFT, RIGHT, element, startLevel)
    }

    fun annotateUtil(
        element: LeafPsiElement, holder: AnnotationHolder,
        LEFT: String, RIGHT: String, multicolorName: String
    ) {
        //Using `element.elementType.toString()` if we didn't want add more dependencies.
        val level = when (element.text) {
            LEFT, RIGHT -> getBracketLevel(element, LEFT, RIGHT)
            else -> -1
        }
        val multicolorColorByLevel = getMulticolorColorByLevel(multicolorName, level)
        if (multicolorColorByLevel != null) {
            if (level >= 0) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.psi)
                    .textAttributes(multicolorColorByLevel)
                    .create()
            }
        }
    }
}