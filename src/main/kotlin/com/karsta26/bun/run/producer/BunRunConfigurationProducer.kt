package com.karsta26.bun.run.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.runConfigurationType
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.karsta26.bun.run.BunConfigurationType
import com.karsta26.bun.run.BunRunConfiguration
import com.karsta26.bun.run.producer.ProducerFromFile.isFileConfigurationFromContext
import com.karsta26.bun.run.producer.ProducerFromFile.setupFileConfigurationFromContext
import com.karsta26.bun.run.producer.ProducerFromPackageJson.isScriptConfigurationFromContext
import com.karsta26.bun.run.producer.ProducerFromPackageJson.setupScriptConfigurationFromContext

class BunRunConfigurationProducer : LazyRunConfigurationProducer<BunRunConfiguration>() {

    override fun getConfigurationFactory() = runConfigurationType<BunConfigurationType>()

//TODO do this only if bun is selected for the project
//    override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext) =
//        other.configuration is NpmRunConfiguration

    override fun setupConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        return setupFileConfigurationFromContext(configuration, context) ||
                setupScriptConfigurationFromContext(configuration, context)
    }

    override fun isConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        return isFileConfigurationFromContext(configuration, context) ||
                isScriptConfigurationFromContext(configuration, context)
    }
}