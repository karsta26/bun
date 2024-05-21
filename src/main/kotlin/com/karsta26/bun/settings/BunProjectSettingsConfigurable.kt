package com.karsta26.bun.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import com.karsta26.bun.BunBundle

class BunProjectSettingsConfigurable(private val project: Project) :
    BoundSearchableConfigurable(BunBundle.message("bun.name"), BunBundle.message("bun.name"), "Settings.Bun") {

    private val settings
        get() = project.service<BunSettings>()

    override fun createPanel(): DialogPanel {
        return panel {
            row(BunBundle.message("bun.settings.exec.description")) {
                textFieldWithBrowseButton(
                    browseDialogTitle = BunBundle.message("bun.settings.exec.dialog.description"),
                    project = project,
                    fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()
                )
                    .bindText(settings::executablePath)
                    .columns(COLUMNS_LARGE)
            }
        }
    }

    override fun apply() {
        settings.update {
            super.apply()
        }
    }
}