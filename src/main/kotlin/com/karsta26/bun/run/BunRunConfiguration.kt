package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import java.nio.file.Path

class BunRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<BunRunConfigurationOptions>(project, factory), CommonProgramRunConfigurationParameters {

    public override fun getOptions(): BunRunConfigurationOptions {
        return super.getOptions() as BunRunConfigurationOptions
    }

    fun setOptions(options: BunRunConfigurationOptions) {
        loadState(options)
    }

    override fun suggestedName(): String {
        if (options.mySingleFileMode && options.myJSFile != null) {
            return Path.of(options.myJSFile!!).fileName.toString()
        }
        return listOfNotNull(options.myCommand.command, options.myScript).joinToString(" ").trim()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return BunRunProfileState(options, environment)
    }

    override fun getConfigurationEditor(): BunSettingsEditor {
        return BunSettingsEditor(this)
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

    override fun checkConfiguration() {
        BunRunConfigurationValidator.checkConfiguration(options)
    }
}
