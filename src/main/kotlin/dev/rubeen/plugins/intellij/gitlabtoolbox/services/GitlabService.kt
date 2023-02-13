package dev.rubeen.plugins.intellij.gitlabtoolbox.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.projectsettings.ProjectSettingsState
import dev.rubeen.plugins.intellij.gitlabtoolbox.exceptions.GitlabException
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.Pager
import org.gitlab4j.api.models.MergeRequest
import org.gitlab4j.api.models.ProjectFilter
import org.gitlab4j.api.models.Project as GitlabProject

private const val SERVICE = "GitLab"

@Service(Service.Level.PROJECT)
class GitlabService(private val project: Project) {
    private lateinit var api: GitLabApi
    private val logger = logger<GitlabService>()
    fun projects(api: String? = null): Pager<GitlabProject> = getApi(api ?: currentApi!!).projectApi.getProjects(
        ProjectFilter().withMembership(true), 10
    )

    fun mergeRequests(gitlabProject: Int, api: String? = null): List<MergeRequest> =
        getApi(api ?: currentApi!!).mergeRequestApi.getMergeRequests(gitlabProject)

    val uri: String
        get() = api.gitLabServerUrl

    fun askUserForAccessToken(api: String? = null) {
        logger.info("Ask user for access token")
        Messages.showPasswordDialog(
            project,
            "Enter Access Token",
            "Access Token",
            Messages.getInformationIcon(),
            null
        ).let { token ->
            logger.info("Save access token")
            CredentialService.instance.saveGitlabAccessToken(api ?: currentApi!!, token!!.toCharArray())
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
        logger.info("Init api for domain $domain")
        val credentials = CredentialService.instance.readGitlabAccessToken(domain)
        if (credentials != null) {
            api = GitLabApi(domain, credentials)
        } else {
            logger.error("No credentials found for domain $domain")
            throw GitlabException("No credentials found for domain $domain")
        }

        logger.info("Api for domain $domain initialized")
        return api
    }

    private fun apiAvailableForDomain(domain: String): Boolean {
        logger.info("Check if api is available for domain $domain")
        return ::api.isInitialized && api.gitLabServerUrl == domain
    }

    private val currentApi: String?
        get() = ProjectSettingsState.getInstance(project).gitlabDomain

}