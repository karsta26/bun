package com.karsta26.bun.run.fragments

import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.panels.HorizontalLayout
import javax.swing.ButtonGroup
import javax.swing.JPanel

class RunMode {
    var script: JBRadioButton = JBRadioButton("Script").apply { setSelected(true) }
    var singleFile: JBRadioButton = JBRadioButton("Single file")
    private var runModeButtons: JPanel

    init {
        ButtonGroup().apply {
            add(script)
            add(singleFile)
        }
        runModeButtons = JPanel(HorizontalLayout(10)).apply {
            add(script)
            add(singleFile)
        }
    }

    fun getComponent(): JPanel {
        return runModeButtons
    }

    fun setSingleFileMode(singleFileMode: Boolean) {
        singleFile.setSelected(singleFileMode)
        script.setSelected(!singleFileMode)
    }

    fun isSingleFileMode() = singleFile.isSelected
}