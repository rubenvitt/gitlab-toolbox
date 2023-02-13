package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main.tabs

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.PanelWithActionsAndCloseButton
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.IconLabelButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.PlatformIcons
import com.intellij.util.ui.UIUtil
import org.gitlab4j.api.models.MergeRequest
import java.awt.CardLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MrTabContent(private val mergeRequest: MergeRequest, toolWindow: ToolWindow) :
    PanelWithActionsAndCloseButton(toolWindow.contentManager, "mergeRequest") {
    var mainContent: JPanel? = null
    var mrTitle: JBLabel? = null
    var labelButton1: IconLabelButton? = null

    val centerPanel = JPanel(CardLayout())

    val content: JPanel
        get() = mainContent!!

    init {
        mrTitle?.text = mergeRequest.title
        init()
    }

    fun createUIComponents() {
        labelButton1 = IconLabelButton(UIUtil.getWarningIcon()) {
            println("Button clicked ${it.javaClass}")
        }
    }

    override fun dispose() {
        Disposer.dispose(this)
    }

    override fun createCenterPanel(): JComponent {
        centerPanel.add(JPanel().apply {
         add(JBLabel("target branch: " + mergeRequest.targetBranch), "test")
         add(JBLabel("source branch: " + mergeRequest.sourceBranch), "test2")
        })
        return centerPanel
    }

    override fun addActionsTo(group: DefaultActionGroup?) {
        if (group == null) throw IllegalArgumentException("Group must not be null")

        group.addAction(MrOpenAction(mergeRequest))
        group.addSeparator()
    }
}

class MrOpenAction(private val mergeRequest: MergeRequest) :
    AnAction("Open MR", "Open Merge Request in browser", PlatformIcons.WEB_ICON) {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.open(mergeRequest.webUrl)
    }
}
