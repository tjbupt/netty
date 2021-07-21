/*
 * Copyright 2021 The Netty Project
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
package io.netty.buffer.api.pool;

/**
<<<<<<< HEAD:buffer/src/main/java/io/netty/buffer/api/pool/PoolChunkListMetric.java
 * Metrics for a list of chunks.
=======
 * The <a href="https://linux.die.net//man/7/epoll">epoll</a> mode to use.
>>>>>>> dev:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollMode.java
 */
public interface PoolChunkListMetric extends Iterable<PoolChunkMetric> {

    /**
<<<<<<< HEAD:buffer/src/main/java/io/netty/buffer/api/pool/PoolChunkListMetric.java
     * Return the minimum usage of the chunk list before which chunks are promoted to the previous list.
=======
     * Use {@code EPOLLET} (edge-triggered).
     *
     * @see <a href="https://linux.die.net//man/7/epoll">man 7 epoll</a>.
>>>>>>> dev:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollMode.java
     */
    int minUsage();

    /**
<<<<<<< HEAD:buffer/src/main/java/io/netty/buffer/api/pool/PoolChunkListMetric.java
     * Return the maximum usage of the chunk list after which chunks are promoted to the next list.
=======
     * Do not use {@code EPOLLET} (level-triggered).
     *
     * @see <a href="https://linux.die.net//man/7/epoll">man 7 epoll</a>.
>>>>>>> dev:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollMode.java
     */
    int maxUsage();
}
