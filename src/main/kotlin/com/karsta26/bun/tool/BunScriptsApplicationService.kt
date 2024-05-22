package com.karsta26.bun.tool

import com.intellij.lang.javascript.buildTools.base.JsbtApplicationService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.karsta26.bun.BunIcons

class BunScriptsApplicationService : JsbtApplicationService() {
    override fun getProjectService(project: Project) = project.service<BunScriptsService>()
    override fun getName() = "Bun"
    override fun getIcon() = BunIcons.BunIcon
    override fun getBuildfileCommonName() = "package.json"

    companion object {
        fun getInstance() = EP_NAME.findExtensionOrFail(BunScriptsApplicationService::class.java)
    }
}