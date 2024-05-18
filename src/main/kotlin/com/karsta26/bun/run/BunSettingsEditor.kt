package com.karsta26.bun.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class BunSettingsEditor : SettingsEditor<BunRunConfiguration>() {

    private var myPanel: JPanel? = null
    private var scriptPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        scriptPathField.addBrowseFolderListener(
            "Select Script To Run", null, null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Script file", scriptPathField)
            .panel
    }

    override fun resetEditorFrom(s: BunRunConfiguration) {
        scriptPathField.text = s.getScriptName();
    }

    override fun applyEditorTo(s: BunRunConfiguration) {
        s.setScriptName(scriptPathField.getText());
    }

    override fun createEditor(): JComponent {
        return myPanel!!
    }
}