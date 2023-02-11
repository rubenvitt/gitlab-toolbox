package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainToolWindow = MainToolWindow(toolWindow)
        val content = ContentFactory.getInstance()
            .createContent(mainToolWindow.getContent, "", false)
        toolWindow.contentManager.addContent(content)
    }
}