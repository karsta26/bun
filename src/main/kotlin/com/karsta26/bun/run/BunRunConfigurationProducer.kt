package com.karsta26.bun.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.runConfigurationType
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.karsta26.bun.util.FileTypeUtil.isBunSupported

class BunRunConfigurationProducer : LazyRunConfigurationProducer<BunRunConfiguration>() {
    override fun getConfigurationFactory() = runConfigurationType<BunConfigurationType>()

    override fun setupConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val element = context.location?.psiElement

        if (element is PsiFile) {
            val fileType = element.fileType
            val virtualFile = element.virtualFile

            if (isBunSupported(virtualFile, fileType)) {
                configuration.options.apply {
                    mySingleFileMode = true
                    myJSFile = element.name
                    myWorkingDirectory = virtualFile.parent.path
                }
                configuration.setGeneratedName()
                return true
            }
        }
        return false
    }

    override fun isConfigurationFromContext(
        configuration: BunRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val element = context.location?.psiElement

        if (element is PsiFile) {
            val fileType = element.fileType
            val virtualFile = element.virtualFile

            if (isBunSupported(virtualFile, fileType)) {
                configuration.options.apply {
                    if (!mySingleFileMode) {
                        return false
                    }
                    if (myJSFile != element.name) {
                        return false
                    }
                    if (myWorkingDirectory != virtualFile.parent.path) {
                        return false
                    }
                }
                return true
            }
        }
        return false
    }
}