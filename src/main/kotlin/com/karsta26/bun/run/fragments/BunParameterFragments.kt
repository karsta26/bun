package com.karsta26.bun.run.fragments

import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.CommonParameterFragments
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.openapi.util.Predicates
import com.intellij.ui.components.TextComponentEmptyText
import com.intellij.ui.components.fields.ExtendableTextField
import com.karsta26.bun.run.BunRunConfiguration

object BunParameterFragments {

    fun scriptFile(): SettingsEditorFragment<BunRunConfiguration, TextFieldWithBrowseButton> {
        val scriptField = TextFieldWithBrowseButton()
        CommandLinePanel.setMinimumWidth(scriptField, 400)
        val message = "Script File"
        scriptField.emptyText.setText(message)
        scriptField.accessibleContext.accessibleName = message
        TextComponentEmptyText.setupPlaceholderVisibility(scriptField.textField)
        CommonParameterFragments.setMonospaced(scriptField.textField)
        MacrosDialog.addMacroSupport(
            scriptField.textField as ExtendableTextField, MacrosDialog.Filters.ALL
        ) { false }
        val fragment = SettingsEditorFragment<BunRunConfiguration, TextFieldWithBrowseButton>(
            "scriptFile",
            "script File",
            null,
            scriptField,
            100,
            { settings, component -> component.text = settings.getScriptName() ?: "" },
            { settings, component -> settings.setScriptName(component.text) },
            Predicates.alwaysTrue()
        )
        fragment.isRemovable = false
        fragment.setEditorGetter { editor: TextFieldWithBrowseButton -> editor }
        fragment.setHint("Script to run")
        return fragment
    }
}