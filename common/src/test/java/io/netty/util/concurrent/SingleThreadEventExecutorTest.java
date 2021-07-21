/*
 * Copyright 2015 The Netty Project
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
package io.netty.util.concurrent;

import org.junit.jupiter.api.Test;
<<<<<<< HEAD
=======

import io.netty.util.concurrent.AbstractEventExecutor.LazyRunnable;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
>>>>>>> dev

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
<<<<<<< HEAD
import java.util.concurrent.CompletionException;
=======
import java.util.concurrent.CountDownLatch;
>>>>>>> dev
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

<<<<<<< HEAD
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
=======
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
>>>>>>> dev

public class SingleThreadEventExecutorTest {
    public static final Runnable DUMMY_TASK = () -> {
    };

    @Test
    public void testWrappedExecutorIsShutdown() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

<<<<<<< HEAD
        SingleThreadEventExecutor executor = new SingleThreadEventExecutor(executorService) {
=======
       final SingleThreadEventExecutor executor =
               new SingleThreadEventExecutor(null, executorService, false) {
>>>>>>> dev
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };

        executorService.shutdownNow();
        executeShouldFail(executor);
        executeShouldFail(executor);
<<<<<<< HEAD
        var exception = assertThrows(
                CompletionException.class, () -> executor.shutdownGracefully().syncUninterruptibly());
        assertThat(exception).hasCauseInstanceOf(RejectedExecutionException.class);
        assertTrue(executor.isShutdown());
    }

    private static void executeShouldFail(Executor executor) {
        assertThrows(RejectedExecutionException.class, () -> executor.execute(DUMMY_TASK));
=======
        assertThrows(RejectedExecutionException.class, new Executable() {
            @Override
            public void execute() {
                executor.shutdownGracefully().syncUninterruptibly();
            }
        });
        assertTrue(executor.isShutdown());
    }

    private static void executeShouldFail(final Executor executor) {
        assertThrows(RejectedExecutionException.class, new Executable() {
            @Override
            public void execute() {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Noop.
                    }
                });
            }
        });
>>>>>>> dev
    }

    @Test
    public void testThreadProperties() {
        final AtomicReference<Thread> threadRef = new AtomicReference<Thread>();
        SingleThreadEventExecutor executor = new SingleThreadEventExecutor(new DefaultThreadFactory("test")) {
            @Override
            protected void run() {
                threadRef.set(Thread.currentThread());
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };
        ThreadProperties threadProperties = executor.threadProperties();

        Thread thread = threadRef.get();
        assertEquals(thread.getId(), threadProperties.id());
        assertEquals(thread.getName(), threadProperties.name());
        assertEquals(thread.getPriority(), threadProperties.priority());
        assertEquals(thread.isAlive(), threadProperties.isAlive());
        assertEquals(thread.isDaemon(), threadProperties.isDaemon());
        assertTrue(threadProperties.stackTrace().length > 0);
        executor.shutdownGracefully();
    }

    @Test
<<<<<<< HEAD
    public void testInvokeAnyInEventLoop() throws Throwable {
        assertTimeoutPreemptively(ofSeconds(3), () -> {
            var exception = assertThrows(CompletionException.class, () -> testInvokeInEventLoop(true, false));
            assertThat(exception).hasCauseInstanceOf(RejectedExecutionException.class);
        });
    }

    @Test
    public void testInvokeAnyInEventLoopWithTimeout() throws Throwable {
        assertTimeoutPreemptively(ofSeconds(3), () -> {
            var exception = assertThrows(CompletionException.class, () -> testInvokeInEventLoop(true, true));
            assertThat(exception).hasCauseInstanceOf(RejectedExecutionException.class);
        });
    }

    @Test
    public void testInvokeAllInEventLoop() throws Throwable {
        assertTimeoutPreemptively(ofSeconds(3), () -> {
            var exception = assertThrows(CompletionException.class, () -> testInvokeInEventLoop(false, false));
            assertThat(exception).hasCauseInstanceOf(RejectedExecutionException.class);
        });
    }

    @Test
    public void testInvokeAllInEventLoopWithTimeout() throws Throwable {
        assertTimeoutPreemptively(ofSeconds(3), () -> {
            var exception = assertThrows(CompletionException.class, () -> testInvokeInEventLoop(false, true));
            assertThat(exception).hasCauseInstanceOf(RejectedExecutionException.class);
        });
    }

    private static void testInvokeInEventLoop(final boolean any, final boolean timeout) {
        final SingleThreadEventExecutor executor = new SingleThreadEventExecutor(Executors.defaultThreadFactory()) {
=======
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testInvokeAnyInEventLoop() {
        testInvokeInEventLoop(true, false);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testInvokeAnyInEventLoopWithTimeout() {
        testInvokeInEventLoop(true, true);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testInvokeAllInEventLoop() {
        testInvokeInEventLoop(false, false);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testInvokeAllInEventLoopWithTimeout() {
        testInvokeInEventLoop(false, true);
    }

    private static void testInvokeInEventLoop(final boolean any, final boolean timeout) {
        final SingleThreadEventExecutor executor = new SingleThreadEventExecutor(null,
                Executors.defaultThreadFactory(), true) {
>>>>>>> dev
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };
        try {
<<<<<<< HEAD
            final Promise<Void> promise = executor.newPromise();
            executor.execute(() -> {
                try {
                    Set<Callable<Boolean>> set = Collections.singleton(() -> {
                        promise.setFailure(new AssertionError("Should never execute the Callable"));
                        return Boolean.TRUE;
                    });
                    if (any) {
                        if (timeout) {
                            executor.invokeAny(set, 10, TimeUnit.SECONDS);
                        } else {
                            executor.invokeAny(set);
                        }
                    } else {
                        if (timeout) {
                            executor.invokeAll(set, 10, TimeUnit.SECONDS);
                        } else {
                            executor.invokeAll(set);
                        }
                    }
                    promise.setFailure(new AssertionError("Should never reach here"));
                } catch (Throwable cause) {
                    promise.setFailure(cause);
=======
            assertThrows(RejectedExecutionException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    final Promise<Void> promise = executor.newPromise();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Set<Callable<Boolean>> set = Collections.<Callable<Boolean>>singleton(
                                        new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        promise.setFailure(new AssertionError("Should never execute the Callable"));
                                        return Boolean.TRUE;
                                    }
                                });
                                if (any) {
                                    if (timeout) {
                                        executor.invokeAny(set, 10, TimeUnit.SECONDS);
                                    } else {
                                        executor.invokeAny(set);
                                    }
                                } else {
                                    if (timeout) {
                                        executor.invokeAll(set, 10, TimeUnit.SECONDS);
                                    } else {
                                        executor.invokeAll(set);
                                    }
                                }
                                promise.setFailure(new AssertionError("Should never reach here"));
                            } catch (Throwable cause) {
                                promise.setFailure(cause);
                            }
                        }
                    });
                    promise.syncUninterruptibly();
>>>>>>> dev
                }
            });
        } finally {
            executor.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
        }
    }

<<<<<<< HEAD
=======
    static class LatchTask extends CountDownLatch implements Runnable {
        LatchTask() {
            super(1);
        }

        @Override
        public void run() {
            countDown();
        }
    }

    static class LazyLatchTask extends LatchTask implements LazyRunnable { }

    @Test
    public void testLazyExecution() throws Exception {
        final SingleThreadEventExecutor executor = new SingleThreadEventExecutor(null,
                Executors.defaultThreadFactory(), false) {
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    try {
                        synchronized (this) {
                            if (!hasTasks()) {
                                wait();
                            }
                        }
                        runAllTasks();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail(e.toString());
                    }
                }
            }

            @Override
            protected void wakeup(boolean inEventLoop) {
                if (!inEventLoop) {
                    synchronized (this) {
                        notifyAll();
                    }
                }
            }
        };

        // Ensure event loop is started
        LatchTask latch0 = new LatchTask();
        executor.execute(latch0);
        assertTrue(latch0.await(100, TimeUnit.MILLISECONDS));
        // Pause to ensure it enters waiting state
        Thread.sleep(100L);

        // Submit task via lazyExecute
        LatchTask latch1 = new LatchTask();
        executor.lazyExecute(latch1);
        // Sumbit lazy task via regular execute
        LatchTask latch2 = new LazyLatchTask();
        executor.execute(latch2);

        // Neither should run yet
        assertFalse(latch1.await(100, TimeUnit.MILLISECONDS));
        assertFalse(latch2.await(100, TimeUnit.MILLISECONDS));

        // Submit regular task via regular execute
        LatchTask latch3 = new LatchTask();
        executor.execute(latch3);

        // Should flush latch1 and latch2 and then run latch3 immediately
        assertTrue(latch3.await(100, TimeUnit.MILLISECONDS));
        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
    }

>>>>>>> dev
    @Test
    public void testTaskAddedAfterShutdownNotAbandoned() throws Exception {

        // A queue that doesn't support remove, so tasks once added cannot be rejected anymore
        LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>() {
            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }
        };

<<<<<<< HEAD
=======
        final Runnable dummyTask = new Runnable() {
            @Override
            public void run() {
            }
        };

>>>>>>> dev
        final LinkedBlockingQueue<Future<?>> submittedTasks = new LinkedBlockingQueue<Future<?>>();
        final AtomicInteger attempts = new AtomicInteger();
        final AtomicInteger rejects = new AtomicInteger();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
<<<<<<< HEAD
        final SingleThreadEventExecutor executor = new SingleThreadEventExecutor(executorService, Integer.MAX_VALUE,
                RejectedExecutionHandlers.reject()) {

            @Override
            protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
                return taskQueue;
            }

=======
        final SingleThreadEventExecutor executor = new SingleThreadEventExecutor(null, executorService, false,
                taskQueue, RejectedExecutionHandlers.reject()) {
>>>>>>> dev
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }

            @Override
<<<<<<< HEAD
            protected boolean confirmShutdown0() {
                boolean result = super.confirmShutdown0();
=======
            protected boolean confirmShutdown() {
                boolean result = super.confirmShutdown();
>>>>>>> dev
                // After shutdown is confirmed, scheduled one more task and record it
                if (result) {
                    attempts.incrementAndGet();
                    try {
<<<<<<< HEAD
                        submittedTasks.add(submit(DUMMY_TASK));
=======
                        submittedTasks.add(submit(dummyTask));
>>>>>>> dev
                    } catch (RejectedExecutionException e) {
                        // ignore, tasks are either accepted or rejected
                        rejects.incrementAndGet();
                    }
                }
                return result;
            }
        };

        // Start the loop
<<<<<<< HEAD
        executor.submit(DUMMY_TASK).sync();
=======
        executor.submit(dummyTask).sync();
>>>>>>> dev

        // Shutdown without any quiet period
        executor.shutdownGracefully(0, 100, TimeUnit.MILLISECONDS).sync();

        // Ensure there are no user-tasks left.
        assertEquals(0, executor.drainTasks());

        // Verify that queue is empty and all attempts either succeeded or were rejected
        assertTrue(taskQueue.isEmpty());
        assertTrue(attempts.get() > 0);
        assertEquals(attempts.get(), submittedTasks.size() + rejects.get());
        for (Future<?> f : submittedTasks) {
            assertTrue(f.isSuccess());
        }
    }

    @Test
<<<<<<< HEAD
    public void testTakeTask() throws Exception {
        final SingleThreadEventExecutor executor =
                new SingleThreadEventExecutor(Executors.defaultThreadFactory()) {
=======
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
    public void testTakeTask() throws Exception {
        final SingleThreadEventExecutor executor =
                new SingleThreadEventExecutor(null, Executors.defaultThreadFactory(), true) {
>>>>>>> dev
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };

<<<<<<< HEAD
        assertTimeoutPreemptively(ofSeconds(5), () -> {
            //add task
            TestRunnable beforeTask = new TestRunnable();
            executor.execute(beforeTask);

            //add scheduled task
            TestRunnable scheduledTask = new TestRunnable();
            ScheduledFuture<?> f = executor.schedule(scheduledTask , 1500, TimeUnit.MILLISECONDS);

            //add task
            TestRunnable afterTask = new TestRunnable();
            executor.execute(afterTask);

            f.sync();

            assertThat(beforeTask.ran.get()).isTrue();
            assertThat(scheduledTask.ran.get()).isTrue();
            assertThat(afterTask.ran.get()).isTrue();
        });
    }

    @Test
=======
        //add task
        TestRunnable beforeTask = new TestRunnable();
        executor.execute(beforeTask);

        //add scheduled task
        TestRunnable scheduledTask = new TestRunnable();
        ScheduledFuture<?> f = executor.schedule(scheduledTask , 1500, TimeUnit.MILLISECONDS);

        //add task
        TestRunnable afterTask = new TestRunnable();
        executor.execute(afterTask);

        f.sync();

        assertThat(beforeTask.ran.get(), is(true));
        assertThat(scheduledTask.ran.get(), is(true));
        assertThat(afterTask.ran.get(), is(true));
    }

    @Test
    @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
>>>>>>> dev
    public void testTakeTaskAlwaysHasTask() throws Exception {
        //for https://github.com/netty/netty/issues/1614

        final SingleThreadEventExecutor executor =
<<<<<<< HEAD
                new SingleThreadEventExecutor(Executors.defaultThreadFactory()) {
=======
                new SingleThreadEventExecutor(null, Executors.defaultThreadFactory(), true) {
>>>>>>> dev
            @Override
            protected void run() {
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };

<<<<<<< HEAD
        assertTimeoutPreemptively(ofSeconds(5), () -> {
            //add scheduled task
            TestRunnable t = new TestRunnable();
            final ScheduledFuture<?> f = executor.schedule(t, 1500, TimeUnit.MILLISECONDS);

            //ensure always has at least one task in taskQueue
            //check if scheduled tasks are triggered
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!f.isDone()) {
                        executor.execute(this);
                    }
                }
            });

            f.sync();

            assertThat(t.ran.get()).isTrue();
        });
=======
        //add scheduled task
        TestRunnable t = new TestRunnable();
        final ScheduledFuture<?> f = executor.schedule(t, 1500, TimeUnit.MILLISECONDS);

        //ensure always has at least one task in taskQueue
        //check if scheduled tasks are triggered
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!f.isDone()) {
                    executor.execute(this);
                }
            }
        });

        f.sync();

        assertThat(t.ran.get(), is(true));
>>>>>>> dev
    }

    private static final class TestRunnable implements Runnable {
        final AtomicBoolean ran = new AtomicBoolean();

        TestRunnable() {
        }

        @Override
        public void run() {
            ran.set(true);
        }
    }
}
