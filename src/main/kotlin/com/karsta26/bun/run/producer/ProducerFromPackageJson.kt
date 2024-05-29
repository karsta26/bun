package com.karsta26.bun.run.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.json.psi.JsonProperty
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.karsta26.bun.run.BunCommand
import com.karsta26.bun.run.BunRunConfiguration

object ProducerFromPackageJson {

    fun setupScriptConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val (packageJson, scriptProperty) = getScriptData(context) ?: return false

        configuration.options.apply {
            myPackageJsonPath = packageJson.path
            myCommand = BunCommand.RUN
            mySingleFileMode = false
            myScript = scriptProperty.name
        }
        configuration.setGeneratedName()
        return true
    }

    fun isScriptConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val (packageJson, scriptProperty) = getScriptData(context) ?: return false

        return with(configuration.options) {
            !mySingleFileMode &&
                    myPackageJsonPath == packageJson.path &&
                    myCommand == BunCommand.RUN &&
                    myScript == scriptProperty.name
        }
    }

    private fun findContainingScriptProperty(element: PsiElement): JsonProperty? {
        val scriptProperty = PackageJsonUtil.findContainingProperty(element)
        val scriptsProperty = PackageJsonUtil.findContainingTopLevelProperty(scriptProperty) ?: return null
        return if ("scripts" == scriptsProperty.name && scriptProperty !== scriptsProperty) scriptProperty else null
    }

    private fun getScriptData(context: ConfigurationContext): ScriptData? {
        val element = context.location?.psiElement ?: return null
        if (!element.isValid) return null

        val psiPackageJson = PackageJsonUtil.getContainingPackageJsonFile(element) ?: return null
        val virtualPackageJson = psiPackageJson.virtualFile ?: return null
        val scriptProperty = findContainingScriptProperty(element) ?: return null

        return ScriptData(virtualPackageJson, scriptProperty)
    }

    private data class ScriptData(val virtualPackageJson: VirtualFile, val scriptProperty: JsonProperty)
}