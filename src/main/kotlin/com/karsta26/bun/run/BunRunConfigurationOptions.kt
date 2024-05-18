package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.XMap

class BunRunConfigurationOptions : LocatableRunConfigurationOptions(), CommonProgramRunConfigurationParameters {
    @get:Attribute("scriptName")
    var myScriptName: String? by string()

    @get:Attribute("programParameters")
    var myProgramParameters: String? by string()

    @get:Attribute("workingDirectory")
    var myWorkingDirectory: String? by string()

    @get:XMap(propertyElementName = "env")
    var myEnvs: MutableMap<String, String> by linkedMap()

    override fun getProject(): Project {
        TODO("Not yet implemented")
    }

    override fun setProgramParameters(value: String?) {
        myProgramParameters = value
    }

    override fun setWorkingDirectory(value: String?) {
        myWorkingDirectory = value
    }

    override fun setEnvs(envs: MutableMap<String, String>) {
        myEnvs = envs
    }

    override fun setPassParentEnvs(passParentEnvs: Boolean) {

    }

    override fun getProgramParameters() = myProgramParameters
    override fun getEnvs() = myEnvs
    override fun getWorkingDirectory() = myWorkingDirectory
    override fun isPassParentEnvs() = true
}