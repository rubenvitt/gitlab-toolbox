package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.FormBuilder
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings.AppSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.GitlabService
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import org.gitlab4j.api.models.Project as GitlabProject

class ProjectSettingsComponent(project: Project) {
    private val mainPanel: JPanel
    private val domainsList = ComboBox<String>()
    private val projectsList = ComboBox<GitlabProject>()

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

    var selectedGitlabProject: GitlabProject?
        get() = projectsList.selectedItem as GitlabProject?
        set(value) {
            projectsList.selectedItem = value
        }

    init {
        domainsList.addItem("")
        AppSettingsState.instance.gitlabDomains?.forEach { domainsList.addItem(it) }
        projectsList.renderer = ProjectListCellRenderer()

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
        val selectedDomain = domainsList.selectedItem as String?
        val service = GitlabService.getService(project)

        val projects = try {
            service.projects(selectedDomain).current()
        } catch (e: Exception) {
            logger.error("Error while fetching projects. Try to re-authenticate.", e)
            GitlabService.getService(project).askUserForAccessToken(selectedDomain)
            logger.info("Re-authentication successful. Try to fetch projects again.")
            service.projects(selectedDomain).current()
        }

        logger.info("Fetched ${projects.size} projects for domain $selectedDomain")
        logger.trace("Projects: $projects")

        projectsList.removeAllItems()
        projectsList.addItem(null)
        projects.forEach { projectsList.addItem(it) }
    }

    fun getProject(projectId: Int): GitlabProject? {
        for (i in 0 until projectsList.itemCount) {
            val project = projectsList.getItemAt(i)
            if (project?.id == projectId) {
                return project
            }
        }
        return null
    }
}

class ProjectListCellRenderer : ListCellRenderer<GitlabProject> {
    override fun getListCellRendererComponent(
        list: JList<out GitlabProject>?,
        value: GitlabProject?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component {
        return if (value == null) {
            JLabel("Select project")
        } else {
            JLabel(value.name)
        }
    }

}
