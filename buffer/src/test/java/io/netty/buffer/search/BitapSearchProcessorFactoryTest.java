/*
 * Copyright 2020 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
<<<<<<< HEAD
<<<<<<<< HEAD:buffer/src/test/java/io/netty/buffer/search/BitapSearchProcessorFactoryTest.java
 *   https://www.apache.org/licenses/LICENSE-2.0
========
 * https://www.apache.org/licenses/LICENSE-2.0
>>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/NioUdtByteConnectorChannelTest.java
=======
 *   https://www.apache.org/licenses/LICENSE-2.0
>>>>>>> dev
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer.search;

import org.junit.jupiter.api.Test;
<<<<<<< HEAD

<<<<<<<< HEAD:buffer/src/test/java/io/netty/buffer/search/BitapSearchProcessorFactoryTest.java
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitapSearchProcessorFactoryTest {
========
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
>>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/NioUdtByteConnectorChannelTest.java
=======
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitapSearchProcessorFactoryTest {
>>>>>>> dev

    @Test
    public void testAcceptMaximumLengthNeedle() {
        new BitapSearchProcessorFactory(new byte[64]);
    }

    @Test
    public void testRejectTooLongNeedle() {
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, () -> new BitapSearchProcessorFactory(new byte[65]));
=======
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                new BitapSearchProcessorFactory(new byte[65]);
            }
        });
>>>>>>> dev
    }

}
