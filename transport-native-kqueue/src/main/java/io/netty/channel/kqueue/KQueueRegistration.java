/*
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
<<<<<<< HEAD:transport-native-kqueue/src/main/java/io/netty/channel/kqueue/KQueueRegistration.java
 *   https://www.apache.org/licenses/LICENSE-2.0
=======
 * https://www.apache.org/licenses/LICENSE-2.0
>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/AbstractUdtTest.java
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.kqueue;

import io.netty.channel.unix.IovArray;

<<<<<<< HEAD:transport-native-kqueue/src/main/java/io/netty/channel/kqueue/KQueueRegistration.java
=======
import io.netty.test.udt.util.UnitHelp;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/AbstractUdtTest.java

/**
 * Registration with an {@link KQueueEventLoop}.
 */
interface KQueueRegistration {

    /**
     * Update the event-set for the registration.
     */
<<<<<<< HEAD:transport-native-kqueue/src/main/java/io/netty/channel/kqueue/KQueueRegistration.java
    void evSet(short filter, short flags, int fflags);
=======
    @BeforeAll
    public static void assumeConditions() {
        assumeTrue(UnitHelp.canLoadAndInitClass("com.barchart.udt.SocketUDT"));
    }
>>>>>>> dev:transport-udt/src/test/java/io/netty/test/udt/nio/AbstractUdtTest.java

    /**
     * Returns an {@link IovArray} that can be used for {@code writev}.
     */
    IovArray cleanArray();
}
