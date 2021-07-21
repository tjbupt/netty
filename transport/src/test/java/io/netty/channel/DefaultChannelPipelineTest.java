/*
 * Copyright 2012 The Netty Project
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
package io.netty.channel;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerMask.Skip;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalHandler;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
<<<<<<< HEAD
import io.netty.util.concurrent.ScheduledFuture;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
=======
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
>>>>>>> dev

import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

<<<<<<< HEAD
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
=======
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
>>>>>>> dev
import static org.junit.jupiter.api.Assertions.fail;

public class DefaultChannelPipelineTest {

<<<<<<< HEAD
    private static final EventLoopGroup group = new MultithreadEventLoopGroup(1, LocalHandler.newFactory());
=======
    private static EventLoopGroup group;
>>>>>>> dev

    private Channel self;
    private Channel peer;

<<<<<<< HEAD
=======
    @BeforeAll
    public static void beforeClass() throws Exception {
        group = new DefaultEventLoopGroup(1);
    }

>>>>>>> dev
    @AfterAll
    public static void afterClass() throws Exception {
        group.shutdownGracefully().sync();
    }

    private void setUp(final ChannelHandler... handlers) throws Exception {
        final AtomicReference<Channel> peerRef = new AtomicReference<>();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(group).channel(LocalServerChannel.class);
        sb.childHandler(new ChannelHandler() {
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                peerRef.set(ctx.channel());
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ReferenceCountUtil.release(msg);
            }
        });

        ChannelFuture bindFuture = sb.bind(LocalAddress.ANY).sync();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(LocalChannel.class);
        b.handler(new ChannelInitializer<LocalChannel>() {
            @Override
            protected void initChannel(LocalChannel ch) {
                ch.pipeline().addLast(handlers);
            }
        });

        self = b.connect(bindFuture.channel().localAddress()).sync().channel();
        peer = peerRef.get();

        bindFuture.channel().close().sync();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (peer != null) {
            peer.close();
            peer = null;
        }
        if (self != null) {
            self = null;
        }
    }

    @Test
    public void testFreeCalled() throws Exception {
        final CountDownLatch free = new CountDownLatch(1);

        final ReferenceCounted holder = new AbstractReferenceCounted() {
            @Override
            protected void deallocate() {
                free.countDown();
            }

            @Override
            public ReferenceCounted touch(Object hint) {
                return this;
            }
        };

        StringInboundHandler handler = new StringInboundHandler();
        setUp(handler);

        peer.writeAndFlush(holder).sync();

        assertTrue(free.await(10, TimeUnit.SECONDS));
        assertTrue(handler.called);
    }

    private static final class StringInboundHandler implements ChannelHandler {
        boolean called;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            called = true;
            if (!(msg instanceof String)) {
                ctx.fireChannelRead(msg);
            }
        }
    }

    private static LocalChannel newLocalChannel() {
        return new LocalChannel(group.next());
    }

    @Test
    public void testAddLastVarArgsSkipsNull() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        pipeline.addLast(null, newHandler(), null);
        assertEquals(1, pipeline.names().size());
        assertEquals("DefaultChannelPipelineTest$TestHandler#0", pipeline.names().get(0));

        pipeline.addLast(newHandler(), null, newHandler());
        assertEquals(3, pipeline.names().size());
        assertEquals("DefaultChannelPipelineTest$TestHandler#0", pipeline.names().get(0));
        assertEquals("DefaultChannelPipelineTest$TestHandler#1", pipeline.names().get(1));
        assertEquals("DefaultChannelPipelineTest$TestHandler#2", pipeline.names().get(2));

        pipeline.addLast((ChannelHandler) null);
        assertEquals(3, pipeline.names().size());
    }

    @Test
    public void testAddFirstVarArgsSkipsNull() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        pipeline.addFirst(null, newHandler(), null);
        assertEquals(1, pipeline.names().size());
        assertEquals("DefaultChannelPipelineTest$TestHandler#0", pipeline.names().get(0));

        pipeline.addFirst(newHandler(), null, newHandler());
        assertEquals(3, pipeline.names().size());
        assertEquals("DefaultChannelPipelineTest$TestHandler#2", pipeline.names().get(0));
        assertEquals("DefaultChannelPipelineTest$TestHandler#1", pipeline.names().get(1));
        assertEquals("DefaultChannelPipelineTest$TestHandler#0", pipeline.names().get(2));

        pipeline.addFirst((ChannelHandler) null);
        assertEquals(3, pipeline.names().size());
    }

    @Test
    public void testRemoveChannelHandler() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        ChannelHandler handler1 = newHandler();
        ChannelHandler handler2 = newHandler();
        ChannelHandler handler3 = newHandler();

        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler2);
        pipeline.addLast("handler3", handler3);
        assertSame(pipeline.get("handler1"), handler1);
        assertSame(pipeline.get("handler2"), handler2);
        assertSame(pipeline.get("handler3"), handler3);

        pipeline.remove(handler1);
        assertNull(pipeline.get("handler1"));
        pipeline.remove(handler2);
        assertNull(pipeline.get("handler2"));
        pipeline.remove(handler3);
        assertNull(pipeline.get("handler3"));
    }

    @Test
    public void testRemoveIfExists() {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline(newLocalChannel());

        ChannelHandler handler1 = newHandler();
        ChannelHandler handler2 = newHandler();
        ChannelHandler handler3 = newHandler();

        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler2);
        pipeline.addLast("handler3", handler3);

        assertNotNull(pipeline.removeIfExists(handler1));
        assertNull(pipeline.get("handler1"));

        assertNotNull(pipeline.removeIfExists("handler2"));
        assertNull(pipeline.get("handler2"));

        assertNotNull(pipeline.removeIfExists(TestHandler.class));
        assertNull(pipeline.get("handler3"));
    }

    @Test
    public void testRemoveIfExistsDoesNotThrowException() {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline(newLocalChannel());

        ChannelHandler handler1 = newHandler();
        ChannelHandler handler2 = newHandler();
        pipeline.addLast("handler1", handler1);

        assertNull(pipeline.removeIfExists("handlerXXX"));
        assertNull(pipeline.removeIfExists(handler2));

        class NonExistingHandler implements ChannelHandler { }

        assertNull(pipeline.removeIfExists(NonExistingHandler.class));
        assertNotNull(pipeline.get("handler1"));
    }

    @Test
    public void testRemoveThrowNoSuchElementException() {
<<<<<<< HEAD
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline(newLocalChannel());
=======
        final DefaultChannelPipeline pipeline = new DefaultChannelPipeline(new LocalChannel());
>>>>>>> dev

        ChannelHandler handler1 = newHandler();
        pipeline.addLast("handler1", handler1);

<<<<<<< HEAD
        assertThrows(NoSuchElementException.class, () -> pipeline.remove("handlerXXX"));
=======
        assertThrows(NoSuchElementException.class, new Executable() {
            @Override
            public void execute() {
                pipeline.remove("handlerXXX");
            }
        });
>>>>>>> dev
    }

    @Test
    public void testReplaceChannelHandler() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        ChannelHandler handler1 = newHandler();
        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler1);
        pipeline.addLast("handler3", handler1);
        assertSame(pipeline.get("handler1"), handler1);
        assertSame(pipeline.get("handler2"), handler1);
        assertSame(pipeline.get("handler3"), handler1);

        ChannelHandler newHandler1 = newHandler();
        pipeline.replace("handler1", "handler1", newHandler1);
        assertSame(pipeline.get("handler1"), newHandler1);

        ChannelHandler newHandler3 = newHandler();
        pipeline.replace("handler3", "handler3", newHandler3);
        assertSame(pipeline.get("handler3"), newHandler3);

        ChannelHandler newHandler2 = newHandler();
        pipeline.replace("handler2", "handler2", newHandler2);
        assertSame(pipeline.get("handler2"), newHandler2);
    }

    @Test
    public void testReplaceHandlerChecksDuplicateNames() {
<<<<<<< HEAD
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
        final ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev

        ChannelHandler handler1 = newHandler();
        ChannelHandler handler2 = newHandler();
        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler2);

<<<<<<< HEAD
        ChannelHandler newHandler1 = newHandler();
        assertThrows(IllegalArgumentException.class, () -> pipeline.replace("handler1", "handler2", newHandler1));
=======
        final ChannelHandler newHandler1 = newHandler();
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                pipeline.replace("handler1", "handler2", newHandler1);
            }
        });
>>>>>>> dev
    }

    @Test
    public void testReplaceNameWithGenerated() {
<<<<<<< HEAD
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev

        ChannelHandler handler1 = newHandler();
        pipeline.addLast("handler1", handler1);
        assertSame(pipeline.get("handler1"), handler1);

        ChannelHandler newHandler1 = newHandler();
        pipeline.replace("handler1", null, newHandler1);
        assertSame(pipeline.get("DefaultChannelPipelineTest$TestHandler#0"), newHandler1);
        assertNull(pipeline.get("handler1"));
    }

    @Test
    public void testRenameChannelHandler() {
<<<<<<< HEAD
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev

        ChannelHandler handler1 = newHandler();
        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler1);
        pipeline.addLast("handler3", handler1);
        assertSame(pipeline.get("handler1"), handler1);
        assertSame(pipeline.get("handler2"), handler1);
        assertSame(pipeline.get("handler3"), handler1);

        ChannelHandler newHandler1 = newHandler();
        pipeline.replace("handler1", "newHandler1", newHandler1);
        assertSame(pipeline.get("newHandler1"), newHandler1);
        assertNull(pipeline.get("handler1"));

        ChannelHandler newHandler3 = newHandler();
        pipeline.replace("handler3", "newHandler3", newHandler3);
        assertSame(pipeline.get("newHandler3"), newHandler3);
        assertNull(pipeline.get("handler3"));

        ChannelHandler newHandler2 = newHandler();
        pipeline.replace("handler2", "newHandler2", newHandler2);
        assertSame(pipeline.get("newHandler2"), newHandler2);
        assertNull(pipeline.get("handler2"));
    }

    @Test
    public void testChannelHandlerContextNavigation() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        final int HANDLER_ARRAY_LEN = 5;
        ChannelHandler[] firstHandlers = newHandlers(HANDLER_ARRAY_LEN);
        ChannelHandler[] lastHandlers = newHandlers(HANDLER_ARRAY_LEN);

        pipeline.addFirst(firstHandlers);
        pipeline.addLast(lastHandlers);

        verifyContextNumber(pipeline, HANDLER_ARRAY_LEN * 2);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testThrowInExceptionCaught() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger counter = new AtomicInteger();
<<<<<<< HEAD
        Channel channel = newLocalChannel();
        try {
            channel.register().syncUninterruptibly();
            channel.pipeline().addLast(new ChannelHandler() {
=======
        Channel channel = new LocalChannel();
        try {
            group.register(channel).syncUninterruptibly();
            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
>>>>>>> dev
                class TestException extends Exception { }

                @Override
                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                    throw new TestException();
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    if (cause instanceof TestException) {
                        ctx.executor().execute(new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                            }
                        });
                    }
                    counter.incrementAndGet();
                    throw new Exception();
                }
            });

            channel.pipeline().fireChannelReadComplete();
            latch.await();
            assertEquals(1, counter.get());
        } finally {
            channel.close().syncUninterruptibly();
        }
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testThrowInOtherHandlerAfterInvokedFromExceptionCaught() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger counter = new AtomicInteger();
<<<<<<< HEAD
        Channel channel = newLocalChannel();
        try {
            channel.register().syncUninterruptibly();
            channel.pipeline().addLast(new ChannelHandler() {
=======
        Channel channel = new LocalChannel();
        try {
            group.register(channel).syncUninterruptibly();
            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
>>>>>>> dev
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                    ctx.fireChannelReadComplete();
                }
<<<<<<< HEAD
            }, new ChannelHandler() {
=======
            }, new ChannelInboundHandlerAdapter() {
>>>>>>> dev
                class TestException extends Exception { }

                @Override
                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                    throw new TestException();
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    if (cause instanceof TestException) {
                        ctx.executor().execute(new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                            }
                        });
                    }
                    counter.incrementAndGet();
                    throw new Exception();
                }
            });

            channel.pipeline().fireExceptionCaught(new Exception());
            latch.await();
            assertEquals(1, counter.get());
        } finally {
            channel.close().syncUninterruptibly();
        }
    }

    @Test
    public void testFireChannelRegistered() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.addLast(new ChannelInitializer<Channel>() {
            @Override
<<<<<<< HEAD
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ChannelHandler() {
=======
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
>>>>>>> dev
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) {
                        latch.countDown();
                    }
                });
            }
        });
        pipeline.channel().register();
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testPipelineOperation() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        final int handlerNum = 5;
        ChannelHandler[] handlers1 = newHandlers(handlerNum);
        ChannelHandler[] handlers2 = newHandlers(handlerNum);

        final String prefixX = "x";
        for (int i = 0; i < handlerNum; i++) {
            if (i % 2 == 0) {
                pipeline.addFirst(prefixX + i, handlers1[i]);
            } else {
                pipeline.addLast(prefixX + i, handlers1[i]);
            }
        }

        for (int i = 0; i < handlerNum; i++) {
            if (i % 2 != 0) {
                pipeline.addBefore(prefixX + i, String.valueOf(i), handlers2[i]);
            } else {
                pipeline.addAfter(prefixX + i, String.valueOf(i), handlers2[i]);
            }
        }

        verifyContextNumber(pipeline, handlerNum * 2);
    }

    @Test
    public void testChannelHandlerContextOrder() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        pipeline.addFirst("1", newHandler());
        pipeline.addLast("10", newHandler());

        pipeline.addBefore("10", "5", newHandler());
        pipeline.addAfter("1", "3", newHandler());
        pipeline.addBefore("5", "4", newHandler());
        pipeline.addAfter("5", "6", newHandler());

        pipeline.addBefore("1", "0", newHandler());
        pipeline.addAfter("10", "11", newHandler());

        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) pipeline.firstContext();
        assertNotNull(ctx);
        while (ctx != null) {
            int i = toInt(ctx.name());
            int j = next(ctx);
            if (j != -1) {
                assertTrue(i < j);
            } else {
                assertNull(ctx.next.next);
            }
            ctx = ctx.next;
        }

        verifyContextNumber(pipeline, 8);
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void testLifeCycleAwareness() throws Exception {
        setUp();

        ChannelPipeline p = self.pipeline();

        final List<LifeCycleAwareTestHandler> handlers = new ArrayList<>();
        final int COUNT = 20;
        final CountDownLatch addLatch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++) {
            final LifeCycleAwareTestHandler handler = new LifeCycleAwareTestHandler("handler-" + i);

            // Add handler.
            p.addFirst(handler.name, handler);
            self.eventLoop().execute(() -> {
                // Validate handler life-cycle methods called.
                handler.validate(true, false);

                // Store handler into the list.
                handlers.add(handler);

                addLatch.countDown();
            });
        }
        addLatch.await();

        // Change the order of remove operations over all handlers in the pipeline.
        Collections.shuffle(handlers);

        final CountDownLatch removeLatch = new CountDownLatch(COUNT);

        for (final LifeCycleAwareTestHandler handler : handlers) {
            assertSame(handler, p.remove(handler.name));

            self.eventLoop().execute(() -> {
                // Validate handler life-cycle methods called.
                handler.validate(true, true);
                removeLatch.countDown();
            });
        }
        removeLatch.await();
    }

    @Test
<<<<<<< HEAD
    @Timeout(value = 100000, unit = TimeUnit.MILLISECONDS)
=======
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
>>>>>>> dev
    public void testRemoveAndForwardInbound() throws Exception {
        final BufferedTestHandler handler1 = new BufferedTestHandler();
        final BufferedTestHandler handler2 = new BufferedTestHandler();

        setUp(handler1, handler2);

        self.eventLoop().submit(() -> {
            ChannelPipeline p = self.pipeline();
            handler1.inboundBuffer.add(8);
            assertEquals(8, handler1.inboundBuffer.peek());
            assertTrue(handler2.inboundBuffer.isEmpty());
            p.remove(handler1);
            assertEquals(1, handler2.inboundBuffer.size());
            assertEquals(8, handler2.inboundBuffer.peek());
        }).sync();
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void testRemoveAndForwardOutbound() throws Exception {
        final BufferedTestHandler handler1 = new BufferedTestHandler();
        final BufferedTestHandler handler2 = new BufferedTestHandler();

        setUp(handler1, handler2);

        self.eventLoop().submit(() -> {
            ChannelPipeline p = self.pipeline();
            handler2.outboundBuffer.add(8);
            assertEquals(8, handler2.outboundBuffer.peek());
            assertTrue(handler1.outboundBuffer.isEmpty());
            p.remove(handler2);
            assertEquals(1, handler1.outboundBuffer.size());
            assertEquals(8, handler1.outboundBuffer.peek());
        }).sync();
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void testReplaceAndForwardOutbound() throws Exception {
        final BufferedTestHandler handler1 = new BufferedTestHandler();
        final BufferedTestHandler handler2 = new BufferedTestHandler();

        setUp(handler1);

        self.eventLoop().submit(() -> {
            ChannelPipeline p = self.pipeline();
            handler1.outboundBuffer.add(8);
            assertEquals(8, handler1.outboundBuffer.peek());
            assertTrue(handler2.outboundBuffer.isEmpty());
            p.replace(handler1, "handler2", handler2);
            assertEquals(8, handler2.outboundBuffer.peek());
        }).sync();
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void testReplaceAndForwardInboundAndOutbound() throws Exception {
        final BufferedTestHandler handler1 = new BufferedTestHandler();
        final BufferedTestHandler handler2 = new BufferedTestHandler();

        setUp(handler1);

        self.eventLoop().submit(() -> {
            ChannelPipeline p = self.pipeline();
            handler1.inboundBuffer.add(8);
            handler1.outboundBuffer.add(8);

            assertEquals(8, handler1.inboundBuffer.peek());
            assertEquals(8, handler1.outboundBuffer.peek());
            assertTrue(handler2.inboundBuffer.isEmpty());
            assertTrue(handler2.outboundBuffer.isEmpty());

            p.replace(handler1, "handler2", handler2);
            assertEquals(8, handler2.outboundBuffer.peek());
            assertEquals(8, handler2.inboundBuffer.peek());
        }).sync();
    }

    @Test
    @Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
    public void testRemoveAndForwardInboundOutbound() throws Exception {
        final BufferedTestHandler handler1 = new BufferedTestHandler();
        final BufferedTestHandler handler2 = new BufferedTestHandler();
        final BufferedTestHandler handler3 = new BufferedTestHandler();

        setUp(handler1, handler2, handler3);

        self.eventLoop().submit(() -> {
            ChannelPipeline p = self.pipeline();
            handler2.inboundBuffer.add(8);
            handler2.outboundBuffer.add(8);

            assertEquals(8, handler2.inboundBuffer.peek());
            assertEquals(8, handler2.outboundBuffer.peek());

            assertEquals(0, handler1.outboundBuffer.size());
            assertEquals(0, handler3.inboundBuffer.size());

            p.remove(handler2);
            assertEquals(8, handler3.inboundBuffer.peek());
            assertEquals(8, handler1.outboundBuffer.peek());
        }).sync();
    }

    // Tests for https://github.com/netty/netty/issues/2349
    @Test
<<<<<<< HEAD
    public void testCancelBind() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register();
=======
    public void testCancelBind() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ChannelFuture future = pipeline.bind(new LocalAddress("test"), promise);
        assertTrue(future.isCancelled());
    }

    @Test
<<<<<<< HEAD
    public void testCancelConnect() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register();
=======
    public void testCancelConnect() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ChannelFuture future = pipeline.connect(new LocalAddress("test"), promise);
        assertTrue(future.isCancelled());
    }

    @Test
<<<<<<< HEAD
    public void testCancelDisconnect() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register();
=======
    public void testCancelDisconnect() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ChannelFuture future = pipeline.disconnect(promise);
        assertTrue(future.isCancelled());
    }

    @Test
<<<<<<< HEAD
    public void testCancelClose() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register();
=======
    public void testCancelClose() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ChannelFuture future = pipeline.close(promise);
        assertTrue(future.isCancelled());
    }

    @Test
    public void testWrongPromiseChannel() throws Exception {
<<<<<<< HEAD
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register().sync();
=======
        final ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel()).sync();
>>>>>>> dev

        ChannelPipeline pipeline2 = newLocalChannel().pipeline();
        pipeline2.channel().register().sync();

        try {
<<<<<<< HEAD
            ChannelPromise promise2 = pipeline2.channel().newPromise();
            assertThrows(IllegalArgumentException.class, () -> pipeline.close(promise2));
=======
            final ChannelPromise promise2 = pipeline2.channel().newPromise();
            assertThrows(IllegalArgumentException.class, new Executable() {
                @Override
                public void execute() {
                    pipeline.close(promise2);
                }
            });
>>>>>>> dev
        } finally {
            pipeline.close();
            pipeline2.close();
        }
    }

    @Test
<<<<<<< HEAD
    public void testUnexpectedVoidChannelPromiseCloseFuture() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register().sync();

        try {
            ChannelPromise promise = (ChannelPromise) pipeline.channel().closeFuture();
            assertThrows(IllegalArgumentException.class, () -> pipeline.close(promise));
=======
    public void testUnexpectedVoidChannelPromise() throws Exception {
        final ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel()).sync();

        try {
            final ChannelPromise promise = new VoidChannelPromise(pipeline.channel(), false);
            assertThrows(IllegalArgumentException.class, new Executable() {
                @Override
                public void execute() {
                    pipeline.close(promise);
                }
            });
        } finally {
            pipeline.close();
        }
    }

    @Test
    public void testUnexpectedVoidChannelPromiseCloseFuture() throws Exception {
        final ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel()).sync();

        try {
            final ChannelPromise promise = (ChannelPromise) pipeline.channel().closeFuture();
            assertThrows(IllegalArgumentException.class, new Executable() {
                @Override
                public void execute() {
                    pipeline.close(promise);
                }
            });
>>>>>>> dev
        } finally {
            pipeline.close();
        }
    }

    @Test
<<<<<<< HEAD
    public void testCancelDeregister() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register().sync();
=======
    public void testCancelDeregister() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ChannelFuture future = pipeline.deregister(promise);
        assertTrue(future.isCancelled());
    }

    @Test
<<<<<<< HEAD
    public void testCancelWrite() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register().sync();
=======
    public void testCancelWrite() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ByteBuf buffer = Unpooled.buffer();
        assertEquals(1, buffer.refCnt());
        ChannelFuture future = pipeline.write(buffer, promise);
        assertTrue(future.isCancelled());
        assertEquals(0, buffer.refCnt());
    }

    @Test
<<<<<<< HEAD
    public void testCancelWriteAndFlush() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.channel().register().sync();
=======
    public void testCancelWriteAndFlush() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        group.register(pipeline.channel());
>>>>>>> dev

        ChannelPromise promise = pipeline.channel().newPromise();
        assertTrue(promise.cancel(false));
        ByteBuf buffer = Unpooled.buffer();
        assertEquals(1, buffer.refCnt());
        ChannelFuture future = pipeline.writeAndFlush(buffer, promise);
        assertTrue(future.isCancelled());
        assertEquals(0, buffer.refCnt());
    }

    @Test
<<<<<<< HEAD
    public void testFirstContextEmptyPipeline() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testFirstContextEmptyPipeline() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev
        assertNull(pipeline.firstContext());
    }

    @Test
<<<<<<< HEAD
    public void testLastContextEmptyPipeline() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testLastContextEmptyPipeline() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev
        assertNull(pipeline.lastContext());
    }

    @Test
<<<<<<< HEAD
    public void testFirstHandlerEmptyPipeline() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testFirstHandlerEmptyPipeline() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev
        assertNull(pipeline.first());
    }

    @Test
<<<<<<< HEAD
    public void testLastHandlerEmptyPipeline() throws Exception {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testLastHandlerEmptyPipeline() {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev
        assertNull(pipeline.last());
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testChannelInitializerException() throws Exception {
        final IllegalStateException exception = new IllegalStateException();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        EmbeddedChannel channel = new EmbeddedChannel(false, false, new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                throw exception;
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                super.exceptionCaught(ctx, cause);
                error.set(cause);
                latch.countDown();
            }
        });
        latch.await();
        assertFalse(channel.isActive());
        assertSame(exception, error.get());
    }

    @Test
<<<<<<< HEAD
=======
    public void testChannelUnregistrationWithCustomExecutor() throws Exception {
        final CountDownLatch channelLatch = new CountDownLatch(1);
        final CountDownLatch handlerLatch = new CountDownLatch(1);
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        pipeline.addLast(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new WrapperExecutor(),
                        new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) {
                                channelLatch.countDown();
                            }

                            @Override
                            public void handlerRemoved(ChannelHandlerContext ctx) {
                                handlerLatch.countDown();
                            }
                        });
            }
        });
        Channel channel = pipeline.channel();
        group.register(channel);
        channel.close();
        channel.deregister();
        assertTrue(channelLatch.await(2, TimeUnit.SECONDS));
        assertTrue(handlerLatch.await(2, TimeUnit.SECONDS));
    }

    @Test
>>>>>>> dev
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testAddHandlerBeforeRegisteredThenRemove() {
        final EventLoop loop = group.next();

        CheckEventExecutorHandler handler = new CheckEventExecutorHandler(loop);
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.addFirst(handler);
        handler.addedPromise.syncUninterruptibly();
        pipeline.channel().register();
        pipeline.remove(handler);
        handler.removedPromise.syncUninterruptibly();

        pipeline.channel().close().syncUninterruptibly();
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testAddHandlerBeforeRegisteredThenReplace() throws Exception {
        final EventLoop loop = group.next();
        final CountDownLatch latch = new CountDownLatch(1);

        CheckEventExecutorHandler handler = new CheckEventExecutorHandler(loop);
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.addFirst(handler);
        handler.addedPromise.syncUninterruptibly();
        pipeline.channel().register();
        pipeline.replace(handler, null, new ChannelHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                latch.countDown();
            }
        });
        handler.removedPromise.syncUninterruptibly();
        latch.await();

        pipeline.channel().close().syncUninterruptibly();
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testAddRemoveHandlerCalled() throws Throwable {
        ChannelPipeline pipeline = newLocalChannel().pipeline();

        CallbackCheckHandler handler = new CallbackCheckHandler();
        pipeline.addFirst(handler);
        pipeline.remove(handler);
        assertTrue(handler.addedHandler.get());
        assertTrue(handler.removedHandler.get());

        CallbackCheckHandler handlerType = new CallbackCheckHandler();
        pipeline.addFirst(handlerType);
        pipeline.remove(handlerType.getClass());
        assertTrue(handlerType.addedHandler.get());
        assertTrue(handlerType.removedHandler.get());

        CallbackCheckHandler handlerName = new CallbackCheckHandler();
        pipeline.addFirst("handler", handlerName);
        pipeline.remove("handler");
        assertTrue(handlerName.addedHandler.get());
        assertTrue(handlerName.removedHandler.get());

        CallbackCheckHandler first = new CallbackCheckHandler();
        pipeline.addFirst(first);
        pipeline.removeFirst();
        assertTrue(first.addedHandler.get());
        assertTrue(first.removedHandler.get());

        CallbackCheckHandler last = new CallbackCheckHandler();
        pipeline.addFirst(last);
        pipeline.removeLast();
        assertTrue(last.addedHandler.get());
        assertTrue(last.removedHandler.get());

        pipeline.channel().register().syncUninterruptibly();
        Throwable cause = handler.error.get();
        Throwable causeName = handlerName.error.get();
        Throwable causeType = handlerType.error.get();
        Throwable causeFirst = first.error.get();
        Throwable causeLast = last.error.get();
        pipeline.channel().close().syncUninterruptibly();
        rethrowIfNotNull(cause);
        rethrowIfNotNull(causeName);
        rethrowIfNotNull(causeType);
        rethrowIfNotNull(causeFirst);
        rethrowIfNotNull(causeLast);
    }

    private static void rethrowIfNotNull(Throwable cause) throws Throwable {
        if (cause != null) {
            throw cause;
        }
    }

    @Test
<<<<<<< HEAD
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testOperationsFailWhenRemoved() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testAddReplaceHandlerNotRegistered() throws Throwable {
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        ChannelHandler handler = new ErrorChannelHandler(error);
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        pipeline.addFirst(handler);
        pipeline.replace(handler, null, new ErrorChannelHandler(error));

        Throwable cause = error.get();
        if (cause != null) {
            throw cause;
        }
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerAddedAndRemovedCalledInCorrectOrder() throws Throwable {
        final EventExecutorGroup group1 = new DefaultEventExecutorGroup(1);
        final EventExecutorGroup group2 = new DefaultEventExecutorGroup(1);

>>>>>>> dev
        try {
            pipeline.channel().register().syncUninterruptibly();

            ChannelHandler handler = new ChannelHandler() { };
            pipeline.addFirst(handler);
            ChannelHandlerContext ctx = pipeline.context(handler);
            pipeline.remove(handler);

            testOperationsFailsOnContext(ctx);
        } finally {
            pipeline.channel().close().syncUninterruptibly();
        }
    }

    @Test
<<<<<<< HEAD
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testOperationsFailWhenReplaced() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerAddedExceptionFromChildHandlerIsPropagated() {
        final EventExecutorGroup group1 = new DefaultEventExecutorGroup(1);
>>>>>>> dev
        try {
            pipeline.channel().register().syncUninterruptibly();

<<<<<<< HEAD
            ChannelHandler handler = new ChannelHandler() { };
            pipeline.addFirst(handler);
            ChannelHandlerContext ctx = pipeline.context(handler);
            pipeline.replace(handler, null, new ChannelHandler() { });

            testOperationsFailsOnContext(ctx);
=======
    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerRemovedExceptionFromChildHandlerIsPropagated() {
        final EventExecutorGroup group1 = new DefaultEventExecutorGroup(1);
        try {
            final Promise<Void> promise = group1.next().newPromise();
            String handlerName = "foo";
            final Exception exception = new RuntimeException();
            ChannelPipeline pipeline = new LocalChannel().pipeline();
            pipeline.addLast(handlerName, new ChannelHandlerAdapter() {
                @Override
                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                    throw exception;
                }
            });
            pipeline.addLast(group1, new CheckExceptionHandler(exception, promise));
            group.register(pipeline.channel()).syncUninterruptibly();
            pipeline.remove(handlerName);
            promise.syncUninterruptibly();
>>>>>>> dev
        } finally {
            pipeline.channel().close().syncUninterruptibly();
        }
    }

<<<<<<< HEAD
    private static void testOperationsFailsOnContext(ChannelHandlerContext ctx) {
        assertChannelPipelineException(ctx.writeAndFlush(""));
        assertChannelPipelineException(ctx.write(""));
        assertChannelPipelineException(ctx.bind(new SocketAddress() { }));
        assertChannelPipelineException(ctx.close());
        assertChannelPipelineException(ctx.connect(new SocketAddress() { }));
        assertChannelPipelineException(ctx.connect(new SocketAddress() { }, new SocketAddress() { }));
        assertChannelPipelineException(ctx.deregister());
        assertChannelPipelineException(ctx.disconnect());
=======
    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerAddedThrowsAndRemovedThrowsException() throws InterruptedException {
        final EventExecutorGroup group1 = new DefaultEventExecutorGroup(1);
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final Promise<Void> promise = group1.next().newPromise();
            final Exception exceptionAdded = new RuntimeException();
            final Exception exceptionRemoved = new RuntimeException();
            String handlerName = "foo";
            ChannelPipeline pipeline = new LocalChannel().pipeline();
            pipeline.addLast(group1, new CheckExceptionHandler(exceptionAdded, promise));
            pipeline.addFirst(handlerName, new ChannelHandlerAdapter() {
                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                    throw exceptionAdded;
                }
>>>>>>> dev

        class ChannelPipelineExceptionValidator implements ChannelHandler {

            private Promise<Void> validationPromise = ImmediateEventExecutor.INSTANCE.newPromise();

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                try {
                    assertThat(cause, Matchers.instanceOf(ChannelPipelineException.class));
                } catch (Throwable error) {
                    validationPromise.setFailure(error);
                    return;
                }
                validationPromise.setSuccess(null);
            }

            void validate() {
                validationPromise.syncUninterruptibly();
                validationPromise = ImmediateEventExecutor.INSTANCE.newPromise();
            }
        }

<<<<<<< HEAD
        ChannelPipelineExceptionValidator validator = new ChannelPipelineExceptionValidator();
        ctx.pipeline().addLast(validator);
=======
    @Test
    @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
    public void testAddRemoveHandlerCalledOnceRegistered() throws Throwable {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        CallbackCheckHandler handler = new CallbackCheckHandler();
>>>>>>> dev

        ctx.fireChannelRead("");
        validator.validate();

        ctx.fireUserEventTriggered("");
        validator.validate();

        ctx.fireChannelReadComplete();
        validator.validate();

        ctx.fireExceptionCaught(new Exception());
        validator.validate();

        ctx.fireChannelActive();
        validator.validate();

        ctx.fireChannelRegistered();
        validator.validate();

        ctx.fireChannelInactive();
        validator.validate();

        ctx.fireChannelUnregistered();
        validator.validate();

        ctx.fireChannelWritabilityChanged();
        validator.validate();
    }

    private static void assertChannelPipelineException(ChannelFuture f) {
        try {
            f.syncUninterruptibly();
        } catch (CompletionException e) {
            assertThat(e.getCause(), Matchers.instanceOf(ChannelPipelineException.class));
        }
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
<<<<<<< HEAD
    public void testAddReplaceHandlerCalled() throws Throwable {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
=======
    public void testAddReplaceHandlerCalledOnceRegistered() throws Throwable {
        ChannelPipeline pipeline = new LocalChannel().pipeline();
>>>>>>> dev
        CallbackCheckHandler handler = new CallbackCheckHandler();
        CallbackCheckHandler handler2 = new CallbackCheckHandler();

        pipeline.addFirst(handler);
        pipeline.replace(handler, null, handler2);

        assertTrue(handler.addedHandler.get());
        assertTrue(handler.removedHandler.get());
        assertTrue(handler2.addedHandler.get());
        assertNull(handler2.removedHandler.getNow());

        pipeline.channel().register().syncUninterruptibly();
        Throwable cause = handler.error.get();
        if (cause != null) {
            throw cause;
        }

        Throwable cause2 = handler2.error.get();
        if (cause2 != null) {
            throw cause2;
        }

        assertNull(handler2.removedHandler.getNow());
        pipeline.remove(handler2);
        assertTrue(handler2.removedHandler.get());
        pipeline.channel().close().syncUninterruptibly();
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testAddBefore() throws Throwable {
        EventLoopGroup defaultGroup = new MultithreadEventLoopGroup(2, LocalHandler.newFactory());
        try {
            EventLoop eventLoop1 = defaultGroup.next();
            EventLoop eventLoop2 = defaultGroup.next();

            ChannelPipeline pipeline1 = new LocalChannel(eventLoop1).pipeline();
            ChannelPipeline pipeline2 = new LocalChannel(eventLoop2).pipeline();

            pipeline1.channel().register().syncUninterruptibly();
            pipeline2.channel().register().syncUninterruptibly();

            CountDownLatch latch = new CountDownLatch(2 * 10);
            for (int i = 0; i < 10; i++) {
                eventLoop1.execute(new TestTask(pipeline2, latch));
                eventLoop2.execute(new TestTask(pipeline1, latch));
            }
            latch.await();
            pipeline1.channel().close().syncUninterruptibly();
            pipeline2.channel().close().syncUninterruptibly();
        } finally {
            defaultGroup.shutdownGracefully();
        }
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
<<<<<<< HEAD
    public void testAddInListenerNio() throws Throwable {
        EventLoopGroup nioEventLoopGroup = new MultithreadEventLoopGroup(1, NioHandler.newFactory());
        try {
            testAddInListener(new NioSocketChannel(nioEventLoopGroup.next()));
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
=======
    public void testAddInListenerNio() {
        testAddInListener(new NioSocketChannel(), new NioEventLoopGroup(1));
    }

    @SuppressWarnings("deprecation")
    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testAddInListenerOio() {
        testAddInListener(new OioSocketChannel(), new OioEventLoopGroup(1));
>>>>>>> dev
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
<<<<<<< HEAD
    public void testAddInListenerLocal() throws Throwable {
        testAddInListener(newLocalChannel());
    }

    private static void testAddInListener(Channel channel) throws Throwable {
=======
    public void testAddInListenerLocal() {
        testAddInListener(new LocalChannel(), new DefaultEventLoopGroup(1));
    }

    private static void testAddInListener(Channel channel, EventLoopGroup group) {
>>>>>>> dev
        ChannelPipeline pipeline1 = channel.pipeline();
        try {
            final Object event = new Object();
            final Promise<Object> promise = ImmediateEventExecutor.INSTANCE.newPromise();
<<<<<<< HEAD
            pipeline1.channel().register().addListener((ChannelFutureListener) future -> {
                ChannelPipeline pipeline = future.channel().pipeline();
                final AtomicBoolean handlerAddedCalled = new AtomicBoolean();
                pipeline.addLast(new ChannelHandler() {
                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                        handlerAddedCalled.set(true);
                    }

                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        promise.setSuccess(event);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        promise.setFailure(cause);
=======
            group.register(pipeline1.channel()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    ChannelPipeline pipeline = future.channel().pipeline();
                    final AtomicBoolean handlerAddedCalled = new AtomicBoolean();
                    pipeline.addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void handlerAdded(ChannelHandlerContext ctx) {
                            handlerAddedCalled.set(true);
                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                            promise.setSuccess(event);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            promise.setFailure(cause);
                        }
                    });
                    if (!handlerAddedCalled.get()) {
                        promise.setFailure(new AssertionError("handlerAdded(...) should have been called"));
                        return;
>>>>>>> dev
                    }
                });
                if (!handlerAddedCalled.get()) {
                    promise.setFailure(new AssertionError("handlerAdded(...) should have been called"));
                    return;
                }
                // This event must be captured by the added handler.
                pipeline.fireUserEventTriggered(event);
            });
            assertSame(event, promise.syncUninterruptibly().getNow());
        } finally {
            pipeline1.channel().close().syncUninterruptibly();
        }
    }

    @Test
    public void testNullName() {
        ChannelPipeline pipeline = newLocalChannel().pipeline();
        pipeline.addLast(newHandler());
        pipeline.addLast(null, newHandler());
        pipeline.addFirst(newHandler());
        pipeline.addFirst(null, newHandler());

        pipeline.addLast("test", newHandler());
        pipeline.addAfter("test", null, newHandler());

        pipeline.addBefore("test", null, newHandler());
    }

<<<<<<< HEAD
    // Test for https://github.com/netty/netty/issues/8676.
    @Test
    public void testHandlerRemovedOnlyCalledWhenHandlerAddedCalled() throws Exception {
        EventLoopGroup group = new MultithreadEventLoopGroup(1, LocalHandler.newFactory());
        try {
            final AtomicReference<Error> errorRef = new AtomicReference<>();

            // As this only happens via a race we will verify 500 times. This was good enough to have it failed most of
            // the time.
            for (int i = 0; i < 500; i++) {

                ChannelPipeline pipeline = new LocalChannel(group.next()).pipeline();
                pipeline.channel().register().sync();

                final CountDownLatch latch = new CountDownLatch(1);

                pipeline.addLast(new ChannelHandler() {
                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        // Block just for a bit so we have a chance to trigger the race mentioned in the issue.
                        latch.await(50, TimeUnit.MILLISECONDS);
                    }
                });

                // Close the pipeline which will call destroy0(). This will remove each handler in the pipeline and
                // should call handlerRemoved(...) if and only if handlerAdded(...) was called for the handler before.
                pipeline.close();

                pipeline.addLast(new ChannelHandler() {
                    private boolean handerAddedCalled;

                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) {
                        handerAddedCalled = true;
                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) {
                        if (!handerAddedCalled) {
                            errorRef.set(new AssertionError(
                                    "handlerRemoved(...) called without handlerAdded(...) before"));
                        }
                    }
                });

                latch.countDown();

                pipeline.channel().closeFuture().syncUninterruptibly();

                // Schedule something on the EventLoop to ensure all other scheduled tasks had a chance to complete.
                pipeline.channel().eventLoop().submit(() -> {
                    // NOOP
                }).syncUninterruptibly();
                Error error = errorRef.get();
                if (error != null) {
                    throw error;
=======
    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testUnorderedEventExecutor() throws Throwable {
        ChannelPipeline pipeline1 = new LocalChannel().pipeline();
        EventExecutorGroup eventExecutors = new UnorderedThreadPoolEventExecutor(2);
        EventLoopGroup defaultGroup = new DefaultEventLoopGroup(1);
        try {
            EventLoop eventLoop1 = defaultGroup.next();
            eventLoop1.register(pipeline1.channel()).syncUninterruptibly();
            final CountDownLatch latch = new CountDownLatch(1);
            pipeline1.addLast(eventExecutors, new ChannelInboundHandlerAdapter() {
                @Override
                public void handlerAdded(ChannelHandlerContext ctx) {
                    // Just block one of the two threads.
                    LockSupport.park();
                }

                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                    latch.countDown();
>>>>>>> dev
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testSkipHandlerMethodsIfAnnotated() {
        EmbeddedChannel channel = new EmbeddedChannel(true);
        ChannelPipeline pipeline = channel.pipeline();

        final class SkipHandler implements ChannelHandler {
            private int state = 2;
            private Error errorRef;

            private void fail() {
                errorRef = new AssertionError("Method should never been called");
            }

            @Skip
            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
                fail();
                ctx.bind(localAddress, promise);
            }

            @Skip
            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                                SocketAddress localAddress, ChannelPromise promise) {
                fail();
                ctx.connect(remoteAddress, localAddress, promise);
            }

            @Skip
            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.disconnect(promise);
            }

            @Skip
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.close(promise);
            }

            @Skip
            @Override
            public void register(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.register(promise);
            }

            @Skip
            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.deregister(promise);
            }

            @Skip
            @Override
            public void read(ChannelHandlerContext ctx) {
                fail();
                ctx.read();
            }

            @Skip
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                fail();
                ctx.write(msg, promise);
            }

            @Skip
            @Override
            public void flush(ChannelHandlerContext ctx) {
                fail();
                ctx.flush();
            }

            @Skip
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelRegistered();
            }

            @Skip
            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelUnregistered();
            }

            @Skip
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelActive();
            }

            @Skip
            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelInactive();
            }

            @Skip
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                fail();
                ctx.fireChannelRead(msg);
            }

            @Skip
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelReadComplete();
            }

            @Skip
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                fail();
                ctx.fireUserEventTriggered(evt);
            }

            @Skip
            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelWritabilityChanged();
            }

            @Skip
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                fail();
                ctx.fireExceptionCaught(cause);
            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                state--;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                state--;
            }

            void assertSkipped() {
                assertEquals(0, state);
                Error error = errorRef;
                if (error != null) {
                    throw error;
                }
            }
        }

        final class OutboundCalledHandler implements ChannelHandler {
            private static final int MASK_BIND = 1;
            private static final int MASK_CONNECT = 1 << 1;
            private static final int MASK_DISCONNECT = 1 << 2;
            private static final int MASK_CLOSE = 1 << 3;
            private static final int MASK_REGISTER = 1 << 4;
            private static final int MASK_DEREGISTER = 1 << 5;
            private static final int MASK_READ = 1 << 6;
            private static final int MASK_WRITE = 1 << 7;
            private static final int MASK_FLUSH = 1 << 8;
            private static final int MASK_ADDED = 1 << 9;
            private static final int MASK_REMOVED = 1 << 10;

            private int executionMask;

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                executionMask |= MASK_ADDED;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                executionMask |= MASK_REMOVED;
            }

            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
                executionMask |= MASK_BIND;
                promise.setSuccess();
            }

            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                                SocketAddress localAddress, ChannelPromise promise) {
                executionMask |= MASK_CONNECT;
                promise.setSuccess();
            }

            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_DISCONNECT;
                promise.setSuccess();
            }

            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_CLOSE;
                promise.setSuccess();
            }

            @Override
            public void register(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_REGISTER;
                promise.setSuccess();
            }

            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_DEREGISTER;
                promise.setSuccess();
            }

            @Override
            public void read(ChannelHandlerContext ctx) {
                executionMask |= MASK_READ;
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                executionMask |= MASK_WRITE;
                promise.setSuccess();
            }

            @Override
            public void flush(ChannelHandlerContext ctx) {
                executionMask |= MASK_FLUSH;
            }

            void assertCalled() {
                assertCalled("handlerAdded", MASK_ADDED);
                assertCalled("handlerRemoved", MASK_REMOVED);
                assertCalled("bind", MASK_BIND);
                assertCalled("connect", MASK_CONNECT);
                assertCalled("disconnect", MASK_DISCONNECT);
                assertCalled("close", MASK_CLOSE);
                assertCalled("register", MASK_REGISTER);
                assertCalled("deregister", MASK_DEREGISTER);
                assertCalled("read", MASK_READ);
                assertCalled("write", MASK_WRITE);
                assertCalled("flush", MASK_FLUSH);
            }

            private void assertCalled(String methodName, int mask) {
                assertTrue((executionMask & mask) != 0, methodName + " was not called");
            }
        }

        final class InboundCalledHandler implements ChannelHandler {

            private static final int MASK_CHANNEL_REGISTER = 1;
            private static final int MASK_CHANNEL_UNREGISTER = 1 << 1;
            private static final int MASK_CHANNEL_ACTIVE = 1 << 2;
            private static final int MASK_CHANNEL_INACTIVE = 1 << 3;
            private static final int MASK_CHANNEL_READ = 1 << 4;
            private static final int MASK_CHANNEL_READ_COMPLETE = 1 << 5;
            private static final int MASK_USER_EVENT_TRIGGERED = 1 << 6;
            private static final int MASK_CHANNEL_WRITABILITY_CHANGED = 1 << 7;
            private static final int MASK_EXCEPTION_CAUGHT = 1 << 8;
            private static final int MASK_ADDED = 1 << 9;
            private static final int MASK_REMOVED = 1 << 10;

            private int executionMask;

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                executionMask |= MASK_ADDED;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                executionMask |= MASK_REMOVED;
            }

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_REGISTER;
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_UNREGISTER;
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_ACTIVE;
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_INACTIVE;
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                executionMask |= MASK_CHANNEL_READ;
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_READ_COMPLETE;
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                executionMask |= MASK_USER_EVENT_TRIGGERED;
            }

            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_WRITABILITY_CHANGED;
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                executionMask |= MASK_EXCEPTION_CAUGHT;
            }

            void assertCalled() {
                assertCalled("handlerAdded", MASK_ADDED);
                assertCalled("handlerRemoved", MASK_REMOVED);
                assertCalled("channelRegistered", MASK_CHANNEL_REGISTER);
                assertCalled("channelUnregistered", MASK_CHANNEL_UNREGISTER);
                assertCalled("channelActive", MASK_CHANNEL_ACTIVE);
                assertCalled("channelInactive", MASK_CHANNEL_INACTIVE);
                assertCalled("channelRead", MASK_CHANNEL_READ);
                assertCalled("channelReadComplete", MASK_CHANNEL_READ_COMPLETE);
                assertCalled("userEventTriggered", MASK_USER_EVENT_TRIGGERED);
                assertCalled("channelWritabilityChanged", MASK_CHANNEL_WRITABILITY_CHANGED);
                assertCalled("exceptionCaught", MASK_EXCEPTION_CAUGHT);
            }

            private void assertCalled(String methodName, int mask) {
                assertTrue((executionMask & mask) != 0, methodName + " was not called");
            }
        }

        OutboundCalledHandler outboundCalledHandler = new OutboundCalledHandler();
        SkipHandler skipHandler = new SkipHandler();
        InboundCalledHandler inboundCalledHandler = new InboundCalledHandler();
        pipeline.addLast(outboundCalledHandler, skipHandler, inboundCalledHandler);

        pipeline.fireChannelRegistered();
        pipeline.fireChannelUnregistered();
        pipeline.fireChannelActive();
        pipeline.fireChannelInactive();
        pipeline.fireChannelRead("");
        pipeline.fireChannelReadComplete();
        pipeline.fireChannelWritabilityChanged();
        pipeline.fireUserEventTriggered("");
        pipeline.fireExceptionCaught(new Exception());

        pipeline.register().syncUninterruptibly();
        pipeline.deregister().syncUninterruptibly();
        pipeline.bind(new SocketAddress() {
        }).syncUninterruptibly();
        pipeline.connect(new SocketAddress() {
        }).syncUninterruptibly();
        pipeline.disconnect().syncUninterruptibly();
        pipeline.close().syncUninterruptibly();
        pipeline.write("");
        pipeline.flush();
        pipeline.read();

        pipeline.remove(outboundCalledHandler);
        pipeline.remove(inboundCalledHandler);
        pipeline.remove(skipHandler);

        assertFalse(channel.finish());

        outboundCalledHandler.assertCalled();
        inboundCalledHandler.assertCalled();
        skipHandler.assertSkipped();
    }

    @Test
    public void testWriteThrowsReleaseMessage() {
        testWriteThrowsReleaseMessage0(false);
    }

    @Test
<<<<<<< HEAD
    public void testWriteAndFlushThrowsReleaseMessage() {
        testWriteThrowsReleaseMessage0(true);
    }
=======
    public void testNotPinExecutor() {
        EventExecutorGroup group = new DefaultEventExecutorGroup(2);
        ChannelPipeline pipeline = new LocalChannel().pipeline();
        pipeline.channel().config().setOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);

        pipeline.addLast(group, "h1", new ChannelInboundHandlerAdapter());
        pipeline.addLast(group, "h2", new ChannelInboundHandlerAdapter());

        EventExecutor executor1 = pipeline.context("h1").executor();
        EventExecutor executor2 = pipeline.context("h2").executor();
        assertNotNull(executor1);
        assertNotNull(executor2);
        assertNotSame(executor1, executor2);
        group.shutdownGracefully(0, 0, TimeUnit.SECONDS);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testVoidPromiseNotify() {
        ChannelPipeline pipeline1 = new LocalChannel().pipeline();

        EventLoopGroup defaultGroup = new DefaultEventLoopGroup(1);
        EventLoop eventLoop1 = defaultGroup.next();
        final Promise<Throwable> promise = eventLoop1.newPromise();
        final Exception exception = new IllegalArgumentException();
        try {
            eventLoop1.register(pipeline1.channel()).syncUninterruptibly();
            pipeline1.addLast(new ChannelDuplexHandler() {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    throw exception;
                }
>>>>>>> dev

    private void testWriteThrowsReleaseMessage0(boolean flush) {
        ReferenceCounted referenceCounted = new AbstractReferenceCounted() {
            @Override
            protected void deallocate() {
                // NOOP
            }

            @Override
            public ReferenceCounted touch(Object hint) {
                return this;
            }
        };
        assertEquals(1, referenceCounted.refCnt());

        Channel channel = new LocalChannel(group.next());
        Channel channel2 = new LocalChannel(group.next());
        channel.register().syncUninterruptibly();
        channel2.register().syncUninterruptibly();

        try {
            if (flush) {
                channel.writeAndFlush(referenceCounted, channel2.newPromise());
            } else {
                channel.write(referenceCounted, channel2.newPromise());
            }
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
        assertEquals(0, referenceCounted.refCnt());

        channel.close().syncUninterruptibly();
        channel2.close().syncUninterruptibly();
    }

<<<<<<< HEAD
=======
    // Test for https://github.com/netty/netty/issues/8676.
    @Test
    public void testHandlerRemovedOnlyCalledWhenHandlerAddedCalled() throws Exception {
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            final AtomicReference<Error> errorRef = new AtomicReference<Error>();

            // As this only happens via a race we will verify 500 times. This was good enough to have it failed most of
            // the time.
            for (int i = 0; i < 500; i++) {

                ChannelPipeline pipeline = new LocalChannel().pipeline();
                group.register(pipeline.channel()).sync();

                final CountDownLatch latch = new CountDownLatch(1);

                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        // Block just for a bit so we have a chance to trigger the race mentioned in the issue.
                        latch.await(50, TimeUnit.MILLISECONDS);
                    }
                });

                // Close the pipeline which will call destroy0(). This will remove each handler in the pipeline and
                // should call handlerRemoved(...) if and only if handlerAdded(...) was called for the handler before.
                pipeline.close();

                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    private boolean handerAddedCalled;

                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) {
                        handerAddedCalled = true;
                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) {
                        if (!handerAddedCalled) {
                            errorRef.set(new AssertionError(
                                    "handlerRemoved(...) called without handlerAdded(...) before"));
                        }
                    }
                });

                latch.countDown();

                pipeline.channel().closeFuture().syncUninterruptibly();

                // Schedule something on the EventLoop to ensure all other scheduled tasks had a chance to complete.
                pipeline.channel().eventLoop().submit(new Runnable() {
                    @Override
                    public void run() {
                        // NOOP
                    }
                }).syncUninterruptibly();
                Error error = errorRef.get();
                if (error != null) {
                    throw error;
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testSkipHandlerMethodsIfAnnotated() {
        EmbeddedChannel channel = new EmbeddedChannel(true);
        ChannelPipeline pipeline = channel.pipeline();

        final class SkipHandler implements ChannelInboundHandler, ChannelOutboundHandler {
            private int state = 2;
            private Error errorRef;

            private void fail() {
                errorRef = new AssertionError("Method should never been called");
            }

            @Skip
            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
                fail();
                ctx.bind(localAddress, promise);
            }

            @Skip
            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                                SocketAddress localAddress, ChannelPromise promise) {
                fail();
                ctx.connect(remoteAddress, localAddress, promise);
            }

            @Skip
            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.disconnect(promise);
            }

            @Skip
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.close(promise);
            }

            @Skip
            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
                fail();
                ctx.deregister(promise);
            }

            @Skip
            @Override
            public void read(ChannelHandlerContext ctx) {
                fail();
                ctx.read();
            }

            @Skip
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                fail();
                ctx.write(msg, promise);
            }

            @Skip
            @Override
            public void flush(ChannelHandlerContext ctx) {
                fail();
                ctx.flush();
            }

            @Skip
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelRegistered();
            }

            @Skip
            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelUnregistered();
            }

            @Skip
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelActive();
            }

            @Skip
            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelInactive();
            }

            @Skip
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                fail();
                ctx.fireChannelRead(msg);
            }

            @Skip
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelReadComplete();
            }

            @Skip
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                fail();
                ctx.fireUserEventTriggered(evt);
            }

            @Skip
            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) {
                fail();
                ctx.fireChannelWritabilityChanged();
            }

            @Skip
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                fail();
                ctx.fireExceptionCaught(cause);
            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                state--;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                state--;
            }

            void assertSkipped() {
                assertEquals(0, state);
                Error error = errorRef;
                if (error != null) {
                    throw error;
                }
            }
        }

        final class OutboundCalledHandler extends ChannelOutboundHandlerAdapter {
            private static final int MASK_BIND = 1;
            private static final int MASK_CONNECT = 1 << 1;
            private static final int MASK_DISCONNECT = 1 << 2;
            private static final int MASK_CLOSE = 1 << 3;
            private static final int MASK_DEREGISTER = 1 << 4;
            private static final int MASK_READ = 1 << 5;
            private static final int MASK_WRITE = 1 << 6;
            private static final int MASK_FLUSH = 1 << 7;
            private static final int MASK_ADDED = 1 << 8;
            private static final int MASK_REMOVED = 1 << 9;

            private int executionMask;

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                executionMask |= MASK_ADDED;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                executionMask |= MASK_REMOVED;
            }

            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
                executionMask |= MASK_BIND;
                promise.setSuccess();
            }

            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                                SocketAddress localAddress, ChannelPromise promise) {
                executionMask |= MASK_CONNECT;
                promise.setSuccess();
            }

            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_DISCONNECT;
                promise.setSuccess();
            }

            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_CLOSE;
                promise.setSuccess();
            }

            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
                executionMask |= MASK_DEREGISTER;
                promise.setSuccess();
            }

            @Override
            public void read(ChannelHandlerContext ctx) {
                executionMask |= MASK_READ;
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                executionMask |= MASK_WRITE;
                promise.setSuccess();
            }

            @Override
            public void flush(ChannelHandlerContext ctx) {
                executionMask |= MASK_FLUSH;
            }

            void assertCalled() {
                assertCalled("handlerAdded", MASK_ADDED);
                assertCalled("handlerRemoved", MASK_REMOVED);
                assertCalled("bind", MASK_BIND);
                assertCalled("connect", MASK_CONNECT);
                assertCalled("disconnect", MASK_DISCONNECT);
                assertCalled("close", MASK_CLOSE);
                assertCalled("deregister", MASK_DEREGISTER);
                assertCalled("read", MASK_READ);
                assertCalled("write", MASK_WRITE);
                assertCalled("flush", MASK_FLUSH);
            }

            private void assertCalled(String methodName, int mask) {
                assertTrue((executionMask & mask) != 0, methodName + " was not called");
            }
        }

        final class InboundCalledHandler extends ChannelInboundHandlerAdapter {

            private static final int MASK_CHANNEL_REGISTER = 1;
            private static final int MASK_CHANNEL_UNREGISTER = 1 << 1;
            private static final int MASK_CHANNEL_ACTIVE = 1 << 2;
            private static final int MASK_CHANNEL_INACTIVE = 1 << 3;
            private static final int MASK_CHANNEL_READ = 1 << 4;
            private static final int MASK_CHANNEL_READ_COMPLETE = 1 << 5;
            private static final int MASK_USER_EVENT_TRIGGERED = 1 << 6;
            private static final int MASK_CHANNEL_WRITABILITY_CHANGED = 1 << 7;
            private static final int MASK_EXCEPTION_CAUGHT = 1 << 8;
            private static final int MASK_ADDED = 1 << 9;
            private static final int MASK_REMOVED = 1 << 10;

            private int executionMask;

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                executionMask |= MASK_ADDED;
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) {
                executionMask |= MASK_REMOVED;
            }

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_REGISTER;
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_UNREGISTER;
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_ACTIVE;
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_INACTIVE;
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                executionMask |= MASK_CHANNEL_READ;
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_READ_COMPLETE;
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                executionMask |= MASK_USER_EVENT_TRIGGERED;
            }

            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) {
                executionMask |= MASK_CHANNEL_WRITABILITY_CHANGED;
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                executionMask |= MASK_EXCEPTION_CAUGHT;
            }

            void assertCalled() {
                assertCalled("handlerAdded", MASK_ADDED);
                assertCalled("handlerRemoved", MASK_REMOVED);
                assertCalled("channelRegistered", MASK_CHANNEL_REGISTER);
                assertCalled("channelUnregistered", MASK_CHANNEL_UNREGISTER);
                assertCalled("channelActive", MASK_CHANNEL_ACTIVE);
                assertCalled("channelInactive", MASK_CHANNEL_INACTIVE);
                assertCalled("channelRead", MASK_CHANNEL_READ);
                assertCalled("channelReadComplete", MASK_CHANNEL_READ_COMPLETE);
                assertCalled("userEventTriggered", MASK_USER_EVENT_TRIGGERED);
                assertCalled("channelWritabilityChanged", MASK_CHANNEL_WRITABILITY_CHANGED);
                assertCalled("exceptionCaught", MASK_EXCEPTION_CAUGHT);
            }

            private void assertCalled(String methodName, int mask) {
                assertTrue((executionMask & mask) != 0, methodName + " was not called");
            }
        }

        OutboundCalledHandler outboundCalledHandler = new OutboundCalledHandler();
        SkipHandler skipHandler = new SkipHandler();
        InboundCalledHandler inboundCalledHandler = new InboundCalledHandler();
        pipeline.addLast(outboundCalledHandler, skipHandler, inboundCalledHandler);

        pipeline.fireChannelRegistered();
        pipeline.fireChannelUnregistered();
        pipeline.fireChannelActive();
        pipeline.fireChannelInactive();
        pipeline.fireChannelRead("");
        pipeline.fireChannelReadComplete();
        pipeline.fireChannelWritabilityChanged();
        pipeline.fireUserEventTriggered("");
        pipeline.fireExceptionCaught(new Exception());

        pipeline.deregister().syncUninterruptibly();
        pipeline.bind(new SocketAddress() {
        }).syncUninterruptibly();
        pipeline.connect(new SocketAddress() {
        }).syncUninterruptibly();
        pipeline.disconnect().syncUninterruptibly();
        pipeline.close().syncUninterruptibly();
        pipeline.write("");
        pipeline.flush();
        pipeline.read();

        pipeline.remove(outboundCalledHandler);
        pipeline.remove(inboundCalledHandler);
        pipeline.remove(skipHandler);

        assertFalse(channel.finish());

        outboundCalledHandler.assertCalled();
        inboundCalledHandler.assertCalled();
        skipHandler.assertSkipped();
    }

    @Test
    public void testWriteThrowsReleaseMessage() {
        testWriteThrowsReleaseMessage0(false);
    }

    @Test
    public void testWriteAndFlushThrowsReleaseMessage() {
        testWriteThrowsReleaseMessage0(true);
    }

    private void testWriteThrowsReleaseMessage0(boolean flush) {
        ReferenceCounted referenceCounted = new AbstractReferenceCounted() {
            @Override
            protected void deallocate() {
                // NOOP
            }

            @Override
            public ReferenceCounted touch(Object hint) {
                return this;
            }
        };
        assertEquals(1, referenceCounted.refCnt());

        Channel channel = new LocalChannel();
        Channel channel2 = new LocalChannel();
        group.register(channel).syncUninterruptibly();
        group.register(channel2).syncUninterruptibly();

        try {
            if (flush) {
                channel.writeAndFlush(referenceCounted, channel2.newPromise());
            } else {
                channel.write(referenceCounted, channel2.newPromise());
            }
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
        assertEquals(0, referenceCounted.refCnt());

        channel.close().syncUninterruptibly();
        channel2.close().syncUninterruptibly();
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerAddedFailedButHandlerStillRemoved() throws InterruptedException {
        testHandlerAddedFailedButHandlerStillRemoved0(false);
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testHandlerAddedFailedButHandlerStillRemovedWithLaterRegister() throws InterruptedException {
        testHandlerAddedFailedButHandlerStillRemoved0(true);
    }

    private static void testHandlerAddedFailedButHandlerStillRemoved0(boolean lateRegister)
            throws InterruptedException {
        EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(16);
        final int numHandlers = 32;
        try {
            Channel channel = new LocalChannel();
            channel.config().setOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, false);
            if (!lateRegister) {
                group.register(channel).sync();
            }
            channel.pipeline().addFirst(newHandler());

            List<CountDownLatch> latchList = new ArrayList<CountDownLatch>(numHandlers);
            for (int i = 0; i < numHandlers; i++) {
                CountDownLatch latch = new CountDownLatch(1);
                channel.pipeline().addFirst(executorGroup, "h" + i, new BadChannelHandler(latch));
                latchList.add(latch);
            }
            if (lateRegister) {
                group.register(channel).sync();
            }

            for (int i = 0; i < numHandlers; i++) {
                // Wait until the latch was countDown which means handlerRemoved(...) was called.
                latchList.get(i).await();
                assertNull(channel.pipeline().get("h" + i));
            }
        } finally {
            executorGroup.shutdownGracefully();
        }
    }

    private static final class BadChannelHandler extends ChannelHandlerAdapter {
        private final CountDownLatch latch;

        BadChannelHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            TimeUnit.MILLISECONDS.sleep(10);
            throw new RuntimeException();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            latch.countDown();
        }
    }

>>>>>>> dev
    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void handlerAddedStateUpdatedBeforeHandlerAddedDoneForceEventLoop() throws InterruptedException {
        handlerAddedStateUpdatedBeforeHandlerAddedDone(true);
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void handlerAddedStateUpdatedBeforeHandlerAddedDoneOnCallingThread() throws InterruptedException {
        handlerAddedStateUpdatedBeforeHandlerAddedDone(false);
    }

    private static void handlerAddedStateUpdatedBeforeHandlerAddedDone(boolean executeInEventLoop)
            throws InterruptedException {
        final ChannelPipeline pipeline = newLocalChannel().pipeline();
        final Object userEvent = new Object();
        final Object writeObject = new Object();
        final CountDownLatch doneLatch = new CountDownLatch(1);

        Runnable r = () -> {
            pipeline.addLast(new ChannelHandler() {
                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                    if (evt == userEvent) {
                        ctx.write(writeObject);
                    }
                    ctx.fireUserEventTriggered(evt);
                }
            });
            pipeline.addFirst(new ChannelHandler() {
                @Override
                public void handlerAdded(ChannelHandlerContext ctx) {
                    ctx.fireUserEventTriggered(userEvent);
                }

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                    if (msg == writeObject) {
                        doneLatch.countDown();
                    }
                    ctx.write(msg, promise);
                }
            });
        };

        if (executeInEventLoop) {
            pipeline.channel().eventLoop().execute(r);
        } else {
            r.run();
        }

        doneLatch.await();
    }

    private static final class TestTask implements Runnable {

        private final ChannelPipeline pipeline;
        private final CountDownLatch latch;

        TestTask(ChannelPipeline pipeline, CountDownLatch latch) {
            this.pipeline = pipeline;
            this.latch = latch;
        }

        @Override
        public void run() {
            pipeline.addLast(new ChannelHandler() { });
            latch.countDown();
        }
    }

    private static final class CallbackCheckHandler extends ChannelHandlerAdapter {
        final Promise<Boolean> addedHandler = ImmediateEventExecutor.INSTANCE.newPromise();
        final Promise<Boolean> removedHandler = ImmediateEventExecutor.INSTANCE.newPromise();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            if (!addedHandler.trySuccess(true)) {
                error.set(new AssertionError("handlerAdded(...) called multiple times: " + ctx.name()));
            } else if (removedHandler.getNow() == Boolean.TRUE) {
                error.set(new AssertionError("handlerRemoved(...) called before handlerAdded(...): " + ctx.name()));
            }
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            if (!removedHandler.trySuccess(true)) {
                error.set(new AssertionError("handlerRemoved(...) called multiple times: " + ctx.name()));
            } else if (addedHandler.getNow() == Boolean.FALSE) {
                error.set(new AssertionError("handlerRemoved(...) called before handlerAdded(...): " + ctx.name()));
            }
        }
    }

    private static final class CheckExceptionHandler implements ChannelHandler {
        private final Throwable expected;
        private final Promise<Void> promise;

        CheckExceptionHandler(Throwable expected, Promise<Void> promise) {
            this.expected = expected;
            this.promise = promise;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (cause instanceof ChannelPipelineException && cause.getCause() == expected) {
                promise.setSuccess(null);
            } else {
                promise.setFailure(new AssertionError("cause not the expected instance"));
            }
        }
    }

    private static void assertHandler(CheckOrderHandler actual, CheckOrderHandler... handlers) throws Throwable {
        for (CheckOrderHandler h : handlers) {
            if (h == actual) {
                actual.checkError();
                return;
            }
        }
        fail("handler was not one of the expected handlers");
    }

    private static final class CheckOrderHandler implements ChannelHandler {
        private final Queue<CheckOrderHandler> addedQueue;
        private final Queue<CheckOrderHandler> removedQueue;
        private final AtomicReference<Throwable> error = new AtomicReference<>();

        CheckOrderHandler(Queue<CheckOrderHandler> addedQueue, Queue<CheckOrderHandler> removedQueue) {
            this.addedQueue = addedQueue;
            this.removedQueue = removedQueue;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            addedQueue.add(this);
            checkExecutor(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            removedQueue.add(this);
            checkExecutor(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            error.set(cause);
        }

        void checkError() throws Throwable {
            Throwable cause = error.get();
            if (cause != null) {
                throw cause;
            }
        }

        private void checkExecutor(ChannelHandlerContext ctx) {
            if (!ctx.executor().inEventLoop()) {
                error.set(new AssertionError());
            }
        }
    }

    private static final class CheckEventExecutorHandler extends ChannelHandlerAdapter {
        final EventExecutor executor;
        final Promise<Void> addedPromise;
        final Promise<Void> removedPromise;

        CheckEventExecutorHandler(EventExecutor executor) {
            this.executor = executor;
            addedPromise = executor.newPromise();
            removedPromise = executor.newPromise();
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            assertExecutor(ctx, addedPromise);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            assertExecutor(ctx, removedPromise);
        }

        private void assertExecutor(ChannelHandlerContext ctx, Promise<Void> promise) {
            final boolean same;
            try {
                same = executor == ctx.executor();
            } catch (Throwable cause) {
                promise.setFailure(cause);
                return;
            }
            if (same) {
                promise.setSuccess(null);
            } else {
                promise.setFailure(new AssertionError("EventExecutor not the same"));
            }
        }
    }
    private static final class ErrorChannelHandler extends ChannelHandlerAdapter {
        private final AtomicReference<Throwable> error;

        ErrorChannelHandler(AtomicReference<Throwable> error) {
            this.error = error;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            error.set(new AssertionError());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            error.set(new AssertionError());
        }
    }

    private static int next(DefaultChannelHandlerContext ctx) {
        DefaultChannelHandlerContext next = ctx.next;
        if (next == null) {
            return Integer.MAX_VALUE;
        }

        return toInt(next.name());
    }

    private static int toInt(String name) {
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void verifyContextNumber(ChannelPipeline pipeline, int expectedNumber) {
        assertEquals(expectedNumber, pipeline.names().size());
        assertEquals(expectedNumber, pipeline.toMap().size());

        pipeline.executor().submit(() -> {
            DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) pipeline.firstContext();
            int handlerNumber = 0;
            if (ctx != null) {
                for (;;) {
                    handlerNumber++;
                    if (ctx == pipeline.lastContext()) {
                        break;
                    }
                    ctx = ctx.next;
                }
            }
            assertEquals(expectedNumber, handlerNumber);
        }).syncUninterruptibly();
    }

    private static ChannelHandler[] newHandlers(int num) {
        assert num > 0;

        ChannelHandler[] handlers = new ChannelHandler[num];
        for (int i = 0; i < num; i++) {
            handlers[i] = newHandler();
        }

        return handlers;
    }

    private static ChannelHandler newHandler() {
        return new TestHandler();
    }

    @Sharable
    private static class TestHandler implements ChannelHandler { }

    private static class BufferedTestHandler implements ChannelHandler {
        final Queue<Object> inboundBuffer = new ArrayDeque<>();
        final Queue<Object> outboundBuffer = new ArrayDeque<>();

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            outboundBuffer.add(msg);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            inboundBuffer.add(msg);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            if (!inboundBuffer.isEmpty()) {
                for (Object o: inboundBuffer) {
                    ctx.fireChannelRead(o);
                }
                ctx.fireChannelReadComplete();
            }
            if (!outboundBuffer.isEmpty()) {
                for (Object o: outboundBuffer) {
                    ctx.write(o);
                }
                ctx.flush();
            }
        }
    }

    /** Test handler to validate life-cycle aware behavior. */
    private static final class LifeCycleAwareTestHandler extends ChannelHandlerAdapter {
        private final String name;

        private boolean afterAdd;
        private boolean afterRemove;

        /**
         * Constructs life-cycle aware test handler.
         *
         * @param name Handler name to display in assertion messages.
         */
        private LifeCycleAwareTestHandler(String name) {
            this.name = name;
        }

        public void validate(boolean afterAdd, boolean afterRemove) {
            assertEquals(afterAdd, this.afterAdd, name);
            assertEquals(afterRemove, this.afterRemove, name);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            validate(false, false);

            afterAdd = true;
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            validate(true, false);

            afterRemove = true;
        }
    }

    private static final class WrapperExecutor extends AbstractEventExecutor {

        private final ExecutorService wrapped = Executors.newSingleThreadExecutor();

        @Override
        public boolean isShuttingDown() {
            return wrapped.isShutdown();
        }

        @Override
        public Future<?> shutdownGracefully(long l, long l2, TimeUnit timeUnit) {
            throw new IllegalStateException();
        }

        @Override
        public Future<?> terminationFuture() {
            throw new IllegalStateException();
        }

        @Override
        public void shutdown() {
            wrapped.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return wrapped.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return wrapped.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return wrapped.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return wrapped.awaitTermination(timeout, unit);
        }

        @Override
        public boolean inEventLoop(Thread thread) {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            wrapped.execute(command);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(
                Runnable command, long initialDelay, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }
}
