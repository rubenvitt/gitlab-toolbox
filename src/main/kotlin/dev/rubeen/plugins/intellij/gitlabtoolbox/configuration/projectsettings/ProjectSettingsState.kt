package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "dev.rubeen.plugins.intellij.gitlabtoolbox.ProjectSettingsState",
    storages = [Storage("gitlab-toolbox.xml")],
)
class ProjectSettingsState : PersistentStateComponent<ProjectSettingsState> {
    var gitlabDomain: String? = null
    var gitlabProjectId: String? = null

    companion object {
        fun getInstance(project: Project): ProjectSettingsState? = project.getService(ProjectSettingsState::class.java)
    }

    override fun getState(): ProjectSettingsState = this

    override fun loadState(state: ProjectSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
