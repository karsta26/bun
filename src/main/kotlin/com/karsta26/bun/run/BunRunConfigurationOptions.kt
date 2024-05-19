package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.XMap

class BunRunConfigurationOptions : LocatableRunConfigurationOptions(), CommonProgramRunConfigurationParameters {
    @get:Attribute("jsFile")
    var myJSFile: String? by string()

    @get:Attribute("packageJsonPath")
    var myPackageJsonPath: String? by string()

    @get:Attribute("singleFileMode")
    var mySingleFileMode: Boolean by property(true)

    @get:Attribute("programParameters")
    var myProgramParameters: String? by string()

    @get:Attribute("bunOptions")
    var myBunOptions: String? by string()

    @get:Attribute("workingDirectory")
    var myWorkingDirectory: String? by string()

    @get:XMap(
        propertyElementName = "envs",
        entryTagName = "env",
        keyAttributeName = "name"
    )
    var myEnvs: MutableMap<String, String> by linkedMap()

    @get:Attribute(value = "passParentEnvs")
    var myPassParentEnvs: Boolean by property(true)

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
        myPassParentEnvs = passParentEnvs
    }

    override fun getProgramParameters() = myProgramParameters
    override fun getEnvs() = myEnvs
    override fun getWorkingDirectory() = myWorkingDirectory
    override fun isPassParentEnvs() = myPassParentEnvs
}