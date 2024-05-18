package com.karsta26.bun.settings

import com.intellij.openapi.components.BaseState

class BunSettingsState : BaseState() {
    var executablePath by string()
}