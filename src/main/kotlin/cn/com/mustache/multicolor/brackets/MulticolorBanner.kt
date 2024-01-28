package cn.com.mustache.multicolor.brackets

import cn.com.mustache.multicolor.brackets.settings.MulticolorConfigurable
import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import cn.com.mustache.multicolor.brackets.util.toPsiFile
import cn.com.mustache.multicolor.brackets.visitor.MulticolorHighlightVisitor.Companion.checkForBigFile
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.ui.HyperlinkLabel

class MulticolorBanner : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {

        val psiFile = file.toPsiFile(project)
        if (psiFile != null && !checkForBigFile(psiFile)) {
            if (MulticolorSettings.instance.suppressBigFileCheck) return null
            return EditorNotificationPanel().apply {
                text("Multicolor is disabled for files > " + MulticolorSettings.instance.bigFilesLinesThreshold + " lines")
                icon(AllIcons.General.InspectionsEye)
                createComponentActionLabel("got it, don't show again") {
                    MulticolorSettings.instance.suppressBigFileCheck = true
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }

                createComponentActionLabel("open settings") {
                    ShowSettingsUtilImpl.showSettingsDialog(project, MulticolorConfigurable.ID, "")
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }

        return null
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("MulticolorBanner")

        fun EditorNotificationPanel.createComponentActionLabel(labelText: String, callback: (HyperlinkLabel) -> Unit) {
            val label: Ref<HyperlinkLabel> = Ref.create()
            label.set(createActionLabel(labelText) {
                callback(label.get())
            })
        }
    }
}
