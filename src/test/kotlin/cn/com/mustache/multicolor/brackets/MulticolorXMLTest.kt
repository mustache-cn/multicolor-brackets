package cn.com.mustache.multicolor.brackets

import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class MulticolorXMLTest : LightJavaCodeInsightFixtureTestCase() {
    fun `disabled for non-determinist results of testMulticolorTagNameForXML`() {
        @Language("XML") val code =
                """
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE note SYSTEM>
<idea-plugin>
    <name>Multicolor Brackets</name>
    <description>
        <p>Supported languages:</p>
        <p>Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, C#, HTML, XML, SQL, Apex language ...</p>
        <br/>
    </description>
</idea-plugin>
                """.trimIndent()
        val multicolorSettings = MulticolorSettings.instance
        myFixture.configureByText(XmlFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(0),
                                angleLevel(0),//idea-plugin
                                angleLevel(0),

                                angleLevel(1),
                                angleLevel(1),//name
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),//name
                                angleLevel(1),

                                angleLevel(1),
                                angleLevel(1),//description
                                angleLevel(1),

                                angleLevel(2),
                                angleLevel(2),//p
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),//p
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),//p
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),//p
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),//br
                                angleLevel(2),

                                angleLevel(1),
                                angleLevel(1),//description
                                angleLevel(1),

                                angleLevel(0),
                                angleLevel(0),//idea-plugin
                                angleLevel(0)
                        )
                )
    }

    fun testMulticolorForXML() {
        @Language("XML") val code =
                """
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE note SYSTEM>
<idea-plugin>
    <name>Multicolor Brackets</name>
    <description>
        <p>Supported languages:</p>
        <p>Java, Scala, Clojure, Kotlin, Python, Haskell, Agda, Rust, JavaScript, TypeScript, Erlang, Go, Groovy, Ruby, Elixir, ObjectiveC, PHP, C#, HTML, XML, SQL, Apex language ...</p>
        <br/>
    </description>
</idea-plugin>
                """.trimIndent()
        val multicolorSettings = MulticolorSettings.instance
        myFixture.configureByText(XmlFileType.INSTANCE, code)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val doHighlighting = myFixture.doHighlighting()
        assertFalse(doHighlighting.isEmpty())
        doHighlighting
                .map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
                .toTypedArray()
                .shouldBe(
                        arrayOf(
                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(0),
                                angleLevel(0),

                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(2),
                                angleLevel(2),

                                angleLevel(1),
                                angleLevel(1),

                                angleLevel(0),
                                angleLevel(0)
                        )
                )
    }
}
