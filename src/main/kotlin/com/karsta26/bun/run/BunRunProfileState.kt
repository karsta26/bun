package com.karsta26.bun.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.karsta26.bun.settings.BunSettings

class BunRunProfileState(
    private val project: Project,
    private val options: BunRunConfigurationOptions,
    environment: ExecutionEnvironment
) :
    CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val executablePath = BunSettings.getInstance(project).executablePath
        val commandLine = GeneralCommandLine(executablePath, options.getScriptName())
        val processHandler = ProcessHandlerFactory.getInstance()
            .createColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }
}