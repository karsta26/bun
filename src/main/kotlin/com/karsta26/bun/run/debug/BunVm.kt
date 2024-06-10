package com.karsta26.bun.run.debug

import com.jetbrains.debugger.wip.PageConnection
import com.jetbrains.debugger.wip.WipRemoteVmConnection
import io.netty.channel.Channel
import org.jetbrains.concurrency.Promise
import org.jetbrains.debugger.DebugEventListener
import org.jetbrains.debugger.MessagingLogger
import org.jetbrains.wip.StandaloneWipVm

class BunVm() : WipRemoteVmConnection() {
    override fun createVm(
        page: PageConnection,
        channel: Channel,
        debugMessageQueue: MessagingLogger?
    ): StandaloneWipVm {
        return super.createVm(page, channel, debugMessageQueue)
    }
}