package com.karsta26.bun.run

import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import com.karsta26.bun.BunIcons
import com.karsta26.bun.MyBundle

class BunConfigurationType : SimpleConfigurationType(
    "BunConfigurationType",
    MyBundle.message("bun.name"),
    MyBundle.message("bun.run.description"),
    NotNullLazyValue.createConstantValue(BunIcons.BunIcon)
) {
    override fun createTemplateConfiguration(project: Project) = BunRunConfiguration(project, this)
    override fun getOptionsClass() = BunRunConfigurationOptions::class.java
}