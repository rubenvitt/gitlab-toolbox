package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "dev.rubeen.plugins.intellij.gitlabtoolbox.AppSettingsState",
    storages = [Storage("gitlab-toolbox.xml")],
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var gitlabDomains: List<String>? = null

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
