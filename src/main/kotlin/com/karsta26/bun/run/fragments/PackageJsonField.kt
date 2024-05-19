package com.karsta26.bun.run.fragments

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.ui.components.textFieldWithBrowseButton

class PackageJsonField(project: Project) {
    @Suppress("DialogTitleCapitalization")
    private val myPackageJsonField = textFieldWithBrowseButton(
        browseDialogTitle = "Select package.json file",
        project = project,
        fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
    )

    fun getComponent() = myPackageJsonField
    fun getPath() = myPackageJsonField.text
    fun setPath(path: String) {
        myPackageJsonField.text = path
    }
}