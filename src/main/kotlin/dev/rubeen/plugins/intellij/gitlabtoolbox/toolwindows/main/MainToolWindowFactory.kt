package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainToolWindow = MainToolWindow(toolWindow)
        val content = ContentFactory.getInstance()
            .createContent(mainToolWindow.getContent, "Merge Request List", false).apply {
                isCloseable = false
                isPinned = true
            }

        toolWindow.contentManager.addContent(content)
        toolWindow.title = "Gitlab Toolbox"
    }
}