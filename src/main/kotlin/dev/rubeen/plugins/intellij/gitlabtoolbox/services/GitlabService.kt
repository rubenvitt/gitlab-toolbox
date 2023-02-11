package dev.rubeen.plugins.intellij.gitlabtoolbox.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings.ProjectSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.exceptions.GitlabException
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Project as GitlabProject

private const val SERVICE = "GitLab"

@Service(Service.Level.PROJECT)
class GitlabService(private val project: Project) {
    private lateinit var api: GitLabApi
    fun projects(api: String? = null): MutableList<GitlabProject> = getApi(api ?: currentApi!!).projectApi.projects

    fun askUserForAccessToken(api: String? = null) {
        Messages.showPasswordDialog(
            project,
            "Enter Access Token",
            "Access Token",
            Messages.getInformationIcon(),
            null
        ).let { token ->
            CredentialService.instance.saveGitlabAccessToken(api ?: currentApi!!, token!!)
        }
    }

    companion object {
        fun getService(project: Project): GitlabService = project.getService(GitlabService::class.java)
    }

    private fun getApi(domain: String): GitLabApi {
        require(domain.isNotBlank()) { "Domain must not be blank" }
        if (apiAvailableForDomain(domain)) {
            return api
        }
        return initApi(domain)
    }

    private fun initApi(domain: String): GitLabApi {
        val credentials = CredentialService.instance.readGitlabAccessToken(domain)
        if (credentials != null) {
            api = GitLabApi(domain, credentials)
        } else {
            throw GitlabException("No credentials found for domain $domain")
        }

        return api
    }

    private fun apiAvailableForDomain(domain: String) = ::api.isInitialized && api.gitLabServerUrl == domain

    val currentApi
        get() = ProjectSettingsState.getInstance(project)?.gitlabDomain

}