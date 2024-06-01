package com.karsta26.bun.run.producer

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiFile
import com.karsta26.bun.run.BunRunConfiguration
import com.karsta26.bun.util.isBunSupported

object ProducerFromFile {
    fun setupFileConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val element = context.location?.psiElement as? PsiFile ?: return false
        val virtualFile = element.virtualFile

        if (isBunSupported(element)) {
            configuration.options.apply {
                mySingleFileMode = true
                myJSFile = element.name
                myWorkingDirectory = virtualFile.parent.path
            }
            configuration.setGeneratedName()
            return true
        }
        return false
    }

    fun isFileConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val element = context.location?.psiElement as? PsiFile ?: return false
        val virtualFile = element.virtualFile

        return isBunSupported(element) && with(configuration.options) {
            mySingleFileMode && myJSFile == element.name && myWorkingDirectory == virtualFile.parent.path
        }
    }
}