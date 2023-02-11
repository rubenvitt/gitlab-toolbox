package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main

import com.intellij.openapi.wm.ToolWindow
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings.ProjectSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService
import org.gitlab4j.api.models.MergeRequest
import java.awt.Component
import java.awt.Desktop
import java.net.URI
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class MainToolWindow(toolWindow: ToolWindow) {
    private lateinit var windowContent: JPanel
    private lateinit var mrList: JList<MergeRequest>
    private lateinit var reloadButton: JButton

    val getContent: JPanel = windowContent

    init {
        toolWindow.title = "Gitlab Toolbox"
        reloadButton.addActionListener {
            Thread {
                ProjectSettingsState.getInstance(toolWindow.project).gitlabProjectId?.let { projectId ->
                    GitlabService.getService(toolWindow.project).mergeRequests(projectId).let { mergeRequests ->
                        if (mergeRequests.isNotEmpty()) mrList.setListData(mergeRequests.toTypedArray())
                        else mrList.setListData(arrayOf(null))
                    }
                }
            }.start()
        }

        mrList.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                println("clicked")
                if (e.clickCount == 2) {
                    if (Desktop.isDesktopSupported()) {
                        val uri = GitlabService.getService(toolWindow.project).uri.plus("/").plus(mrList.selectedValue.webUrl)
                        Desktop.getDesktop().browse(URI(uri))
                    }
                }
            }
        })
        mrList.cellRenderer = MRListCellRenderer(toolWindow)
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
