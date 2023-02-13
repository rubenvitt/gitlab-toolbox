package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings

import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.CredentialService
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {
    private val mainPanel: JPanel
    private val jbList = JBList<String>()
    private val jbAddButton = JButton("Add domain")
    private val jRemoveButton = JButton("Remove domain")
    private val jbRemoveCredentialsButton = JButton("Remove credentials for domain")
    private val jbAccessTokenInput = JBPasswordField()
    private val jbSaveAccessTokenButton = JButton("Save access token")

    val panel: JPanel
        get() = mainPanel

    val preferredFocusedComponent: JComponent
        get() = jbList

    var gitlabDomains: List<String> = listOf()
        set(value) {
            field = value
            jbList.removeAll()
            jbList.setListData(value.toTypedArray())
        }

    init {
        jbAddButton.addActionListener {
            Messages.showInputDialog(
                "Enter domain",
                "Add Domain",
                Messages.getQuestionIcon()
            )?.let { domain -> addGitlabDomain(domain) }
        }

        jRemoveButton.addActionListener {
            if (jbList.selectedIndex == 0) return@addActionListener
            jbList.remove(jbList.selectedIndex)
            CredentialService.instance.removeGitlabAccessToken(jbList.selectedValue)
        }

        jbRemoveCredentialsButton.addActionListener {
            CredentialService.instance.removeGitlabAccessToken(jbList.selectedValue)
        }

        jbSaveAccessTokenButton.addActionListener {
            CredentialService.instance.saveGitlabAccessToken(jbList.selectedValue, jbAccessTokenInput.password)
        }

        val firstLine = FormBuilder.createFormBuilder().apply {
            addLabeledComponent("Domains:", jbList)
            addLabeledComponent("Access Token", jbAccessTokenInput)
            addComponent(jbSaveAccessTokenButton)
            addComponent(jbRemoveCredentialsButton)
            addComponent(jRemoveButton)
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(firstLine.panel)
            .addComponentFillVertically(JPanel(), 0)
            .addComponent(jbAddButton)
            .panel
    }

    private fun addGitlabDomain(domain: String) {
        if (domain.isBlank()) return
        if (gitlabDomains.contains(domain)) return
        gitlabDomains = gitlabDomains + domain
    }
}