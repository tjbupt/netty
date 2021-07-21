/*
 * Copyright 2017 The Netty Project
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

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD
=======
import org.junit.jupiter.api.function.Executable;
>>>>>>> dev

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultChannelPromiseTest {

    @Test
    public void testNullChannel() {
<<<<<<< HEAD
        assertThrows(NullPointerException.class, () -> new DefaultChannelPromise(null));
=======
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                new DefaultChannelPromise(null);
            }
        });
>>>>>>> dev
    }

    @Test
    public void testChannelWithNullExecutor() {
<<<<<<< HEAD
        assertThrows(NullPointerException.class, () -> new DefaultChannelPromise(new EmbeddedChannel(), null));
=======
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                new DefaultChannelPromise(new EmbeddedChannel(), null);
            }
        });
>>>>>>> dev
    }

    @Test
    public void testNullChannelWithExecutor() {
<<<<<<< HEAD
        assertThrows(NullPointerException.class,
            () -> new DefaultChannelPromise(null, ImmediateEventExecutor.INSTANCE));
=======
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                new DefaultChannelPromise(null, ImmediateEventExecutor.INSTANCE);
            }
        });
>>>>>>> dev
    }
}
