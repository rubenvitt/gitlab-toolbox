package dev.rubeen.plugins.intellij.gitlabtoolbox.services

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

private const val GITLAB_SERVICE = "GitLab"

@Service(Service.Level.APP)
class CredentialService {
    fun saveGitlabAccessToken(domain: String, accessToken: CharArray) {
        PasswordSafe.instance.set(
            createCredentialAttributes(domain),
            Credentials(domain, accessToken)
        )
    }

    fun removeGitlabAccessToken(domain: String) {
        PasswordSafe.instance.set(createCredentialAttributes(domain), null)
    }

    fun readGitlabAccessToken(domain: String): String? =
        PasswordSafe.instance.getPassword(createCredentialAttributes(domain))

    private fun createCredentialAttributes(key: String) = CredentialAttributes(generateServiceName(GITLAB_SERVICE, key))

    companion object {
        val instance: CredentialService
            get() = ApplicationManager.getApplication().getService(CredentialService::class.java)
    }
}