package dev.rubeen.plugins.intellij.gitlabtoolbox

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import dev.rubeen.plugins.intellij.gitlabtoolbox.exceptions.GitlabException
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService

class PopupDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        project?.getService(GitlabService::class.java)?.let { service ->
            try {
                service.projects().current().let { projects ->
                    Messages.showMessageDialog(project, projects.joinToString("\n") {
                        println(it)
                        it.name
                    }, "Information", Messages.getInformationIcon())
                }
            } catch (ex: GitlabException) {
                GitlabService.getService(project).askUserForAccessToken()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}