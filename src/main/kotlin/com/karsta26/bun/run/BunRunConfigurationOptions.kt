package com.karsta26.bun.run

import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class BunRunConfigurationOptions : LocatableRunConfigurationOptions() {
    private val myScriptName: StoredProperty<String?> = string("").provideDelegate(this, "scriptName")

    fun getScriptName(): String? {
        return myScriptName.getValue(this)
    }

    fun setScriptName(scriptName: String) {
        myScriptName.setValue(this, scriptName)
    }
}