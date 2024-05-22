package com.karsta26.bun.run.fragments

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SimpleListCellRenderer
import com.karsta26.bun.run.BunCommand
import javax.swing.DefaultComboBoxModel

class BunCommandField {

    private val commandField = ComboBox<BunCommand>()
    var item: BunCommand
        get() = commandField.item
        set(value) {
            commandField.item = value
        }

    init {
        commandField.setRenderer(SimpleListCellRenderer.create("", BunCommand::command))
        commandField.model = DefaultComboBoxModel(BunCommand.entries.sorted().toTypedArray()).apply {
            selectedItem = BunCommand.RUN
        }
    }

    fun getComponent() = commandField

    fun toggleRunMode(isScriptMode: Boolean) {
        commandField.isEnabled = isScriptMode
        if (!isScriptMode) item = BunCommand.RUN
    }
}