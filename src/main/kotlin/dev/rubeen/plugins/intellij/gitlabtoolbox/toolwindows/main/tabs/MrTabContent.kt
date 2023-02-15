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
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.PlatformIcons
import com.intellij.util.ui.UIUtil
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService
import org.gitlab4j.api.models.MergeRequest
import java.awt.CardLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import kotlin.reflect.KFunction0

class MrTabContent(private val mergeRequest: MergeRequest, private val toolWindow: ToolWindow) :
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

    fun reload() {
        // recreate center panel
        centerPanel.removeAll()
        createCenterPanel()
    }

    override fun dispose() {
        Disposer.dispose(this)
    }

    override fun createCenterPanel(): JComponent {
        val commits = GitlabService.getService(toolWindow.project).mergeRequestCommits(mergeRequest) // add 100 dummy commits
        centerPanel.add(JPanel(GridBagLayout()).apply {
            add(JPanel(GridLayout(2, 1)).apply {
                add(JBLabel("target branch: " + mergeRequest.targetBranch))
                add(JBLabel("source branch: " + mergeRequest.sourceBranch))
            }, GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                weightx = 1.0
            })
            add(JBScrollPane(JBList(commits.all()).apply {
                cellRenderer = ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
                    JPanel().apply {
                        add(JBLabel(value.id))
                        add(JBLabel(value.message ?: "No message"))
                        add(JBLabel(value.authorName))
                        add(JBLabel(value.committerEmail))
                    }
                }
            }), GridBagConstraints().apply {
                gridx = 1
                gridy = 0
                weightx = 3.0
            })
        })
        return centerPanel
    }

    override fun addActionsTo(group: DefaultActionGroup?) {
        if (group == null) throw IllegalArgumentException("Group must not be null")

        group.addAction(MrOpenAction(mergeRequest))
        group.addAction(MRReloadAction(this::reload))
        group.addSeparator()
    }
}

class MRReloadAction(private val reload: KFunction0<Unit>) :
    AnAction("Reload", "Reload Merge Request details", PlatformIcons.PROJECT_ICON) {
    override fun actionPerformed(e: AnActionEvent) {
        reload()
    }
}

class MrOpenAction(private val mergeRequest: MergeRequest) :
    AnAction("Open MR", "Open Merge Request in browser", PlatformIcons.WEB_ICON) {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.open(mergeRequest.webUrl)
    }
}
