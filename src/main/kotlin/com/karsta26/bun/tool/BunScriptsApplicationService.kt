package com.karsta26.bun.tool

import com.intellij.lang.javascript.buildTools.base.JsbtApplicationService
import com.intellij.openapi.project.Project
import com.karsta26.bun.BunIcons

class BunScriptsApplicationService : JsbtApplicationService() {
    override fun getProjectService(project: Project) = BunScriptsService.getInstance(project)
    override fun getName() = "Bun"
    override fun getIcon() = BunIcons.BunIcon
    override fun getBuildfileCommonName() = "package.json"

    object Util {
        @JvmStatic
        fun getInstance(): BunScriptsApplicationService =
            EP_NAME.findExtensionOrFail(BunScriptsApplicationService::class.java)
    }
}