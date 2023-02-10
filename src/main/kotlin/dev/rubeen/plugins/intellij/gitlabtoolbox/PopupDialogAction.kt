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
            val domain = Messages.showInputDialog(
                project,
                "Enter domain",
                "Domain",
                Messages.getInformationIcon(),
                "https://gitlab.com",
                null
            )
            try {
                service.getApi(domain!!).projectApi.projects.let { projects ->
                    Messages.showMessageDialog(project, projects?.joinToString("\n") {
                        println(it)
                        it.name
                    }, "Information", Messages.getInformationIcon())
                }
            } catch (ex: GitlabException) {
                Messages.showPasswordDialog(
                    project,
                    "Enter Access Token",
                    "Access Token",
                    Messages.getInformationIcon(),
                    null
                ).let { token ->
                    service.saveAccessToken(domain!!, token!!)
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}