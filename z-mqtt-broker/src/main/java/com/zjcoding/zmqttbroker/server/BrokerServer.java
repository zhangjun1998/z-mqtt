package com.zjcoding.zmqttbroker.server;

import com.zjcoding.zmqttbroker.codec.WebSocketMqttCodec;
import com.zjcoding.zmqttbroker.config.BrokerProperties;
import com.zjcoding.zmqttbroker.handler.BrokerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;

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
    private WebSocketMqttCodec socketMqttCodec;

    @Resource
    private BrokerProperties brokerProperties;

    private SslContext sslContext;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    @PostConstruct
    private void init() throws Exception {
        // 初始化线程组
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        // SSL加密
        if (brokerProperties.isUseSSL()) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = this.getClass().getResourceAsStream("keystore/z-mqtt-broker.pfx");
            keyStore.load(inputStream, brokerProperties.getSslPassword().toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, brokerProperties.getSslPassword().toCharArray());
            sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
        }

        try {
            // 启动broker监听
            initMqttBroker();
            initWebSocketServer();
        } catch (InterruptedException e) {
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
     * 监听MQTT连接
     *
     * @author ZhangJun
     * @date 10:57 2021/2/24
     */
    private void initMqttBroker() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();

        // server启动引导设置
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 初始化处理子连接的handler
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // SSL加密
                        if (sslContext != null) {
                            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
                            sslEngine.setUseClientMode(false);
                            sslEngine.setNeedClientAuth(false);
                            pipeline.addLast("ssl", new SslHandler(sslEngine));
                        }
                        pipeline.addFirst("heartbeat", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()))
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

    /**
     * 监听WebSocket连接
     *
     * @author ZhangJun
     * @date 15:09 2021/3/7
     */
    private void initWebSocketServer() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // SSL加密
                        if (sslContext != null) {
                            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
                            sslEngine.setUseClientMode(false);
                            sslEngine.setNeedClientAuth(false);
                            pipeline.addLast("ssl", new SslHandler(sslEngine));
                        }
                        // 心跳
                        pipeline.addFirst("heartbeat", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()))
                                // Http请求 => HttpRequest对象  HttpResponse => Http响应
                                .addLast("httpServerCodec", new HttpServerCodec())
                                // 聚合header与body组成完整的Http请求，最大数据量为1Mb
                                .addLast("aggregator", new HttpObjectAggregator(1024 * 1024))
                                // 压缩出站数据
                                .addLast("compressor", new HttpContentCompressor())
                                // 处理WebSocket协议
                                .addLast("protocol", new WebSocketServerProtocolHandler("/mqtt", "mqtt,mqttv3.1,mqttv3.1.1", true, 65536))
                                // 协议之间编码转换
                                .addLast("mqttWebsocket", socketMqttCodec)
                                // MQTT协议编码码
                                .addLast("decoder", new MqttDecoder())
                                .addLast("encoder", MqttEncoder.INSTANCE)
                                // 自定义处理器
                                .addLast("brokerHandler", brokerHandler);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());

        Channel channel = bootstrap.bind(brokerProperties.getWebSocketPort()).sync().channel();
        channel.closeFuture().addListener((ChannelFutureListener) future -> {
            channel.deregister();
            channel.close();
        });
    }

}
