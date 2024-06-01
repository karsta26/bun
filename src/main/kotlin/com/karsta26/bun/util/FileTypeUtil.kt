package com.karsta26.bun.util

import com.intellij.lang.ecmascript6.JSXHarmonyFileType
import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

fun isBunSupported(psiFile: PsiFile) = isBunSupported(psiFile.virtualFile, psiFile.fileType)

private fun isBunSupported(virtualFile: VirtualFile, fileType: FileType): Boolean {
    return isJavaScriptFile(virtualFile, fileType) || isTypeScriptFile(virtualFile)
}

private fun isJavaScriptFile(virtualFile: VirtualFile, fileType: FileType): Boolean {
    return TypeScriptUtil.isJavaScriptFile(virtualFile.name) || fileType is JavaScriptFileType || fileType is JSXHarmonyFileType
}

private fun isTypeScriptFile(virtualFile: VirtualFile) = TypeScriptUtil.isTypeScriptFile(virtualFile)