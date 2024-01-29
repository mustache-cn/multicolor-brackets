package cn.com.mustache.multicolor.brackets

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import java.awt.Color
import java.lang.ref.WeakReference

data class MulticolorInfo(var level: Int, var color: Color) {
    private var _startElement: WeakReference<PsiElement>? = null
    private var _endElement: WeakReference<PsiElement>? = null

    var startElement: PsiElement?
        get() = _startElement?.get()
        set(value) {
            _startElement = value?.let { WeakReference(it) }
        }

    val startOffset get() = startElement?.textRange?.startOffset ?: -1

    var endElement: PsiElement?
        get() = _endElement?.get()
        set(value) {
            _endElement = value?.let { WeakReference(it) }
        }

    val endOffset get() = endElement?.textRange?.endOffset ?: -1

    fun containsOffset(offset: Int): Boolean {
        val startElement = startElement ?: return false
        val endElement = endElement ?: return false
        val startOffset = startElement.textRange.startOffset
        val endOffset = endElement.textRange.endOffset

        return offset in startOffset..endOffset
    }

    companion object {
        val MULTICOLOR_INFO_KEY: Key<MulticolorInfo> = Key.create("MULTICOLOR_INFO")
    }
}