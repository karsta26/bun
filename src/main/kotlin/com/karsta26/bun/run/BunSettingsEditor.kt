package com.karsta26.bun.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.execution.ui.CommonProgramParametersPanel.addMacroSupport
import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.RawCommandLineEditor
import com.intellij.util.ui.FormBuilder
import com.karsta26.bun.run.fragments.JSFile
import com.karsta26.bun.run.fragments.PackageJsonField
import com.karsta26.bun.run.fragments.RunMode
import com.karsta26.bun.run.fragments.WorkingDirectory
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JSeparator
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
    private val commandField = JTextField()
    private val scriptField = JTextField()
    private val componentIndexesSpecificToSingleFileMode = listOf(3, 4, 9, 10)
    private val componentIndexesSpecificToScriptMode = listOf(5, 6, 11, 12)

    init {
        myPanel = FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .setHorizontalGap(10)
            .setVerticalGap(4)
            .addLabeledComponent("Run mode: ", runMode.getComponent())
            .addComponent(JSeparator(), 8)
            .addLabeledComponent("&Working dir:", workingDirectoryField.getComponent())
            .addLabeledComponent("&package.json:", packageJsonField.getComponent())
            .addLabeledComponent("&Command:", commandField)
            .addLabeledComponent("JS/TS &file:", jsFileField.getComponent())
            .addLabeledComponent("Scrip&ts:", scriptField)
            .addLabeledComponent("A&rguments:", argumentsField)
            .addComponent(JSeparator(), 8)
            .addLabeledComponent("Bun options:", bunOptionsField)
            .addLabeledComponent("Environment:", envVarsField)
            .panel

        toggleRunMode(false)
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
            commandField.text = it.myCommand.orEmpty()
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
            myCommand = commandField.text
            myScript = scriptField.text
        }
    }

    private fun toggleRunMode(isScriptMode: Boolean) {
        val visibilitySetter = { componentIndexes: List<Int>, visibility: Boolean ->
            componentIndexes.forEach { myPanel.getComponent(it).isVisible = visibility }
        }

        visibilitySetter(componentIndexesSpecificToSingleFileMode, !isScriptMode)
        visibilitySetter(componentIndexesSpecificToScriptMode, isScriptMode)

        commandField.apply {
            isEnabled = isScriptMode
            if (!isScriptMode) text = BunCommand.RUN.command
        }
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