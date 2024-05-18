package com.karsta26.bun.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class BunRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<BunRunConfigurationOptions>(project, factory) {

    override fun getOptions(): BunRunConfigurationOptions {
        return super.getOptions() as BunRunConfigurationOptions
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return BunRunProfileState(project, options, environment)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return BunSettingsEditor()
    }

    fun getScriptName(): String {
        return options.getScriptName()!!
    }

    fun setScriptName(scriptName: String?) {
        options.setScriptName(scriptName!!)
    }
}