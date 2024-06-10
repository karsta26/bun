package com.karsta26.bun.run.debug

import com.jetbrains.debugger.wip.PageConnection
import com.jetbrains.debugger.wip.WipRemoteVmConnection
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.thenRun
import org.jetbrains.debugger.MessagingLogger
import org.jetbrains.debugger.Vm
import org.jetbrains.io.NettyUtil
import org.jetbrains.io.webSocket.WebSocketProtocolHandler
import org.jetbrains.io.webSocket.WebSocketProtocolHandshakeHandler
import org.jetbrains.wip.StandaloneWipVm
import org.jetbrains.wip.WipVm
import org.jetbrains.wip.protocol.inspector.DetachedEventData
import java.net.InetSocketAddress
import java.net.URI

class BunVmConnection : WipRemoteVmConnection() {

    override fun connectDebugger(
        page: PageConnection,
        context: ChannelHandlerContext,
        result: AsyncPromise<WipVm>,
        debugMessageQueue: MessagingLogger?
    ) {
        val url = URI.create(page.webSocketDebuggerUrl!!)

        val handshaker: WebSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
            url, WebSocketVersion.V13, null, true, EmptyHttpHeaders.INSTANCE
        )
        val channel = context.channel()

        val vm = createVm(page, channel, debugMessageQueue)
        vm.title = page.title
        vm.commandProcessor.eventMap.add(DetachedEventData.TYPE) {
            if (it.reason() == "targetCrashed") {
                println("Target crashed")
            } else {
                println("Target detached")
            }
        }

        val pipeline = channel.pipeline()

        val s = arrayOf<ChannelHandler>(object : WebSocketProtocolHandshakeHandler(handshaker) {
            override fun completed() {
                vm.initDomains().thenRun {
                    result.setResult(vm)
                    vm.ready()
                }
            }

            override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
                result.setError(cause!!)
                ctx?.fireExceptionCaught(cause)
            }
        }, WebSocketFrameAggregator(NettyUtil.MAX_CONTENT_LENGTH), object : WebSocketProtocolHandler() {
            override fun textFrameReceived(
                channel: Channel,
                message: TextWebSocketFrame
            ) {
                vm.textFrameReceived(message)
            }
        })

        pipeline.addLast(*s)


        val ff = handshaker.handshake(channel)

        ff.addListener {
            println(it)
        }

//        super.connectDebugger(page, context, result, debugMessageQueue)
    }

    override fun connectedAddressToPresentation(
        address: InetSocketAddress,
        vm: Vm
    ): String {
        return super.connectedAddressToPresentation(address, vm)
    }

    override fun connectToPage(
        context: ChannelHandlerContext,
        address: InetSocketAddress,
        connectionsJson: ByteBuf,
        result: AsyncPromise<WipVm>
    ): Boolean {

        val s = """[
    {
        "id": "intellij",
        "title": "run",
        "type": "node",
        "url": "file://",
        "webSocketDebuggerUrl": "ws://localhost:6499/intellij"
    }
]"""
        val jsonBytes = s.toByteArray(Charsets.UTF_8)
        val byteBuf: ByteBuf = Unpooled.wrappedBuffer(jsonBytes)
        return super.connectToPage(context, address, byteBuf, result)
    }

    override fun processPageConnections(
        context: ChannelHandlerContext,
        debugMessageQueue: MessagingLogger?,
        pageConnections: List<PageConnection>,
        result: AsyncPromise<WipVm>
    ): Boolean {
        return super.processPageConnections(context, debugMessageQueue, pageConnections, result)
    }

    public override fun createVm(
        page: PageConnection,
        channel: Channel,
        debugMessageQueue: MessagingLogger?
    ): StandaloneWipVm {
        val vm = super.createVm(page, channel, debugMessageQueue)
        val bunVm = BunWipVm(debugEventListener, page.url, channel, debugMessageQueue)
        return bunVm
    }
}