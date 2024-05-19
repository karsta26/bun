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
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JSeparator
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class BunSettingsEditor(bunRunConfiguration: BunRunConfiguration) : SettingsEditor<BunRunConfiguration>() {

    private var myPanel: JPanel
    private var runMode = RunMode()
    private var packageJsonField = PackageJsonField(bunRunConfiguration.project)
    private var workingDirectoryField = WorkingDirectory(bunRunConfiguration.project)
    private var jsFileField = JSFile(bunRunConfiguration.project, workingDirectoryField)
    private val envVarsField = EnvironmentVariablesTextFieldWithBrowseButton()
    private val bunOptionsField = RawCommandLineEditor().apply { addMacroSupport(editorField) }
    private val argumentsField = RawCommandLineEditor().apply { addMacroSupport(editorField) }
    private val componentIndexesSpecificToSingleFileMode = listOf(3, 4, 7, 8)
    private val componentIndexesSpecificToScriptMode = listOf(5, 6)

    init {
        myPanel = FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .setHorizontalGap(10)
            .setVerticalGap(4)
            .addLabeledComponent("Run mode: ", runMode.getComponent())
            .addComponent(JSeparator(), 8)
            .addLabeledComponent("&Working dir:", workingDirectoryField.getComponent())
            .addLabeledComponent("&package.json:", packageJsonField.getComponent())
            .addLabeledComponent("JS/TS &file:", jsFileField.getComponent())
            .addLabeledComponent("A&rguments:", argumentsField)
            .addComponent(JSeparator(), 8)
            .addLabeledComponent("Bun options:", bunOptionsField)
            .addLabeledComponent("Environment:", envVarsField)
            .panel

        toggleRunMode(false)
        addListenersToRunModeToggle()
    }

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
        }
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
        }
    }

    override fun createEditor(): JComponent {
        return myPanel
    }

    private fun toggleRunMode(isScriptMode: Boolean) {
        componentIndexesSpecificToSingleFileMode.forEach {
            myPanel.getComponent(it).isVisible = !isScriptMode
        }
        componentIndexesSpecificToScriptMode.forEach {
            myPanel.getComponent(it).isVisible = isScriptMode
        }
    }

    private fun addListenersToRunModeToggle() {
        runMode.script.addChangeListener(object : ChangeListener {
            override fun stateChanged(e: ChangeEvent?) {
                val sourceState = e?.source ?: return
                if (sourceState is JRadioButton) {
                    toggleRunMode(sourceState.isSelected)
                }
            }
        })
        runMode.singleFile.addChangeListener(object : ChangeListener {
            override fun stateChanged(e: ChangeEvent?) {
                val sourceState = e?.source ?: return
                if (sourceState is JRadioButton) {
                    toggleRunMode(!sourceState.isSelected)
                }
            }
        })
    }
}