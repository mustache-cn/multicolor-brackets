package cn.com.mustache.multicolor.brackets.visitor

import cn.com.mustache.multicolor.brackets.MulticolorHighlighter.getHighlightInfo
import cn.com.mustache.multicolor.brackets.MulticolorInfo
import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.awt.Color


abstract class MulticolorHighlightVisitor : HighlightVisitor {

    private var highlightInfoHolder: HighlightInfoHolder? = null

    override fun suitableForFile(file: PsiFile): Boolean {
        return MulticolorSettings.instance.isMulticolorEnabled &&
                checkForBigFile(file) &&
                fileIsNotHaskellOrIntelliJHaskellPluginNotEnabled(file.fileType.name)
    }

    final override fun analyze(
        file: PsiFile,
        updateWholeFile: Boolean,
        holder: HighlightInfoHolder,
        action: Runnable
    ): Boolean {
        highlightInfoHolder = holder
        onBeforeAnalyze(file, updateWholeFile)
        try {
            action.run()
        } catch (e: Throwable) {
        } finally {
            onAfterAnalyze()
        }
        return true
    }

    protected open fun onBeforeAnalyze(file: PsiFile, updateWholeFile: Boolean) = Unit

    protected open fun onAfterAnalyze() {
        highlightInfoHolder = null
    }

    protected fun PsiElement.setHighlightInfo(
        parent: PsiElement?,
        level: Int,
        startElement: PsiElement?,
        endElement: PsiElement?
    ) {
        val holder = highlightInfoHolder ?: return
        val globalScheme = EditorColorsManager.getInstance().globalScheme
        getHighlightInfo(globalScheme, this, level)
            ?.also {
                holder.add(it)

                if (startElement != null || endElement != null) {
                    val color: Color? = globalScheme.getAttributes(it.forcedTextAttributesKey)?.foregroundColor
                    color?.let {
                        parent?.saveMulticolorInfo(level, color, startElement, endElement)
                    }
                }
            }
    }

    private fun PsiElement.saveMulticolorInfo(
        level: Int,
        color: Color,
        startElement: PsiElement?,
        endElement: PsiElement?
    ) {
        val multicolorInfo = MulticolorInfo.MULTICOLOR_INFO_KEY[this]?.also {
            it.level = level
            it.color = color
        } ?: MulticolorInfo(level, color)
            .also { MulticolorInfo.MULTICOLOR_INFO_KEY[this] = it }

        startElement?.let { multicolorInfo.startElement = it }
        endElement?.let { multicolorInfo.endElement = it }
    }

    companion object {
        private val isIntelliJHaskellEnabled: Boolean by lazy {
            PluginManagerCore.getPlugin(
                PluginId.getId("intellij.haskell")
            )?.isEnabled ?: false
        }

        fun checkForBigFile(file: PsiFile): Boolean =
            !(MulticolorSettings.instance.doNOTMulticolorifyBigFiles &&
                    file.getLineCount() > MulticolorSettings.instance.bigFilesLinesThreshold)

        private fun fileIsNotHaskellOrIntelliJHaskellPluginNotEnabled(fileType: String) =
            fileType != "Haskell" || !isIntelliJHaskellEnabled
    }
}

fun PsiElement.getLineCount(): Int {
    try {
        val doc = containingFile?.let { PsiDocumentManager.getInstance(project).getDocument(it) }
        if (doc != null) {
            val spaceRange = textRange ?: TextRange.EMPTY_RANGE

            if (spaceRange.endOffset <= doc.textLength && spaceRange.startOffset < spaceRange.endOffset) {
                val startLine = doc.getLineNumber(spaceRange.startOffset)
                val endLine = doc.getLineNumber(spaceRange.endOffset - 1)

                return endLine - startLine + 1
            }
        }
        return StringUtil.getLineBreakCount(text ?: "") + 1
    } catch (e: Throwable) {
        return 0
    }
}
