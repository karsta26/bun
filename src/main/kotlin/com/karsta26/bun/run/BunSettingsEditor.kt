package com.karsta26.bun.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.execution.ui.CommonProgramParametersPanel.addMacroSupport
import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.karsta26.bun.run.fragments.*
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
            row("&Working dir:") { cell(workingDirectoryField.getComponent()).align(Align.FILL) }
            row("&package.json:") { cell(packageJsonField.getComponent()).align(Align.FILL) }
            row("&Command:") { cell(commandField.getComponent()).align(Align.FILL) }
            row("JS/TS &file:") { cell(jsFileField.getComponent()).align(Align.FILL) }
            row("Scrip&ts:") { cell(scriptField).align(Align.FILL) }
            row("A&rguments:") { cell(argumentsField).align(Align.FILL) }
            separator()
            row("Bun options:") { cell(bunOptionsField).align(Align.FILL) }
            row("Environment:") { cell(envVarsField).align(Align.FILL) }
        }

        addListenersToRunModeToggle()
    }

    override fun createEditor() = myPanel

    override fun resetEditorFrom(runConfiguration: BunRunConfiguration) {
        runConfiguration.options.let {
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
        toggleRunMode(!runConfiguration.options.mySingleFileMode)
    }

    override fun applyEditorTo(runConfiguration: BunRunConfiguration) {
        runConfiguration.options.apply {
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
}