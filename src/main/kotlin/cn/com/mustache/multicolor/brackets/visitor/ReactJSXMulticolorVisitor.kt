package cn.com.mustache.multicolor.brackets.visitor

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.javascript.DialectDetector
import com.intellij.psi.PsiFile

class ReactJSXMulticolorVisitor : XmlMulticolorVisitor() {

    override fun suitableForFile(file: PsiFile): Boolean {
        return DialectDetector.isJSX(file)
    }

    override fun clone(): HighlightVisitor {
        return ReactJSXMulticolorVisitor()
    }
}