package cn.com.mustache.multicolor.brackets

import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class MulticolorJavaTest : LightJavaCodeInsightFixtureTestCase() {
    override fun tearDown() {
        super.tearDown()
        val multicolorSettings = MulticolorSettings.instance
        multicolorSettings.isMulticolorEnabled = true
        multicolorSettings.isEnableMulticolorAngleBrackets = true
        multicolorSettings.isEnableMulticolorRoundBrackets = true
        multicolorSettings.isEnableMulticolorSquareBrackets = true
        multicolorSettings.isEnableMulticolorSquigglyBrackets = true
    }

    fun testDisableMulticolorForJava() {
        val multicolorSettings = MulticolorSettings.instance
        multicolorSettings.isMulticolorEnabled = false
        @Language("Java") val code =
                """
public class Test<T> {
    public Boolean test() {
        return "true".equals("false") || "false".equals("true".concat("false".length() + "*"));
    }
}
                """.trimIndent()
        myFixture.configureByText(JavaFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf()
                )
    }

    fun testIssue391() {
        val multicolorSettings = MulticolorSettings.instance
        @Language("Java") val code =
                """
public class Test {
    public Boolean test() {
        return "true".equals("false") || "false".equals("true".concat("false".length() + "*"));
    }
}
                """.trimIndent()
        myFixture.configureByText(JavaFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting.filter { brackets.contains(it.text.toChar()) }
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                roundLevel(0),//{

                                roundLevel(1),//(
                                roundLevel(1),//)

                                roundLevel(1),//{

                                roundLevel(2),
                                roundLevel(2),
                                roundLevel(2),
                                roundLevel(3),
                                roundLevel(4),
                                roundLevel(4),
                                roundLevel(3),
                                roundLevel(2),

                                roundLevel(1),//}

                                roundLevel(0)//}
                        )
                )
    }
}