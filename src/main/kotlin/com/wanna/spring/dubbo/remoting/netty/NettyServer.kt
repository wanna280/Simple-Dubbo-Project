package com.wanna.spring.dubbo.remoting.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.common.utils.NetUtils
import com.wanna.spring.dubbo.remoting.AbstractServer
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.Constants
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

/**
 * NettyServer，负责根据Netty的ServerBootstrap、EventLoopGroup去启动Netty容器；
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class NettyServer(url: URL, handler: ChannelHandler) : AbstractServer(url, handler) {

    /**
     * ServerBootstrap
     */
    private var serverBootstrap: ServerBootstrap? = null

    /**
     * BossGroup
     */
    private var bossGroup: EventLoopGroup? = null

    /**
     * WorkerGroup
     */
    private var workerGroup: EventLoopGroup? = null

    /**
     * BossChannel
     */
    private var bossChannel: Channel? = null

    /**
     * Key为"ip:port", Value为DubboChannel，是通过NettyServerHandler接收到请求之后，
     * 将Channel去进行保存下来的结果，这里使用到的是引用传递的方式去进行获取到NettyServerHandler
     * 当中收集起来的Channel列表
     *
     * @see NettyServerHandler.channels
     */
    private var channels: Map<String, com.wanna.spring.dubbo.remoting.Channel>? = null

    /**
     * 打开Server
     */
    override fun doOpen() {
        this.bossGroup = NioEventLoopGroup(1)
        this.workerGroup = NioEventLoopGroup(Constants.DEFAULT_IO_THREADS)

        // NettyServerHandler
        val nettyServerHandler = NettyServerHandler(this@NettyServer, getUrl())

        // 将NettyServerHandler的Channels引用去进行保存起来
        // 当后续有连接进来时，就会自动回调ChannelHandler的相关方法去获取到它的Channel
        this.channels = nettyServerHandler.getChannels()

        // 构建出来ServerBootstrap
        val serverBootstrap = ServerBootstrap().group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    val pipeline = ch.pipeline()

                    // 创建一个用于构建Netty的编解码器的适配器
                    val adapter = NettyCodecAdapter(this@NettyServer, getUrl(), getCodec())

                    // add Encoder & Decoder
                    pipeline.addLast("dubbo-decoder", adapter.getDecoder())
                    pipeline.addLast("dubbo-encoder", adapter.getEncoder())

                    // add NettyServerHandler
                    pipeline.addLast("dubbo-server-handler", nettyServerHandler)
                }
            })

        this.serverBootstrap = serverBootstrap

        // bind address
        val channelFuture = serverBootstrap.bind(getBindAddress())
        this.bossChannel = channelFuture.syncUninterruptibly().channel()
    }

    /**
     * 关闭Server
     */
    override fun doClose() {
        this.bossGroup?.shutdownGracefully()
        this.workerGroup?.shutdownGracefully()
    }

    /**
     * 获取ChannelMap
     *
     * @return ChannelMap(Key是"ip:port", Value-DubboChannel)
     */
    open fun getChannelMap(): Map<String, com.wanna.spring.dubbo.remoting.Channel> =
        this.channels ?: throw IllegalStateException("channels不能为null")

    override fun getChannels(): Collection<com.wanna.spring.dubbo.remoting.Channel> {
        return getChannelMap().values
    }

    override fun getChannel(address: InetSocketAddress): com.wanna.spring.dubbo.remoting.Channel? {
        return getChannelMap()[NetUtils.toAddressString(address)]
    }
}