package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings

import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.CredentialService
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {
    private val mainPanel: JPanel
    private val jbList = JBList<String>()
    private val jbAddButton = JButton("Add domain")
    private val jRemoveButton = JButton("Remove domain")
    private val jbRemoveCredentialsButton = JButton("Remove credentials for domain")

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

        val firstLine = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEADING)
            add(
                FormBuilder.createFormBuilder()
                    .addLabeledComponent("Domains:", jbList)
                    .panel
            )
            add(jbRemoveCredentialsButton)
            add(jRemoveButton)
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(firstLine)
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