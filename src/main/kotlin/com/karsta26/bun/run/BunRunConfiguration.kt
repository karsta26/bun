package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File
import java.nio.file.Path

class BunRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<BunRunConfigurationOptions>(project, factory), CommonProgramRunConfigurationParameters {

    public override fun getOptions(): BunRunConfigurationOptions {
        return super.getOptions() as BunRunConfigurationOptions
    }

    override fun suggestedName(): String {
        if (options.mySingleFileMode && options.myJSFile != null) {
            return Path.of(options.myJSFile!!).fileName.toString()
        }
        return (options.myCommand ?: "") + " " + (options.myScript ?: "")
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return BunRunProfileState(options, environment)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
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
        with(options) {
            if (mySingleFileMode) {
                if (workingDirectory == null || workingDirectory!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify working directory")
                }
                if (!File(workingDirectory!!).isDirectory) {
                    throw RuntimeConfigurationError("Please specify working directory correctly")
                }
                if (myJSFile == null || myJSFile!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify JS/TS file")
                }
                if (Path.of(myJSFile!!).isAbsolute) {
                    if (!Path.of(myJSFile!!).toFile().isFile) {
                        throw RuntimeConfigurationError("Please specify JS/TS file correctly")
                    }
                } else {
                    if (!Path.of(myWorkingDirectory!!, myJSFile).toFile().isFile) {
                        throw RuntimeConfigurationError("JS/TS file not found in working directory")
                    }
                }
            } else {
                if (myPackageJsonPath == null || myPackageJsonPath!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify package.json")
                }
                val file = File(myPackageJsonPath!!)
                if (!file.isAbsolute || !file.isFile) {
                    throw RuntimeConfigurationError("Please specify package.json correctly")
                }
            }
        }
    }
}
