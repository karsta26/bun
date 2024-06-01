package com.karsta26.bun.run.before

import com.intellij.execution.BeforeRunTaskProvider
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.impl.EditConfigurationsDialog
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.lang.javascript.buildTools.HyperlinkListeningExecutionException
import com.intellij.lang.javascript.buildTools.base.JsbtUtil
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.util.PathUtil
import com.karsta26.bun.BunIcons
import com.karsta26.bun.run.BunConfigurationType
import com.karsta26.bun.run.BunRunConfiguration
import com.karsta26.bun.run.BunRunConfigurationValidator
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import java.io.File

val BUN_RUN_PROVIDER_ID = Key.create<BunBeforeRunTask>("BunBeforeRunTask")

class BunBeforeRunTaskProvider : BeforeRunTaskProvider<BunBeforeRunTask>() {
    override fun getId() = BUN_RUN_PROVIDER_ID
    override fun getName() = "Run Bun Tasks"
    override fun getIcon() = BunIcons.BunIcon
    override fun getTaskIcon(task: BunBeforeRunTask) = BunIcons.BunIcon
    override fun isConfigurable() = true
    override fun createTask(runConfiguration: RunConfiguration) = BunBeforeRunTask()

    override fun getDescription(task: BunBeforeRunTask): String {
        val taskState = task.state
        if (taskState.mySingleFileMode) {
            val jsFile = taskState.myJSFile ?: ""
            val fileName = PathUtil.getFileName(jsFile)
            return "Run file with Bun [$fileName]"
        } else {
            val path = taskState.myPackageJsonPath ?: ""
            val fileName = PathUtil.getFileName(path)
            val parentPath = PathUtil.getParentPath(path)
            val parentName = PathUtil.getFileName(parentPath)
            val folderAndFile = if (parentName.isEmpty()) fileName else parentName + File.separator + fileName
            val tasks = taskState.myScript?.split(" ")?.joinToString(", ") { "'$it'" }.orEmpty()
            return "Run Bun script: ${taskState.myCommand.command} $tasks [$folderAndFile]"
        }
    }

    override fun configureTask(
        context: DataContext,
        configuration: RunConfiguration,
        task: BunBeforeRunTask
    ): Promise<Boolean> {
        val dialog = BunBeforeRunTaskDialog(configuration as BunRunConfiguration, task.state)
        val dialogResult = dialog.showAndGet()
        val settingsChanged = dialogResult && dialog.settings != task.state
        if (settingsChanged) task.loadState(dialog.settings)
        return resolvedPromise(settingsChanged)
    }

    override fun canExecuteTask(configuration: RunConfiguration, task: BunBeforeRunTask): Boolean {
        return runCatching { BunRunConfigurationValidator.checkConfiguration(task.state) }.isSuccess
    }

    override fun executeTask(
        context: DataContext,
        configuration: RunConfiguration,
        env: ExecutionEnvironment,
        task: BunBeforeRunTask
    ): Boolean {

        try {
            BunRunConfigurationValidator.checkConfiguration(task.state)
        } catch (error: RuntimeConfigurationError) {
            handleInvalidConfiguration(error, env)
            return false
        }

        return executeBeforeRunTask(env, task)
    }

    private fun executeBeforeRunTask(
        env: ExecutionEnvironment,
        task: BunBeforeRunTask
    ): Boolean {
        val settings = RunManager.getInstance(env.project).createConfiguration("", BunConfigurationType::class.java)
        (settings.configuration as BunRunConfiguration).apply {
            options = task.state
            setGeneratedName()
        }
        return JsbtUtil.executeBeforeRunTask(env, settings)
    }

    private fun handleInvalidConfiguration(
        error: RuntimeConfigurationError,
        env: ExecutionEnvironment
    ) {
        val message = HtmlBuilder()
            .append(error.messageHtml).br()
            .appendLink("", "Edit run configuration")
            .toString()
        val exception = HyperlinkListeningExecutionException(message) {
            RunManager.getInstance(env.project).selectedConfiguration = env.runnerAndConfigurationSettings
            EditConfigurationsDialog(env.project).show()
        }
        ExecutionUtil.handleExecutionError(env.project, "Run", "Before launch Bun Tasks", exception)
    }
}
