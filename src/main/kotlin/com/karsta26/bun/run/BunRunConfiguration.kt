package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class BunRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<BunRunConfigurationOptions>(project, factory), CommonProgramRunConfigurationParameters {

    override fun getOptions(): BunRunConfigurationOptions {
        return super.getOptions() as BunRunConfigurationOptions
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return BunRunProfileState(options, environment)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return BunSettingsEditor(this)
    }

    fun getScriptName(): String? {
        return options.myScriptName
    }

    fun setScriptName(scriptName: String?) {
        options.myScriptName = scriptName
    }

    override fun setProgramParameters(value: String?) {
        options.programParameters = value
    }

    override fun setWorkingDirectory(value: String?) {
        options.workingDirectory = value
    }

    override fun setPassParentEnvs(passParentEnvs: Boolean) {
        options.isPassParentEnvs = passParentEnvs
    }

    override fun setEnvs(envs: MutableMap<String, String>) {
        options.envs = envs
    }

    override fun getProgramParameters() = options.programParameters
    override fun getWorkingDirectory() = options.workingDirectory
    override fun isPassParentEnvs() = options.isPassParentEnvs
    override fun getEnvs() = options.envs
}