package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

private const val EMPTY_VALUE = "- Select domain -"

class AppSettingsConfigurable : Configurable {
    private var settingsComponent: AppSettingsComponent? = null
    override fun createComponent(): JComponent {
        settingsComponent = AppSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        return settings.gitlabDomains != settingsComponent!!.gitlabDomains.withoutEmpty()
    }

    override fun apply() {
        val settings = AppSettingsState.instance

        settings.gitlabDomains = settingsComponent!!.gitlabDomains.withoutEmpty()
    }

    override fun reset() {
        settingsComponent!!.gitlabDomains = AppSettingsState.instance.gitlabDomains ?: listOf()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String = "GitLab Toolbox"
}

private fun <E> List<E>.withoutEmpty(): List<E> {
    return this.filter { it != EMPTY_VALUE }
}
