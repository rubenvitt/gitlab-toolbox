package dev.rubeen.plugins.intellij.gitlabtoolbox.services

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import dev.rubeen.plugins.intellij.gitlabtoolbox.exceptions.GitlabException
import org.gitlab4j.api.GitLabApi

private const val SERVICE = "GitLab"

@Service(Service.Level.PROJECT)
class GitlabService(project: Project) {
    lateinit var api: GitLabApi
    fun projects() = getApi("https://gitlab.com").projectApi.projects

    fun getApi(domain: String): GitLabApi {
        require(domain.isNotBlank()) { "Domain must not be blank" }
        if (!::api.isInitialized) {
            val credentials = loadCredentials(domain)
            if (credentials != null) {
                api = GitLabApi(domain, credentials)
            }
            else {
                throw GitlabException("No credentials found for domain $domain")
            }
        }
        return api
    }

    fun loadCredentials(domain: String): String? =
            PasswordSafe.instance.getPassword(createCredentialAttributes(domain))

    fun saveAccessToken(domain: String, accessToken: String) {
        PasswordSafe.instance.set(createCredentialAttributes(domain), Credentials(domain, accessToken))
    }

    private fun createCredentialAttributes(key: String) = CredentialAttributes(serviceName(key))
    private fun serviceName(key: String) = generateServiceName(SERVICE, key)
}