package com.karsta26.bun.tool

import com.intellij.lang.javascript.buildTools.base.JsbtFileStructure
import com.intellij.openapi.vfs.VirtualFile

class BunScriptsStructure(packageJson: VirtualFile) : JsbtFileStructure(packageJson) {
    private var myScripts = listOf<BunScript>()
    override var taskNames = listOf<String>()

    var scripts: List<BunScript>
        get() = myScripts
        set(scripts) {
            myScripts = scripts.toList()
            taskNames = myScripts.map { it.name }
        }
}