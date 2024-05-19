package com.karsta26.bun.util

import com.intellij.lang.ecmascript6.JSXHarmonyFileType
import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile

object FileTypeUtil {

    fun isBunSupported(virtualFile: VirtualFile, fileType: FileType): Boolean {
        return virtualFile.extension!!.matches(Regex("js|ts|jsx|cjs|mjs"))
//        return isJavaScriptFile(virtualFile, fileType) || isTypeScriptFile(virtualFile)
    }

    private fun isJavaScriptFile(virtualFile: VirtualFile, fileType: FileType): Boolean {
        return TypeScriptUtil.isJavaScriptFile(virtualFile.name) || fileType is JavaScriptFileType || fileType is JSXHarmonyFileType
    }

    private fun isTypeScriptFile(virtualFile: VirtualFile): Boolean {
        return TypeScriptUtil.isTypeScriptFile(virtualFile)
    }
}