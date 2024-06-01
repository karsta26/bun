package com.karsta26.bun.tool

import com.intellij.lang.javascript.buildTools.base.JsbtFileManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class BunFileManager(project: Project) : JsbtFileManager(project, BunScriptsService.getInstance(project)) {
    companion object {
        @JvmStatic
        fun getInstance(project: Project): BunFileManager = project.service()
    }
}