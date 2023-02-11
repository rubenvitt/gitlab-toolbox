package dev.rubeen.plugins.intellij.gitlabtoolbox.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main.MainToolWindow

private val mainToolWindowId = MainToolWindow::class.java

class ToolWindowListener(private val project: Project) : ToolWindowManagerListener {

    override fun stateChanged(
        toolWindowManager: ToolWindowManager,
        toolWindow: ToolWindow,
        changeType: ToolWindowManagerListener.ToolWindowManagerEventType,
    ) {
        if (toolWindow.id != mainToolWindowId.name) return
        println("ToolWindowListener.stateChanged $changeType for toolwindow: ${toolWindow.id}")
    }
}