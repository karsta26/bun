package com.karsta26.bun

import com.intellij.openapi.fileTypes.FileType

class BunLockFileType : FileType {
    override fun getName() = "Bun lock file"
    override fun getDescription() = "Bun lock file"
    override fun getDefaultExtension() = "lockb"
    override fun getIcon() = BunIcons.BunIcon
    override fun isBinary() = true
}
