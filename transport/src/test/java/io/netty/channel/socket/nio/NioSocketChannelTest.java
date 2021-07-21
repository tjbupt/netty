/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.socket.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
<<<<<<< HEAD
=======
import io.netty.util.internal.PlatformDependent;
>>>>>>> dev
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NetworkChannel;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
<<<<<<< HEAD
import java.util.concurrent.ThreadLocalRandom;
=======
>>>>>>> dev
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
<<<<<<< HEAD
=======
import static org.junit.jupiter.api.Assertions.assertNotSame;

>>>>>>> dev


public class NioSocketChannelTest extends AbstractNioChannelTest<NioSocketChannel> {

    /**
     * Reproduces the issue #1600
     */
    @Test
    public void testFlushCloseReentrance() throws Exception {
        EventLoopGroup group = new MultithreadEventLoopGroup(1, NioHandler.newFactory());
        try {
            final Queue<ChannelFuture> futures = new LinkedBlockingQueue<>();

            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group).channel(NioServerSocketChannel.class);
            sb.childOption(ChannelOption.SO_SNDBUF, 1024);
            sb.childHandler(new ChannelHandler() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    // Write a large enough data so that it is split into two loops.
                    futures.add(ctx.write(
                            ctx.alloc().buffer().writeZero(1048576)).addListener(ChannelFutureListener.CLOSE));
                    futures.add(ctx.write(ctx.alloc().buffer().writeZero(1048576)));
                    ctx.flush();
                    futures.add(ctx.write(ctx.alloc().buffer().writeZero(1048576)));
                    ctx.flush();
                }
            });

            SocketAddress address = sb.bind(0).sync().channel().localAddress();

            Socket s = new Socket(NetUtil.LOCALHOST, ((InetSocketAddress) address).getPort());

            InputStream in = s.getInputStream();
            byte[] buf = new byte[8192];
            for (;;) {
                if (in.read(buf) == -1) {
                    break;
                }

                // Wait a little bit so that the write attempts are split into multiple flush attempts.
                Thread.sleep(10);
            }
            s.close();

            assertThat(futures.size(), is(3));
            ChannelFuture f1 = futures.poll();
            ChannelFuture f2 = futures.poll();
            ChannelFuture f3 = futures.poll();
            assertThat(f1.isSuccess(), is(true));
            assertThat(f2.isDone(), is(true));
            assertThat(f2.isSuccess(), is(false));
            assertThat(f2.cause(), is(instanceOf(ClosedChannelException.class)));
            assertThat(f3.isDone(), is(true));
            assertThat(f3.isSuccess(), is(false));
            assertThat(f3.cause(), is(instanceOf(ClosedChannelException.class)));
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * Reproduces the issue #1679
     */
    @Test
    public void testFlushAfterGatheredFlush() throws Exception {
        EventLoopGroup group = new MultithreadEventLoopGroup(1, NioHandler.newFactory());
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group).channel(NioServerSocketChannel.class);
            sb.childHandler(new ChannelHandler() {
                @Override
                public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                    // Trigger a gathering write by writing two buffers.
                    ctx.write(Unpooled.wrappedBuffer(new byte[] { 'a' }));
                    ChannelFuture f = ctx.write(Unpooled.wrappedBuffer(new byte[] { 'b' }));
                    f.addListener((ChannelFutureListener) future -> {
                        // This message must be flushed
                        ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{'c'}));
                    });
                    ctx.flush();
                }
            });

            SocketAddress address = sb.bind(0).sync().channel().localAddress();

            Socket s = new Socket(NetUtil.LOCALHOST, ((InetSocketAddress) address).getPort());

            DataInput in = new DataInputStream(s.getInputStream());
            byte[] buf = new byte[3];
            in.readFully(buf);

            assertThat(new String(buf, CharsetUtil.US_ASCII), is("abc"));

            s.close();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    // Test for https://github.com/netty/netty/issues/4805
    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testChannelReRegisterReadSameEventLoop() throws Exception {
<<<<<<< HEAD
        final EventLoopGroup group = new MultithreadEventLoopGroup(2, NioHandler.newFactory());
=======
        testChannelReRegisterRead(true);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testChannelReRegisterReadDifferentEventLoop() throws Exception {
        testChannelReRegisterRead(false);
    }

    private static void testChannelReRegisterRead(final boolean sameEventLoop) throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup(2);
>>>>>>> dev
        final CountDownLatch latch = new CountDownLatch(1);

        // Just some random bytes
        byte[] bytes = new byte[1024];
        ThreadLocalRandom.current().nextBytes(bytes);

        Channel sc = null;
        Channel cc = null;
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(group)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childHandler(new ChannelInitializer<Channel>() {
                 @Override
                 protected void initChannel(final Channel ch) throws Exception {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                         @Override
                         protected void messageReceived(ChannelHandlerContext ctx, ByteBuf byteBuf) {
                             // We was able to read something from the Channel after reregister.
                             latch.countDown();
                         }

                         @Override
                         public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                             final EventLoop loop = group.next();
                             deregister(ctx, loop);
                         }

                         private void deregister(ChannelHandlerContext ctx, final EventLoop loop) {
                             // As soon as the channel becomes active re-register it to another
                             // EventLoop. After this is done we should still receive the data that
                             // was written to the channel.
                             ctx.deregister().addListener((ChannelFutureListener) cf -> {
                                 Channel channel = cf.channel();
                                 channel.register();
                             });
                         }
                     });
                 }
             });

            sc = b.bind(0).syncUninterruptibly().channel();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelHandler() { });
            cc = bootstrap.connect(sc.localAddress()).syncUninterruptibly().channel();
            cc.writeAndFlush(Unpooled.wrappedBuffer(bytes)).syncUninterruptibly();
            latch.await();
        } finally {
            if (cc != null) {
                cc.close();
            }
            if (sc != null) {
                sc.close();
            }
            group.shutdownGracefully();
        }
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testShutdownOutputAndClose() throws IOException {
        EventLoopGroup group = new MultithreadEventLoopGroup(1, NioHandler.newFactory());
        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(0));
        Socket accepted = null;
        try {
            Bootstrap sb = new Bootstrap();
            sb.group(group).channel(NioSocketChannel.class);
            sb.handler(new ChannelHandler() { });

            SocketChannel channel = (SocketChannel) sb.connect(socket.getLocalSocketAddress())
                    .syncUninterruptibly().channel();

            accepted = socket.accept();
            channel.shutdownOutput().syncUninterruptibly();

            channel.close().syncUninterruptibly();
        } finally {
            if (accepted != null) {
                try {
                    accepted.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
            try {
                socket.close();
            } catch (IOException ignore) {
                // ignore
            }
            group.shutdownGracefully();
        }
    }

    @Override
    protected NioSocketChannel newNioChannel(EventLoopGroup group) {
        return new NioSocketChannel(group.next());
    }

    @Override
    protected NetworkChannel jdkChannel(NioSocketChannel channel) {
        return channel.javaChannel();
    }

    @Override
    protected SocketOption<?> newInvalidOption() {
        return StandardSocketOptions.IP_MULTICAST_IF;
    }
}
