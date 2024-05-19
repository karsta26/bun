package com.karsta26.bun.run.fragments

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.ui.components.textFieldWithBrowseButton

class WorkingDirectory(project: Project) {
    private val myWorkingDirectoryField = textFieldWithBrowseButton(
        browseDialogTitle = "Working Directory",
        project = project,
        fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
    )

    fun getComponent() = myWorkingDirectoryField
    fun getPath() = myWorkingDirectoryField.text
    fun setPath(path: String) {
        myWorkingDirectoryField.text = path
    }
}