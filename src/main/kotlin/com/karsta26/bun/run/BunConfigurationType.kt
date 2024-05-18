package com.karsta26.bun.run

import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import com.karsta26.bun.BunBundle
import com.karsta26.bun.BunIcons

class BunConfigurationType : SimpleConfigurationType(
    "BunConfigurationType",
    BunBundle.message("bun.name"),
    BunBundle.message("bun.run.description"),
    NotNullLazyValue.createConstantValue(BunIcons.BunIcon)
) {
    override fun createTemplateConfiguration(project: Project) = BunRunConfiguration(project, this)
    override fun getOptionsClass() = BunRunConfigurationOptions::class.java
}