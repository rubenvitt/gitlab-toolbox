package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.FormBuilder
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings.AppSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService
import javax.swing.JPanel

class ProjectSettingsComponent(project: Project) {
    private val mainPanel: JPanel
    private val domainsList = ComboBox<String>()
    private val projectsList = ComboBox<String>()

    companion object {
        private val logger = logger<ProjectSettingsComponent>()
    }

    val panel: JPanel
        get() = mainPanel

    var selectedGitlabDomain: String?
        get() = domainsList.selectedItem as String?
        set(value) {
            domainsList.selectedItem = value
        }

    var selectedGitlabProject: String?
        get() = projectsList.selectedItem as String?
        set(value) {
            projectsList.selectedItem = value
        }

    init {
        domainsList.addItem("")
        AppSettingsState.instance.gitlabDomains?.forEach { domainsList.addItem(it) }

        domainsList.addActionListener {
            // run async
            Thread { updateProjectsList(project) }.start()
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Gitlab domain:", domainsList)
            .addLabeledComponent("Gitlab project:", projectsList)
            .panel
    }

    private fun updateProjectsList(project: Project) {
        val selectedDomain = domainsList.selectedItem
        val service = GitlabService.getService(project)

        val projects = try {
            service.projects(selectedDomain as String)
        } catch (e: Exception) {
            logger.error("Error while fetching projects. Try to re-authenticate.", e)
            GitlabService.getService(project).askUserForAccessToken(selectedDomain as String)
            logger.info("Re-authentication successful. Try to fetch projects again.")
            service.projects(selectedDomain)
        }

        logger.info("Fetched ${projects.size} projects for domain $selectedDomain")
        logger.trace("Projects: $projects")

        projectsList.removeAll()
        projectsList.addItem("")
        projects.forEach { projectsList.addItem(it.name) }
    }
}