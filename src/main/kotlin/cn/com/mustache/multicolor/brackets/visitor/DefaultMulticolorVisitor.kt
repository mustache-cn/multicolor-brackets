package cn.com.mustache.multicolor.brackets.visitor

import cn.com.mustache.multicolor.brackets.bracePairs
import cn.com.mustache.multicolor.brackets.braceTypeSet
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType


class DefaultMulticolorVisitor : MulticolorHighlightVisitor() {

    override fun clone(): HighlightVisitor = DefaultMulticolorVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return
        val matching = filterPairs(type, element) ?: return

        val pair =
            if (matching.size == 1) {
                matching[0]
            } else {
                matching.find { element.isValidBracket(it) }
            } ?: return

        val level = element.getBracketLevel(pair)
        if (level >= 0) {
            multicolorPairs(element, pair, level)
        }
    }

    private fun multicolorPairs(element: LeafPsiElement, pair: BracePair, level: Int) {
        val startElement = element.takeIf { it.elementType == pair.leftBraceType }
        val endElement = element.takeIf { it.elementType == pair.rightBraceType }
        element.setHighlightInfo(element.parent, level, startElement, endElement)
    }

    companion object {

        private fun LeafPsiElement.getBracketLevel(pair: BracePair): Int = iterateBracketParents(parent, pair, -1)

        private tailrec fun iterateBracketParents(element: PsiElement?, pair: BracePair, count: Int): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count
            if (element.haveBrackets({ element.language.braceTypeSet.contains(it.elementType()) },
                    { element.language.braceTypeSet.contains(it.elementType()) })
            ) {
                nextCount++
            }

            return iterateBracketParents(element.parent, pair, nextCount)
        }

        private inline fun PsiElement.haveBrackets(
            checkLeft: (PsiElement) -> Boolean,
            checkRight: (PsiElement) -> Boolean
        ): Boolean {
            if (this is LeafPsiElement) {
                return false
            }

            var findLeftBracket = false
            var findRightBracket = false
            var left: PsiElement? = firstChild
            var right: PsiElement? = lastChild
            while (left != right && (!findLeftBracket || !findRightBracket)) {
                val needBreak = left == null || left.nextSibling == right

                if (left is LeafPsiElement && checkLeft(left)) {
                    findLeftBracket = true
                } else {
                    left = left?.nextSibling
                }
                if (right is LeafPsiElement && checkRight(right)) {
                    findRightBracket = true
                } else {
                    right = right?.prevSibling
                }

                if (needBreak) {
                    break
                }
            }

            return findLeftBracket && findRightBracket
        }

        private fun PsiElement.elementType(): IElementType? {
            return (this as? LeafPsiElement)?.elementType
        }

        private fun LeafPsiElement.isValidBracket(pair: BracePair): Boolean {
            val pairType = when (elementType) {
                pair.leftBraceType -> pair.rightBraceType
                pair.rightBraceType -> pair.leftBraceType
                else -> return false
            }

            return if (pairType == pair.leftBraceType) {
                checkBracePair(this, parent.firstChild, pairType, PsiElement::getNextSibling)
            } else {
                checkBracePair(this, parent.lastChild, pairType, PsiElement::getPrevSibling)
            }
        }

        private fun checkBracePair(
            brace: PsiElement,
            start: PsiElement,
            type: IElementType,
            next: PsiElement.() -> PsiElement?
        ): Boolean {
            var element: PsiElement? = start
            while (element != null && element != brace) {
                if (element is LeafPsiElement && element.elementType == type) {
                    return true
                }

                element = element.next()
            }

            return false
        }

        private fun filterPairs(type: IElementType, element: LeafPsiElement): List<BracePair>? {
            val pairs = element.language.bracePairs ?: return null
            val filterBraceType = pairs[type.toString()]
            return when {
                filterBraceType == null || filterBraceType.isEmpty() -> {
                    null
                }

                element.javaClass.simpleName == "OCMacroForeignLeafElement" -> {
                    null
                }

                else -> {
                    filterBraceType
                }
            }
        }
    }
}
