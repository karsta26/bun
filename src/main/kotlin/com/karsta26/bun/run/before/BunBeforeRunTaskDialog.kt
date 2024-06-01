package com.karsta26.bun.run.before

import com.intellij.execution.configurations.runConfigurationType
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import com.karsta26.bun.run.BunConfigurationType
import com.karsta26.bun.run.BunRunConfiguration
import com.karsta26.bun.run.BunRunConfigurationOptions
import com.karsta26.bun.run.BunSettingsEditor

class BunBeforeRunTaskDialog(runConfiguration: BunRunConfiguration, settings: BunRunConfigurationOptions) :
    DialogWrapper(runConfiguration.project) {
    private var myEditor: BunSettingsEditor

    val settings: BunRunConfigurationOptions
        get() {
            val configurationOptions = BunRunConfigurationOptions()
            myEditor.applyEditorTo(configurationOptions)
            return configurationOptions
        }

    init {
        val bunRunConfiguration =
            runConfigurationType<BunConfigurationType>().createTemplateConfiguration(runConfiguration.project)
        myEditor = bunRunConfiguration.configurationEditor
        Disposer.register(disposable, myEditor)
        title = "Bun Script"
        init()
        myEditor.resetEditorFrom(configureOptions(runConfiguration, settings))
    }

    private fun configureOptions(
        runConfiguration: BunRunConfiguration,
        settings: BunRunConfigurationOptions
    ): BunRunConfigurationOptions {
        settings.myPackageJsonPath = settings.myPackageJsonPath ?: runConfiguration.options.myPackageJsonPath
        settings.myWorkingDirectory = settings.myWorkingDirectory ?: runConfiguration.options.myWorkingDirectory
        return settings
    }

    override fun createCenterPanel() = myEditor.createEditor()
}
