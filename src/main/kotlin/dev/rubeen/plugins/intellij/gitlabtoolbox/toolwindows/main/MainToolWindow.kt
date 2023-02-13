package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main

import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings.ProjectSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService
import dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main.tabs.MrTabContent
import org.gitlab4j.api.models.MergeRequest
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class MainToolWindow(private val toolWindow: ToolWindow) : SimpleToolWindowPanel(false, true) {
    private lateinit var windowContent: JPanel
    private lateinit var mrList: JList<MergeRequest>
    private lateinit var reloadButton: JButton

    val getContent: JPanel = windowContent

    init {
        toolWindow.title = "Gitlab Toolbox"
        reloadButton.addActionListener {
            println("Reload button clicked")
            Thread {
                ProjectSettingsState.getInstance(toolWindow.project).gitlabProjectId?.let { projectId ->
                    GitlabService.getService(toolWindow.project).mergeRequests(projectId).let { mergeRequests ->
                        if (mergeRequests.isNotEmpty()) mrList.setListData(mergeRequests.toTypedArray())
                        else mrList.setListData(arrayOf(null))
                    }
                }
            }.start()
        }

        mrList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1) {
                    openMrInNewTab(mrList.selectedValue)
                }
            }
        })
        mrList.cellRenderer = MRListCellRenderer(toolWindow)
    }

    private fun openMrInNewTab(mergeRequest: MergeRequest?) {
        if (mergeRequest == null) return

        toolWindow.contentManager.contents.find { it.executionId.compareTo(mergeRequest.id) == 0 }?.let {
            toolWindow.contentManager.setSelectedContent(it)
            return
        }

        toolWindow.contentManager.factory.createContent(
            MrTabContent(mergeRequest, toolWindow),
            mergeRequest.title,
            false
        ).let {
            it.executionId = mergeRequest.id.toLong()
            toolWindow.contentManager.addContent(it)
            toolWindow.contentManager.setSelectedContent(it)
        }
    }
}

class MRListCellRenderer(private val toolWindow: ToolWindow) : ListCellRenderer<MergeRequest> {
    override fun getListCellRendererComponent(
        list: JList<out MergeRequest>?,
        value: MergeRequest?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component {
        return JButton(value?.title ?: "No merge requests found")
    }

}
