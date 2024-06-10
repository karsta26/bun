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
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.util.CharsetUtil
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.debugger.MessagingLogger
import org.jetbrains.debugger.Vm
import org.jetbrains.io.SimpleChannelInboundHandlerAdapter
import org.jetbrains.wip.StandaloneWipVm
import org.jetbrains.wip.WipVm
import java.net.InetSocketAddress

class BunVmConnection : WipRemoteVmConnection() {

//    override fun createBootstrap(): Bootstrap {
//        val createBootstrap = super.createBootstrap()
//        return createBootstrap
//    }
//
//    override fun createBootstrap(
//        address: InetSocketAddress,
//        vmResult: AsyncPromise<WipVm>
//    ): Bootstrap {
//        val createBootstrap = super.createBootstrap(address, vmResult)
//        createBootstrap.handler(object : ChannelInitializer<Channel>() {
//            override fun initChannel(ch: Channel?) {
//                val a =
//                    arrayOf(HttpClientCodec(), HttpObjectAggregator(10485760), createChannelHandler(address, vmResult))
//                ch?.pipeline()?.addLast(*a)
//            }
//        })
//        return createBootstrap
//    }

//    override fun createChannelHandler(
//        address: InetSocketAddress,
//        vmResult: AsyncPromise<WipVm>
//    ): ChannelHandler {
//        return Adap(address, vmResult)
//    }

    override fun connectDebugger(
        page: PageConnection,
        context: ChannelHandlerContext,
        result: AsyncPromise<WipVm>,
        debugMessageQueue: MessagingLogger?
    ) {
        super.connectDebugger(page, context, result, debugMessageQueue)
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
        "id": "int1",
        "title": "run",
        "type": "node",
        "url": "file://",
        "webSocketDebuggerUrl": "ws://localhost:6449/int1"
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
        return super.createVm(page, channel, debugMessageQueue)
    }
}

class Adap(val address: InetSocketAddress, val vmResult: AsyncPromise<WipVm>) :
    SimpleChannelInboundHandlerAdapter<String>() {
    override fun messageReceived(context: ChannelHandlerContext?, message: String?) {
        println(message)
    }

    override fun channelRegistered(ctx: ChannelHandlerContext?) {
        super.channelRegistered(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
        super.channelUnregistered(ctx)
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        val request: DefaultFullHttpRequest = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/json");
        request.headers().set(HttpHeaderNames.HOST, address.hostString + ":" + address.port);
        request.headers().set(HttpHeaderNames.ACCEPT, "*/*");
        val ses = ctx?.channel()?.writeAndFlush(
            BinaryWebSocketFrame(
                Unpooled.copiedBuffer(
                    ("{\n    \"id\": 1,\n    \"method\": \"Inspector.enable\"\n}"),
                    CharsetUtil.UTF_8
                )
            )
        )
        ses?.addListener(io.netty.channel.ChannelFutureListener {
            println("asd")
        })
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        super.channelReadComplete(ctx)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        ctx?.close()
        vmResult.setError(cause!!)
        super.exceptionCaught(ctx, cause)
    }
}