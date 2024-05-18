package com.karsta26.bun.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.karsta26.bun.settings.BunSettings

class BunRunProfileState(
    private val options: BunRunConfigurationOptions,
    environment: ExecutionEnvironment
) :
    CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val executablePath = BunSettings.getInstance(environment.project).executablePath
        val commandLine = GeneralCommandLine(executablePath, options.myScriptName)
            .withWorkDirectory(options.myWorkingDirectory)
            .withEnvironment(options.envs)
            .withParameters(options.myProgramParameters?.split(" ") ?: listOf())
        val processHandler = ProcessHandlerFactory.getInstance()
            .createColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }
}