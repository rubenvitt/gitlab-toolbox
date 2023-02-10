package dev.rubeen.plugins.intellij.gitlabtoolbox.configuration.appsettings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.FormBuilder
import dev.rubeen.plugins.intellij.gitlabtoolbox.services.CredentialService
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {
    private val mainPanel: JPanel
    private val jbList = ComboBox<String>()
    private val jbAddButton = JButton("Add domain")
    private val jRemoveButton = JButton("Remove domain")
    private val jbRemoveCredentialsButton = JButton("Remove credentials for domain")

    val panel: JPanel
        get() = mainPanel

    val preferredFocusedComponent: JComponent
        get() = jbList

    var selectedDomain: String?
        get() = jbList.selectedItem as String? ?: "- Select domain -"
        set(value) {
            jbList.selectedItem = value
        }

    fun addDomain(domain: String) {
        jbList.addItem(domain)
    }

    var domains: List<String>
        get() = (0 until jbList.itemCount).map { jbList.getItemAt(it) }
        set(value) {
            jbList.removeAllItems()
            jbList.addItem("- Select domain -")
            value.forEach { jbList.addItem(it) }
        }

    init {
        jbAddButton.addActionListener {
            Messages.showInputDialog(
                "Enter domain",
                "Add Domain",
                Messages.getQuestionIcon()
            )?.let { domain -> addDomain(domain) }
        }

        jRemoveButton.addActionListener {
            if (jbList.selectedIndex == 0) return@addActionListener
            jbList.removeItemAt(jbList.selectedIndex)
            CredentialService.instance.removeGitlabAccessToken(jbList.selectedItem as String)
        }

        jbRemoveCredentialsButton.addActionListener {
            CredentialService.instance.removeGitlabAccessToken(jbList.selectedItem as String)
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
}