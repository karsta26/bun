package com.karsta26.bun.run.debug

import io.netty.channel.Channel
import org.jetbrains.concurrency.Promise
import org.jetbrains.debugger.DebugEventListener
import org.jetbrains.debugger.MessagingLogger
import org.jetbrains.debugger.StandaloneVmHelper
import org.jetbrains.jsonProtocol.Request
import org.jetbrains.wip.StandaloneWipVm

class BunWipVm(tabListener: DebugEventListener, url: String?, channel: Channel, debugMessageQueue: MessagingLogger?) :
    StandaloneWipVm(tabListener, url, channel, debugMessageQueue) {
    override fun initDomains(): Promise<*> {
        val domains = super.initDomains()
        return domains
    }

    override fun write(message: Request<*>): Boolean {
        return super.write(message)
    }

    override val debugListener: DebugEventListener
        get() = super.debugListener

    override val attachStateManager: StandaloneVmHelper
        get() = super.attachStateManager
}