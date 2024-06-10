package com.karsta26.bun.run.debug

import io.netty.channel.Channel
import org.jetbrains.concurrency.Promise
import org.jetbrains.debugger.DebugEventListener
import org.jetbrains.debugger.ExceptionCatchMode
import org.jetbrains.debugger.MessagingLogger
import org.jetbrains.wip.StandaloneWipVm

class BunWipVm(
    tabListener: DebugEventListener,
    url: String?,
    channel: Channel,
    val debugMessageQueue: MessagingLogger?
) :
    StandaloneWipVm(tabListener, url, channel, debugMessageQueue) {
    override fun initDomains(): Promise<*> {
        val domains = super.initDomains()
        commandProcessor.send(org.jetbrains.wip.protocol.inspector.Enable())
        commandProcessor.send(org.jetbrains.wip.protocol.runtime.Enable())
        commandProcessor.send(org.jetbrains.wip.protocol.debugger.Pause())
        commandProcessor.send(org.jetbrains.wip.protocol.debugger.SetBreakpointsActive(true))
        setBreakOnException(ExceptionCatchMode.ALL)
        commandProcessor.send(org.jetbrains.wip.protocol.console.Enable())
        commandProcessor.send(org.jetbrains.wip.protocol.debugger.Resume())
        return domains
    }
}