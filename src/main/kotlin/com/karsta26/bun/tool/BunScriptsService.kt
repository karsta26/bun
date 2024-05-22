package com.karsta26.bun.tool

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.runConfigurationType
import com.intellij.lang.javascript.buildTools.base.*
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.karsta26.bun.BunIcons
import com.karsta26.bun.run.BunCommand.INSTALL
import com.karsta26.bun.run.BunCommand.RUN
import com.karsta26.bun.run.BunConfigurationType
import com.karsta26.bun.run.BunRunConfiguration
import com.karsta26.bun.run.BunRunConfigurationOptions
import com.karsta26.bun.tool.BunScriptsUtil.listTasks
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Service(Service.Level.PROJECT)
class BunScriptsService(project: Project) : JsbtService(project) {

    override fun getApplicationService() = BunScriptsApplicationService.getInstance()
    override fun getFileManager() = myProject.service<BunFileManager>()
    override fun createTaskTreeView(layoutPlace: String?) = BunTaskTreeView(this, myProject, layoutPlace)
    override fun isBuildfile(file: VirtualFile) = PackageJsonUtil.isPackageJsonFile(file)
    override fun createEmptyFileStructure(buildfile: VirtualFile) = BunScriptsStructure(buildfile)
    override fun getConfigurationFactory() = runConfigurationType<BunConfigurationType>()
    override fun createToolWindowManager() =
        JsbtToolWindowManager(myProject, "bun", BunIcons.BunIcon, "reference.tool.window.bun", this)

    override fun detectAllBuildfiles(): MutableList<VirtualFile> {
        if (DumbService.isDumb(myProject)) {
            return detectAllBuildfilesInContentRoots(webModulesOnly = false, filterOutEmptyBuildfiles = false)
        }
        return ReadAction.compute(ThrowableComputable<MutableList<VirtualFile>, RuntimeException> {
            if (myProject.isDisposed) {
                mutableListOf()
            } else {
                val scope = JSLibraryUtil.getContentScopeWithoutLibraries(myProject)
                val files = FilenameIndex.getVirtualFilesByName("package.json", scope)
                files.toMutableList()
            }
        })
    }

    override fun detectAllBuildfilesInContentRoots(
        webModulesOnly: Boolean,
        filterOutEmptyBuildfiles: Boolean
    ): MutableList<VirtualFile> {
        return JsbtUtil.detectAllBuildfilesInContentRoots(
            myProject,
            webModulesOnly,
            "package",
            arrayOf("json"),
            false
        )
            .filter { !filterOutEmptyBuildfiles || hasScripts(myProject, it) }
            .toMutableList()
    }

    override fun fetchBuildfileStructure(packageJson: VirtualFile): JsbtFileStructure {
        return ReadAction.compute<BunScriptsStructure, JsbtTaskFetchException> {
            myProject.takeUnless { it.isDisposed }
                ?: throw JsbtTaskFetchException.newGenericException(packageJson, "$myProject is disposed")
            packageJson.takeIf { it.isValid }
                ?: throw JsbtTaskFetchException.newBuildfileSyntaxError(packageJson)
            PsiManager.getInstance(myProject).findFile(packageJson)
                ?: throw JsbtTaskFetchException.newGenericException(packageJson, "Cannot find package.json PSI file")
            listTasks(myProject, packageJson)
        }
    }

    override fun isConfigurationMatched(runConfiguration: RunConfiguration, options: Any): Boolean {
        if (runConfiguration !is BunRunConfiguration) return false
        val runOptions = runConfiguration.options

        return when (options) {
            is BunRunConfigurationOptions -> options.isEqualTo(runOptions)
            is JsbtTaskSet -> {
                val taskNamesAreEqual = when (runOptions.myCommand) {
                    RUN -> JsbtUtil.equalsOrderless(
                        options.taskNames,
                        runOptions.myScript?.split(" ").orEmpty()
                    )

                    INSTALL -> options.name == INSTALL.command
                    else -> false
                }
                taskNamesAreEqual && pathAreEqual(options, runOptions)
            }

            else -> false
        }
    }

    override fun setupRunConfiguration(runConfiguration: RunConfiguration, taskSet: JsbtTaskSet) {
        val bunRunConfiguration = runConfiguration as BunRunConfiguration
        bunRunConfiguration.options.apply {
            mySingleFileMode = false
            myPackageJsonPath = taskSet.structure.buildfile.path
            myCommand = if (taskSet.name == INSTALL.command) INSTALL else RUN
            myScript = if (taskSet.name == INSTALL.command) myScript else taskSet.taskNames.joinToString(" ")
        }
        bunRunConfiguration.setGeneratedName()
    }

    override fun showTaskListingSettingsDialog(file: VirtualFile?): Boolean {
        editConfigurations()
        return true
    }

    private fun hasScripts(project: Project, packageJson: VirtualFile) =
        runCatching { listTasks(project, packageJson).taskNames.isNotEmpty() }
            .getOrElse { false }

    private fun pathAreEqual(
        options: JsbtTaskSet,
        runOptions: BunRunConfigurationOptions
    ) = Path(options.structure.buildfile.path).pathString == Path(runOptions.myPackageJsonPath.orEmpty()).pathString
}