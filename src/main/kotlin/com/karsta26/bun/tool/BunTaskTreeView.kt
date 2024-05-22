package com.karsta26.bun.tool

import com.intellij.execution.PsiLocation
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.buildTools.base.*
import com.intellij.lang.javascript.buildTools.npm.NpmScriptsUtil
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.karsta26.bun.BunIcons
import com.karsta26.bun.run.BunCommand
import javax.swing.tree.DefaultMutableTreeNode

class BunTaskTreeView(service: JsbtService, project: Project, layoutPlace: String?) :
    JsbtTaskTreeView(service, project, layoutPlace) {

    private val noScripts = "No scripts found"

    override fun addBuildfileChildren(node: DefaultMutableTreeNode, fileStructure: JsbtFileStructure) {
        if (fileStructure is BunScriptsStructure) {
            fileStructure.scripts
                .ifEmpty { listOf(noScripts) }
                .forEach { node.add(DefaultMutableTreeNode(it, false)) }
        }
    }

    override fun hasTaskNodes(node: DefaultMutableTreeNode): Boolean {
        return node.children().asSequence()
            .filterIsInstance<DefaultMutableTreeNode>()
            .any { it.userObject is BunScript }
    }

    override fun customizeCell(project: Project, renderer: ColoredTreeCellRenderer, node: DefaultMutableTreeNode) {
        when (val userObject = node.userObject) {
            is BunScriptsStructure -> {
                with(renderer) {
                    icon = BunIcons.BunIcon
                    isIconOnTheRight = false
                    append(userObject.presentablePath)
                }
            }

            is BunScript -> {
                with(renderer) {
                    icon = when (userObject.name) {
                        BunCommand.INSTALL.command -> AllIcons.Actions.Install
                        else -> AllIcons.Nodes.PackageLocal
                    }
                    append(userObject.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                }
            }

            noScripts -> renderer.append(noScripts, SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

    override fun getPersistentId(node: DefaultMutableTreeNode): String? {
        return when (val userObject = node.userObject) {
            is BunScriptsStructure -> userObject.buildfile.path
            is BunScript -> userObject.name
            else -> null
        }
    }

    override fun getPresentableTaskName(node: DefaultMutableTreeNode): String? {
        return when (val userObject = node.userObject) {
            is BunScript -> userObject.name
            else -> null
        }
    }

    override fun createTaskSetFromSelectedNodes(): JsbtTaskSet? {
        val scripts = selectedNodes.mapNotNull { it.userObject as? BunScript }
        val structure = scripts.map { it.structure }.distinct().singleOrNull()
        return structure?.let { JsbtTaskSet(it, scripts.map(BunScript::name)) }
    }

    override fun createJumpToSourceDescriptor(project: Project, node: DefaultMutableTreeNode): Navigatable? {
        return (node.userObject as? BunScript)?.let { script ->
            val property = NpmScriptsUtil.findScriptProperty(project, script.structure.buildfile, script.name)
            return property?.let { PsiLocation.fromPsiElement(it).openFileDescriptor }
        }
    }

    override fun compareNodes(
        node1: DefaultMutableTreeNode,
        node2: DefaultMutableTreeNode,
        sortingMode: JsbtSortingMode
    ): Int {
        val script1 = node1.userObject as? BunScript
        val script2 = node2.userObject as? BunScript

        if (script1 == null && script2 == null) return 0
        if (script1 == null) return 1
        if (script2 == null) return -1

        return when (sortingMode) {
            JsbtSortingMode.NAME -> script1.name.compareTo(script2.name)
            else -> script1.structure.scripts.indexOf(script1) - script2.structure.scripts.indexOf(script2)
        }
    }
}