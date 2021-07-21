/*
 * Copyright 2016 The Netty Project
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
package io.netty.handler.ssl;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalHandler;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

import io.netty.util.internal.PlatformDependent;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
<<<<<<< HEAD

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

=======

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
>>>>>>> dev

public class SniClientTest {
    private static final String PARAMETERIZED_NAME = "{index}: serverSslProvider = {0}, clientSslProvider = {1}";
    static Collection<Object[]> parameters() {
<<<<<<< HEAD
        List<SslProvider> providers = new ArrayList<>(Arrays.asList(SslProvider.values()));
=======
        List<SslProvider> providers = new ArrayList<SslProvider>(Arrays.asList(SslProvider.values()));
>>>>>>> dev
        if (!OpenSsl.isAvailable()) {
            providers.remove(SslProvider.OPENSSL);
            providers.remove(SslProvider.OPENSSL_REFCNT);
        }

        List<Object[]> params = new ArrayList<>();
        for (SslProvider sp: providers) {
            for (SslProvider cp: providers) {
                params.add(new Object[] { sp, cp });
            }
        }
        return params;
    }

    @ParameterizedTest(name = PARAMETERIZED_NAME)
    @Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
    @MethodSource("parameters")
    public void testSniSNIMatcherMatchesClient(SslProvider serverProvider, SslProvider clientProvider)
<<<<<<< HEAD
            throws Throwable {
=======
            throws Exception {
>>>>>>> dev
        assumeTrue(PlatformDependent.javaVersion() >= 8);
        SniClientJava8TestUtil.testSniClient(serverProvider, clientProvider, true);
    }

    @ParameterizedTest(name = PARAMETERIZED_NAME)
    @Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
    @MethodSource("parameters")
    public void testSniSNIMatcherDoesNotMatchClient(
            final SslProvider serverProvider, final SslProvider clientProvider) {
        assumeTrue(PlatformDependent.javaVersion() >= 8);
        assertThrows(SSLException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                SniClientJava8TestUtil.testSniClient(serverProvider, clientProvider, false);
            }
        });
    }

    @ParameterizedTest(name = PARAMETERIZED_NAME)
    @Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
    @MethodSource("parameters")
    public void testSniClient(SslProvider sslServerProvider, SslProvider sslClientProvider) throws Exception {
        String sniHostName = "sni.netty.io";
        LocalAddress address = new LocalAddress("test");
        EventLoopGroup group = new MultithreadEventLoopGroup(1, LocalHandler.newFactory());
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext sslServerContext = null;
        SslContext sslClientContext = null;

        Channel sc = null;
        Channel cc = null;
        try {
            if ((sslServerProvider == SslProvider.OPENSSL || sslServerProvider == SslProvider.OPENSSL_REFCNT)
                && !OpenSsl.supportsKeyManagerFactory()) {
                sslServerContext = SslContextBuilder.forServer(cert.certificate(), cert.privateKey())
                                                    .sslProvider(sslServerProvider)
                                                    .build();
            } else {
                // The used OpenSSL version does support a KeyManagerFactory, so use it.
<<<<<<< HEAD
                KeyManagerFactory kmf = SniClientJava8TestUtil.newSniX509KeyManagerFactory(cert, sniHostName);
                sslServerContext = SslContextBuilder.forServer(kmf)
=======
                KeyManagerFactory kmf = PlatformDependent.javaVersion() >= 8 ?
                        SniClientJava8TestUtil.newSniX509KeyManagerFactory(cert, sniHostName) :
                        SslContext.buildKeyManagerFactory(
                                new X509Certificate[] { cert.cert() }, null,
                                cert.key(), null, null, null);

               sslServerContext = SslContextBuilder.forServer(kmf)
>>>>>>> dev
                                                   .sslProvider(sslServerProvider)
                                                   .build();
            }

            final SslContext finalContext = sslServerContext;
            final Promise<String> promise = group.next().newPromise();
            ServerBootstrap sb = new ServerBootstrap();
            sc = sb.group(group).channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addFirst(new SniHandler(input -> {
                        promise.setSuccess(input);
                        return finalContext;
                    }));
                }
            }).bind(address).syncUninterruptibly().channel();

            TrustManagerFactory tmf = SniClientJava8TestUtil.newSniX509TrustmanagerFactory(sniHostName);
            sslClientContext = SslContextBuilder.forClient().trustManager(tmf)
                                                     .sslProvider(sslClientProvider).build();
            Bootstrap cb = new Bootstrap();

            SslHandler handler = new SslHandler(
                    sslClientContext.newEngine(ByteBufAllocator.DEFAULT, sniHostName, -1));
            cc = cb.group(group).channel(LocalChannel.class).handler(handler)
                    .connect(address).syncUninterruptibly().channel();
            assertEquals(sniHostName, promise.syncUninterruptibly().getNow());

            // After we are done with handshaking getHandshakeSession() should return null.
            handler.handshakeFuture().syncUninterruptibly();
            assertNull(handler.engine().getHandshakeSession());

            SniClientJava8TestUtil.assertSSLSession(
                    handler.engine().getUseClientMode(), handler.engine().getSession(), sniHostName);
        } finally {
            if (cc != null) {
                cc.close().syncUninterruptibly();
            }
            if (sc != null) {
                sc.close().syncUninterruptibly();
            }
            ReferenceCountUtil.release(sslServerContext);
            ReferenceCountUtil.release(sslClientContext);

            cert.delete();

            group.shutdownGracefully();
        }
    }
}
