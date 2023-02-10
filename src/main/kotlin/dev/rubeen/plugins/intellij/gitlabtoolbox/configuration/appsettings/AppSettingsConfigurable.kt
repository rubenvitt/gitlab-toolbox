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
        return settings.domains != settingsComponent!!.domains.withoutEmpty() || settings.selectedDomain != selectedDomainOrNull(
            settingsComponent!!.selectedDomain
        )
    }

    override fun apply() {
        val settings = AppSettingsState.instance

        settings.domains = settingsComponent!!.domains.withoutEmpty()
        settings.selectedDomain = selectedDomainOrNull(settingsComponent!!.selectedDomain)
    }

    override fun reset() {
        settingsComponent!!.domains = AppSettingsState.instance.domains ?: listOf()
        settingsComponent!!.selectedDomain = AppSettingsState.instance.selectedDomain ?: EMPTY_VALUE
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String = "GitLab Toolbox"

    private fun selectedDomainOrNull(selectedDomain: String?) =
        if (settingsComponent!!.selectedDomain == EMPTY_VALUE) null else settingsComponent!!.selectedDomain
}

private fun <E> List<E>.withoutEmpty(): List<E> {
    return this.filter { it != EMPTY_VALUE }
}
