package com.karsta26.bun.run

import com.intellij.execution.configurations.RuntimeConfigurationError
import java.io.File

object BunRunConfigurationValidator {
    fun checkConfiguration(options: BunRunConfigurationOptions) {
        with(options) {
            if (mySingleFileMode) {
                if (workingDirectory == null || workingDirectory!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify working directory")
                }
                if (!File(workingDirectory!!).isDirectory) {
                    throw RuntimeConfigurationError("Please specify working directory correctly")
                }
                if (myJSFile == null || myJSFile!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify JS/TS file")
                }
                if (java.nio.file.Path.of(myJSFile!!).isAbsolute) {
                    if (!java.nio.file.Path.of(myJSFile!!).toFile().isFile) {
                        throw RuntimeConfigurationError("Please specify JS/TS file correctly")
                    }
                } else {
                    if (!java.nio.file.Path.of(myWorkingDirectory!!, myJSFile).toFile().isFile) {
                        throw RuntimeConfigurationError("JS/TS file not found in working directory")
                    }
                }
            } else {
                if (myPackageJsonPath == null || myPackageJsonPath!!.isBlank()) {
                    throw RuntimeConfigurationError("Please specify package.json")
                }
                val file = File(myPackageJsonPath!!)
                if (!file.isAbsolute || !file.isFile) {
                    throw RuntimeConfigurationError("Please specify package.json correctly")
                }
            }
        }
    }
}