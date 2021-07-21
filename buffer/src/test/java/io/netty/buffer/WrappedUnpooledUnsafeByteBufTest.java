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
package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD
=======
import org.junit.jupiter.api.function.Executable;
>>>>>>> dev

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WrappedUnpooledUnsafeByteBufTest extends BigEndianUnsafeDirectByteBufTest {

    @BeforeEach
    @Override
    public void init() {
        Assumptions.assumeTrue(PlatformDependent.useDirectBufferNoCleaner(),
                "PlatformDependent.useDirectBufferNoCleaner() returned false, skip tests");
        super.init();
    }

    @Override
    protected ByteBuf newBuffer(int length, int maxCapacity) {
        Assumptions.assumeTrue(maxCapacity == Integer.MAX_VALUE);

        return new WrappedUnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator.DEFAULT,
                PlatformDependent.allocateMemory(length), length, true);
    }

    @Test
    @Override
    public void testInternalNioBuffer() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testInternalNioBuffer);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testInternalNioBuffer();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testDuplicateReadGatheringByteChannelMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testDuplicateReadGatheringByteChannelMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testDuplicateReadGatheringByteChannelMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testSliceReadGatheringByteChannelMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testSliceReadGatheringByteChannelMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testSliceReadGatheringByteChannelMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testDuplicateReadOutputStreamMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testDuplicateReadOutputStreamMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testDuplicateReadOutputStreamMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testSliceReadOutputStreamMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testSliceReadOutputStreamMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testSliceReadOutputStreamMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testDuplicateBytesInArrayMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testDuplicateBytesInArrayMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testDuplicateBytesInArrayMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testSliceBytesInArrayMultipleThreads() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testSliceBytesInArrayMultipleThreads);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() throws Exception {
                WrappedUnpooledUnsafeByteBufTest.super.testSliceBytesInArrayMultipleThreads();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testNioBufferExposeOnlyRegion() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testNioBufferExposeOnlyRegion);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testNioBufferExposeOnlyRegion();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testGetReadOnlyDirectDst() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testGetReadOnlyDirectDst);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testGetReadOnlyDirectDst();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testGetReadOnlyHeapDst() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testGetReadOnlyHeapDst);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testGetReadOnlyHeapDst();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testReadBytes() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testReadBytes);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testReadBytes();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testDuplicateCapacityChange() {
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, super::testDuplicateCapacityChange);
=======
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testDuplicateCapacityChange();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testRetainedDuplicateCapacityChange() {
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, super::testRetainedDuplicateCapacityChange);
=======
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testRetainedDuplicateCapacityChange();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testLittleEndianWithExpand() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testLittleEndianWithExpand);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testLittleEndianWithExpand();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testWriteUsAsciiCharSequenceExpand() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testWriteUsAsciiCharSequenceExpand);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testWriteUsAsciiCharSequenceExpand();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testWriteUtf8CharSequenceExpand() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testWriteUtf8CharSequenceExpand);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testWriteUtf8CharSequenceExpand();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testWriteIso88591CharSequenceExpand() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testWriteIso88591CharSequenceExpand);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testWriteIso88591CharSequenceExpand();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testWriteUtf16CharSequenceExpand() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testWriteUtf16CharSequenceExpand);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testWriteUtf16CharSequenceExpand();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testGetBytesByteBuffer() {
<<<<<<< HEAD
        assertThrows(IndexOutOfBoundsException.class, super::testGetBytesByteBuffer);
=======
        assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                WrappedUnpooledUnsafeByteBufTest.super.testGetBytesByteBuffer();
            }
        });
>>>>>>> dev
    }

    @Test
    @Override
    public void testForEachByteDesc2() {
        // Ignore
    }

    @Test
    @Override
    public void testForEachByte2() {
        // Ignore
    }
}
