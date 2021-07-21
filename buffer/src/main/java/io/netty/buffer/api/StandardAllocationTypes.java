/*
 * Copyright 2021 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
<<<<<<< HEAD:buffer/src/main/java/io/netty/buffer/api/StandardAllocationTypes.java
 *   https://www.apache.org/licenses/LICENSE-2.0
=======
 * https://www.apache.org/licenses/LICENSE-2.0
>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/NioUdtByteAcceptorChannelTest.java
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer.api;

<<<<<<< HEAD:buffer/src/main/java/io/netty/buffer/api/StandardAllocationTypes.java
/**
 * Standard implementations of {@link AllocationType} that all {@linkplain MemoryManager memory managers} should
 * support.
 */
public enum StandardAllocationTypes implements AllocationType {
    /**
     * The allocation should use Java heap memory.
     */
    ON_HEAP,
=======
package io.netty.test.udt.nio;

import io.netty.channel.udt.nio.NioUdtByteAcceptorChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class NioUdtByteAcceptorChannelTest extends AbstractUdtTest {

>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/NioUdtByteAcceptorChannelTest.java
    /**
     * The allocation should use native (non-heap) memory.
     */
    OFF_HEAP
}
