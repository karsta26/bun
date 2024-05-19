package com.karsta26.bun.tool

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.lang.javascript.buildTools.base.JsbtTaskFetchException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.PsiManagerEx

object BunScriptsUtil {

    fun listTasks(project: Project, packageJson: VirtualFile): BunScriptsStructure {
        return ApplicationManager.getApplication().runReadAction<BunScriptsStructure> {
            doBuildStructure(project, packageJson)
        }
    }

    private fun doBuildStructure(project: Project, packageJson: VirtualFile): BunScriptsStructure {
        if (!packageJson.isValid) {
            throw invalidFile(packageJson)
        }
        val structure = BunScriptsStructure(packageJson)
        findScriptsProperty(project, packageJson)?.let { scriptsProperty ->
            val scripts = (scriptsProperty.value as? JsonObject)
                ?.propertyList
                ?.map { property -> BunScript(structure, property.name) }
                ?.toMutableList()
                ?.apply { add(0, BunScript(structure, "install")) }

            if (scripts != null) {
                structure.scripts = scripts
            }
        }
        return structure
    }

    private fun invalidFile(packageJson: VirtualFile): JsbtTaskFetchException {
        return JsbtTaskFetchException.newBuildfileSyntaxError(packageJson)
    }

    private fun findScriptsProperty(project: Project, packageJson: VirtualFile): JsonProperty? {
        val psiFile = PsiManagerEx.getInstanceEx(project).findFile(packageJson)
            ?: throw invalidFile(packageJson)
        return findScriptsProperty(psiFile as? JsonFile)
    }

    private fun findScriptsProperty(jsonFile: JsonFile?): JsonProperty? {
        return (jsonFile?.topLevelValue as? JsonObject)?.findProperty("scripts")
    }
}