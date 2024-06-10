package com.karsta26.bun.run

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.debugger.DebuggableFileFinderImpl
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.debugger.wip.WipWebSocketConnection
import com.karsta26.bun.run.debug.BunDebugProcess
import com.karsta26.bun.run.debug.BunVmConnection
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.net.InetSocketAddress
import java.nio.file.Path

class BunRunConfiguration(project: Project, factory: ConfigurationFactory) :
    LocatableConfigurationBase<BunRunConfigurationOptions>(project, factory), CommonProgramRunConfigurationParameters,
    DebuggableRunConfiguration {

    public override fun getOptions(): BunRunConfigurationOptions {
        return super.getOptions() as BunRunConfigurationOptions
    }

    fun setOptions(options: BunRunConfigurationOptions) {
        loadState(options)
    }

    override fun suggestedName(): String {
        if (options.mySingleFileMode && options.myJSFile != null) {
            return Path.of(options.myJSFile!!).fileName.toString()
        }
        return listOfNotNull(options.myCommand.command, options.myScript).joinToString(" ").trim()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return BunRunProfileState(options, environment)
    }

    override fun getConfigurationEditor(): BunSettingsEditor {
        return BunSettingsEditor(this)
    }

    override fun setProgramParameters(value: String?) {
        options.programParameters = value
    }

    override fun setWorkingDirectory(value: String?) {
        options.workingDirectory = value
    }

    override fun setPassParentEnvs(passParentEnvs: Boolean) {
        options.isPassParentEnvs = passParentEnvs
    }

    override fun setEnvs(envs: MutableMap<String, String>) {
        options.envs = envs
    }

    override fun getProgramParameters() = options.programParameters
    override fun getWorkingDirectory() = options.workingDirectory
    override fun isPassParentEnvs() = options.isPassParentEnvs
    override fun getEnvs() = options.envs

    override fun checkConfiguration() {
        BunRunConfigurationValidator.checkConfiguration(options)
    }

    override fun computeDebugAddress(state: RunProfileState?): InetSocketAddress {
        return InetSocketAddress("localhost", 6499)
    }

    override fun createDebugProcess(
        socketAddress: InetSocketAddress,
        session: XDebugSession,
        executionResult: ExecutionResult?,
        environment: ExecutionEnvironment
    ): XDebugProcess {

        val state = BunRunProfileState(options, environment, true)
        val s = state.startProcess()

        val bunDebugProcess =
            BunDebugProcess(
                session,
                DebuggableFileFinderImpl(project, null),
                BunVmConnection(),
                DefaultExecutionResult(s)
            )

        val connection = bunDebugProcess.connection
        connection.stateChanged {
            println(it.message)
            println(it.status)
        }

        connection.executeOnStart {
            println("executeOnStart")
        }


//        int asyncCallStackDepth = Registry.get("js.debugger.async.call.stack.depth").asInteger();
//        vm.getCommandProcessor().send((Request)Debugger.SetAsyncCallStackDepth(1));

        connection.open(socketAddress)

        return bunDebugProcess
    }
}
