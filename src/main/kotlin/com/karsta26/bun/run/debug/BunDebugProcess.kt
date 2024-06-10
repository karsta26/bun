package com.karsta26.bun.run.debug

import com.intellij.execution.ExecutionResult
import com.intellij.javascript.debugger.DebuggableFileFinder
import com.intellij.javascript.debugger.JavaScriptDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.debugger.wip.WipWebSocketConnection
import org.jetbrains.debugger.Vm

class BunDebugProcess(session: XDebugSession, finder: DebuggableFileFinder, connection: BunVmConnection, executionResult: ExecutionResult?) :
    JavaScriptDebugProcess<BunVmConnection>(session, finder, connection, executionResult) {

    override fun beforeInitBreakpoints(vm: Vm) {
        super.beforeInitBreakpoints(vm)
    }
}