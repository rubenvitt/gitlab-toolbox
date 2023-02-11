package dev.rubeen.plugins.intellij.gitlabtoolbox.toolwindows.main

import com.intellij.openapi.wm.ToolWindow
import javax.swing.JPanel

class MainToolWindow(toolWindow: ToolWindow) {
    private lateinit var windowContent: JPanel

    val getContent: JPanel = windowContent
}
