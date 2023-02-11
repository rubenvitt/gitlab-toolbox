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
        val settings = ProjectSettingsState.getInstance(project) ?: ProjectSettingsState()
        return settings.gitlabDomain != settingsComponent!!.selectedGitlabDomain
                || settings.gitlabProjectId != settingsComponent!!.selectedGitlabProject
    }

    override fun apply() {
        val settings = ProjectSettingsState.getInstance(project) ?: ProjectSettingsState()

        settings.gitlabDomain = settingsComponent!!.selectedGitlabDomain
        settings.gitlabProjectId = settingsComponent!!.selectedGitlabProject
    }

    override fun getDisplayName(): String {
        return "GitLab Toolbox (Project Settings)"
    }
}