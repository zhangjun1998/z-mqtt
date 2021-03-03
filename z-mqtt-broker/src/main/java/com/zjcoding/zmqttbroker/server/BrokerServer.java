package com.zjcoding.zmqttbroker.server;

import com.zjcoding.zmqttbroker.config.BrokerProperties;
import com.zjcoding.zmqttbroker.handler.BrokerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * broker启动入口
 *
 * @author ZhangJun
 * @date 10:21 2021/2/24
 */

@Component
public class BrokerServer {

    @Resource
    private BrokerHandler brokerHandler;

    @Resource
    private BrokerProperties brokerProperties;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    @PostConstruct
    private void init() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            initMqttBroker();
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    @PreDestroy
    private void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        bossGroup = null;
        workerGroup = null;
    }

    /**
     * 启动broker监听
     *
     * @author ZhangJun
     * @date 10:57 2021/2/24
     */
    private void initMqttBroker() throws InterruptedException{
        ServerBootstrap bootstrap = new ServerBootstrap();

        // server启动引导设置
        bootstrap.group(bossGroup)
                .channel(NioServerSocketChannel.class)
                // todo 支持SSL加密、支持WebSocket连接
                // 初始化处理子连接的handler
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addFirst("heartbeat", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()))
                                .addLast("decoder", new MqttDecoder())
                                .addLast("encoder", MqttEncoder.INSTANCE)
                                .addLast("brokerHandler", brokerHandler);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());

        // 开始监听连接
        Channel channel = bootstrap.bind(brokerProperties.getMqttPort()).sync().channel();
        // 注册连接关闭监听器
        channel.closeFuture().addListener((ChannelFutureListener) future -> {
            channel.deregister();
            channel.close();
        });
    }

}
