package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings

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

        domainsList.addActionListener { event ->
            println("event: $event")
            val selectedDomain = domainsList.selectedItem
            println("Selected domain: $selectedDomain")
            val service = GitlabService.getService(project)
            println("Service: $service")
            val projects = try {
                service.projects(selectedDomain as String)
            } catch (e: Exception) {
                GitlabService.getService(project).askUserForAccessToken(selectedDomain as String)
                service.projects(selectedDomain)
            }
            println("Projects: $projects")
            projectsList.removeAll()
            projectsList.addItem("")
            projects.forEach { projectsList.addItem(it.name) }
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Gitlab domain:", domainsList)
            .addLabeledComponent("Gitlab project:", projectsList)
            .panel
    }
}