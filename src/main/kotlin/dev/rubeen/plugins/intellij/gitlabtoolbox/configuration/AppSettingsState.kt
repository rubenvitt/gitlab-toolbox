package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "dev.rubeen.plugins.intellij.gitlabtoolbox.AppSettingsState", storages = [Storage("gitlab-toolbox.xml")])
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var domains: List<String> = emptyList()
⁄
    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) {
        domains = state.domains
    }
}
