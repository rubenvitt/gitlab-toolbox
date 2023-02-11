package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class ProjectSettingsConfigurable(private val project: Project) : Configurable {

    private var settingsComponent: ProjectSettingsComponent? = null

    override fun createComponent(): JComponent {
        settingsComponent = ProjectSettingsComponent(project)
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = ProjectSettingsState.getInstance(project)
        return settings.gitlabDomain != settingsComponent!!.selectedGitlabDomain
                || settings.gitlabProjectId != settingsComponent!!.selectedGitlabProject?.id
    }

    override fun apply() {
        val settings = ProjectSettingsState.getInstance(project)

        settings.gitlabDomain = settingsComponent!!.selectedGitlabDomain
        settings.gitlabProjectId = settingsComponent!!.selectedGitlabProject?.id
    }

    override fun reset() {
        val settings = ProjectSettingsState.getInstance(project)

        settingsComponent!!.selectedGitlabDomain = settings.gitlabDomain
        settingsComponent!!.selectedGitlabProject = settings.gitlabProjectId?.let { settingsComponent!!.getProject(it) }
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String {
        return "GitLab Toolbox (Project Settings)"
    }
}