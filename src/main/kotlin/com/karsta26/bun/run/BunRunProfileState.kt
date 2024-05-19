package com.karsta26.bun.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.GeneralCommandLine.ParentEnvironmentType
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ProgramParametersConfigurator.expandMacros
import com.karsta26.bun.settings.BunSettings
import java.nio.file.Path

class BunRunProfileState(
    private val options: BunRunConfigurationOptions,
    environment: ExecutionEnvironment
) :
    CommandLineState(environment) {

    private val colorEnvironmentVariables = mapOf(
        "DEBUG_COLORS" to "true",
        "COLORTERM" to "true",
        "FORCE_COLOR" to "true",
        "npm_config_color" to "always",
        "MOCHA_COLORS" to "1"
    )

    override fun startProcess(): ProcessHandler {
        val executablePath = BunSettings.getInstance(environment.project).executablePath
        val commands = mutableListOf(executablePath)
        options.myBunOptions?.let { commands.addAll(it.split(" ").map(::expandMacros)) }
        options.myCommand?.let { commands.addAll(it.split(" ")) }
        if (options.mySingleFileMode) {
            options.myJSFile?.let { commands.add(it) }
        } else {
            options.myScript?.let { commands.addAll(it.split(" ")) }
        }
        val commandLine = GeneralCommandLine(commands)
            .withEnvironment(options.envs)
            .withParameters(options.myProgramParameters?.split(" ").orEmpty().map(::expandMacros))
            .withParentEnvironmentType(if (options.isPassParentEnvs) ParentEnvironmentType.CONSOLE else ParentEnvironmentType.NONE)
            .withEnvironment(colorEnvironmentVariables)

        if (options.mySingleFileMode) {
            commandLine.setWorkDirectory(options.myWorkingDirectory)
        } else {
            commandLine.workDirectory = Path.of(options.myPackageJsonPath!!).parent.toFile()
        }

        val processHandler = ProcessHandlerFactory.getInstance()
            .createColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }
}