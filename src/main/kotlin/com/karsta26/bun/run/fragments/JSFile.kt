package com.karsta26.bun.run.fragments

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.textFieldWithBrowseButton
import java.nio.file.Path

class JSFile(project: Project, private val workingDirectoryField: WorkingDirectory) {
    private val myJSFileField: TextFieldWithBrowseButton = textFieldWithBrowseButton(
        browseDialogTitle = "Select File To Run",
        project = project,
        fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
        fileChosen = {
            val workingDir = workingDirectoryField.getPath()
            if (Path.of(workingDir, Path.of(it.path).fileName.toString()).toFile().isFile) {
                return@textFieldWithBrowseButton Path.of(it.path).fileName.toString()
            }
            return@textFieldWithBrowseButton Path.of(it.path).toString()
        }
    )

    fun getComponent() = myJSFileField
    fun getFile() = myJSFileField.text
    fun setFile(file: String) {
        myJSFileField.text = file
    }
}