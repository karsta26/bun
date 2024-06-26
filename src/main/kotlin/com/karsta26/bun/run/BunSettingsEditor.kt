package com.karsta26.bun.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.execution.ui.CommonProgramParametersPanel.addMacroSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.SwingHelper
import com.karsta26.bun.run.before.BunBeforeRunTaskDialog
import com.karsta26.bun.run.fragments.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextField

class BunSettingsEditor(bunRunConfiguration: BunRunConfiguration) : SettingsEditor<BunRunConfiguration>() {

    private var myPanel: JPanel
    private var runMode = RunMode()
    private var packageJsonField = PackageJsonField(bunRunConfiguration.project)
    private var workingDirectoryField = WorkingDirectory(bunRunConfiguration.project)
    private var jsFileField = JSFile(bunRunConfiguration.project, workingDirectoryField)
    private val envVarsField = EnvironmentVariablesTextFieldWithBrowseButton()
    private val bunOptionsField = RawCommandLineEditor().apply { addMacroSupport(editorField) }
    private val argumentsField = RawCommandLineEditor().apply { addMacroSupport(editorField) }
    private val commandField = BunCommandField()
    private val scriptField = JTextField()
    private val componentIndexesSpecificToSingleFileMode = listOf(3, 4, 9, 10)
    private val componentIndexesSpecificToScriptMode = listOf(5, 6, 11, 12)

    init {
        myPanel = panel {
            row("Run mode: ") { cell(runMode.getComponent()) }
            separator()
            row("&Working dir:") { fillCell(workingDirectoryField.getComponent()) }
            row("&package.json:") { fillCell(packageJsonField.getComponent()) }
            row("&Command:") { fillCell(commandField.getComponent()) }
            row("JS/TS &file:") { fillCell(jsFileField.getComponent()) }
            row("Scrip&ts:") { fillCell(scriptField) }
            row("A&rguments:") { fillCell(argumentsField) }
            separator()
            row("Bun options:") { fillCell(bunOptionsField) }
            row("Environment:") { fillCell(envVarsField) }
        }

        addListenersToRunModeToggle()
    }

    private fun Row.fillCell(component: JComponent) = cell(component).align(Align.FILL)

    public override fun createEditor() = myPanel

    override fun resetEditorFrom(runConfiguration: BunRunConfiguration) {
        resetEditorFrom(runConfiguration.options)
    }

    fun resetEditorFrom(options: BunRunConfigurationOptions) {
        options.let {
            runMode.setSingleFileMode(it.mySingleFileMode)
            packageJsonField.setPath(it.myPackageJsonPath.orEmpty())
            workingDirectoryField.setPath(it.myWorkingDirectory.orEmpty())
            jsFileField.setFile(it.myJSFile.orEmpty())
            envVarsField.envs = it.envs
            envVarsField.isPassParentEnvs = it.isPassParentEnvs
            bunOptionsField.text = it.myBunOptions.orEmpty()
            argumentsField.text = it.myProgramParameters.orEmpty()
            commandField.item = it.myCommand
            scriptField.text = it.myScript.orEmpty()
        }
        toggleRunMode(!options.mySingleFileMode)
        setPreferredDialogSize()
    }

    override fun applyEditorTo(runConfiguration: BunRunConfiguration) {
        applyEditorTo(runConfiguration.options)
    }

    fun applyEditorTo(options: BunRunConfigurationOptions) {
        options.apply {
            mySingleFileMode = runMode.isSingleFileMode()
            myPackageJsonPath = packageJsonField.getPath()
            myWorkingDirectory = workingDirectoryField.getPath()
            myJSFile = jsFileField.getFile()
            myEnvs = envVarsField.envs
            myPassParentEnvs = envVarsField.isPassParentEnvs
            myBunOptions = bunOptionsField.text
            myProgramParameters = argumentsField.text
            myCommand = commandField.item
            myScript = scriptField.text
        }
    }

    private fun toggleRunMode(isScriptMode: Boolean) {
        val visibilitySetter = { componentIndexes: List<Int>, visibility: Boolean ->
            componentIndexes.forEach { myPanel.getComponent(it).isVisible = visibility }
        }

        visibilitySetter(componentIndexesSpecificToSingleFileMode, !isScriptMode)
        visibilitySetter(componentIndexesSpecificToScriptMode, isScriptMode)

        commandField.toggleRunMode(isScriptMode)
    }

    private fun addListenersToRunModeToggle() {
        runMode.script.addChangeListener { e ->
            (e?.source as? JRadioButton)?.let { toggleRunMode(it.isSelected) }
        }
        runMode.singleFile.addChangeListener { e ->
            (e?.source as? JRadioButton)?.let { toggleRunMode(!it.isSelected) }
        }
    }

    private fun setPreferredDialogSize() {
        val dialogWrapper = DialogWrapper.findInstance(myPanel)
        if (dialogWrapper is BunBeforeRunTaskDialog) {
            SwingHelper.setPreferredWidthToFitText(packageJsonField.getComponent())
            SwingHelper.setPreferredWidthToFitText(workingDirectoryField.getComponent())
            SwingHelper.setPreferredWidthToFitText(jsFileField.getComponent())
            ApplicationManager.getApplication()
                .invokeLater({ SwingHelper.adjustDialogSizeToFitPreferredSize(dialogWrapper) }, ModalityState.any())
        }
    }
}