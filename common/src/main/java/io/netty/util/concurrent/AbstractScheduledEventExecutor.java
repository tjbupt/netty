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

import static java.util.Objects.requireNonNull;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;

import static io.netty.util.concurrent.ScheduledFutureTask.deadlineNanos;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
<<<<<<< HEAD
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
=======
>>>>>>> dev
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract base class for {@link EventExecutor}s that want to support scheduling.
 */
public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {
<<<<<<< HEAD
    static final long START_TIME = System.nanoTime();

    private static final Comparator<RunnableScheduledFutureNode<?>> SCHEDULED_FUTURE_TASK_COMPARATOR =
            Comparable::compareTo;

    private PriorityQueue<RunnableScheduledFutureNode<?>> scheduledTaskQueue;
=======
    private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR =
            new Comparator<ScheduledFutureTask<?>>() {
                @Override
                public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
                    return o1.compareTo(o2);
                }
            };

   static final Runnable WAKEUP_TASK = new Runnable() {
       @Override
       public void run() { } // Do nothing
    };

    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
>>>>>>> dev

    long nextTaskId;

    protected AbstractScheduledEventExecutor() {
    }

    /**
     * The time elapsed since initialization of this class in nanoseconds. This may return a negative number just like
     * {@link System#nanoTime()}.
     */
    public static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    /**
     * The deadline (in nanoseconds) for a given delay (in nanoseconds).
     */
    static long deadlineNanos(long delay) {
        long deadlineNanos = nanoTime() + delay;
        // Guard against overflow
        return deadlineNanos < 0 ? Long.MAX_VALUE : deadlineNanos;
    }

<<<<<<< HEAD
    PriorityQueue<RunnableScheduledFutureNode<?>> scheduledTaskQueue() {
=======
    /**
     * Given an arbitrary deadline {@code deadlineNanos}, calculate the number of nano seconds from now
     * {@code deadlineNanos} would expire.
     * @param deadlineNanos An arbitrary deadline in nano seconds.
     * @return the number of nano seconds from now {@code deadlineNanos} would expire.
     */
    protected static long deadlineToDelayNanos(long deadlineNanos) {
        return ScheduledFutureTask.deadlineToDelayNanos(deadlineNanos);
    }

    /**
     * The initial value used for delay and computations based upon a monatomic time source.
     * @return initial value used for delay and computations based upon a monatomic time source.
     */
    protected static long initialNanoTime() {
        return ScheduledFutureTask.initialNanoTime();
    }

    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
>>>>>>> dev
        if (scheduledTaskQueue == null) {
            scheduledTaskQueue = new DefaultPriorityQueue<>(
                    SCHEDULED_FUTURE_TASK_COMPARATOR,
                    // Use same initial capacity as java.util.PriorityQueue
                    11);
        }
        return scheduledTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<RunnableScheduledFutureNode<?>> queue) {
        return queue == null || queue.isEmpty();
    }

    /**
     * Cancel all scheduled tasks.
     *
     * This method MUST be called only when {@link #inEventLoop()} is {@code true}.
     */
    protected final void cancelScheduledTasks() {
        assert inEventLoop();
        PriorityQueue<RunnableScheduledFutureNode<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (isNullOrEmpty(scheduledTaskQueue)) {
            return;
        }

        final RunnableScheduledFutureNode<?>[] scheduledTasks =
                scheduledTaskQueue.toArray(new RunnableScheduledFutureNode<?>[0]);

        for (RunnableScheduledFutureNode<?> task: scheduledTasks) {
            task.cancel(false);
        }

        scheduledTaskQueue.clearIgnoringIndexes();
    }

    /**
     * @see #pollScheduledTask(long)
     */
    protected final RunnableScheduledFuture<?> pollScheduledTask() {
        return pollScheduledTask(nanoTime());
    }

    /**
     * Return the {@link Runnable} which is ready to be executed with the given {@code nanoTime}.
     * You should use {@link #nanoTime()} to retrieve the correct {@code nanoTime}.
     *
     * This method MUST be called only when {@link #inEventLoop()} is {@code true}.
     */
    protected final RunnableScheduledFuture<?> pollScheduledTask(long nanoTime) {
        assert inEventLoop();

<<<<<<< HEAD
        Queue<RunnableScheduledFutureNode<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        RunnableScheduledFutureNode<?> scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
        if (scheduledTask == null) {
=======
        ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
        if (scheduledTask == null || scheduledTask.deadlineNanos() - nanoTime > 0) {
>>>>>>> dev
            return null;
        }
        scheduledTaskQueue.remove();
        scheduledTask.setConsumed();
        return scheduledTask;
    }

    /**
<<<<<<< HEAD
     * Return the nanoseconds when the next scheduled task is ready to be run or {@code -1} if no task is scheduled.
     *
     * This method MUST be called only when {@link #inEventLoop()} is {@code true}.
     */
    protected final long nextScheduledTaskNano() {
        Queue<RunnableScheduledFutureNode<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        RunnableScheduledFutureNode<?> scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
        if (scheduledTask == null) {
            return -1;
        }
        return Math.max(0, scheduledTask.deadlineNanos() - nanoTime());
    }

    final RunnableScheduledFuture<?> peekScheduledTask() {
        Queue<RunnableScheduledFutureNode<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (scheduledTaskQueue == null) {
            return null;
        }
        RunnableScheduledFutureNode<?> node = scheduledTaskQueue.peek();
        if (node == null) {
            return null;
        }
        return node;
=======
     * Return the nanoseconds until the next scheduled task is ready to be run or {@code -1} if no task is scheduled.
     */
    protected final long nextScheduledTaskNano() {
        ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
        return scheduledTask != null ? scheduledTask.delayNanos() : -1;
    }

    /**
     * Return the deadline (in nanoseconds) when the next scheduled task is ready to be run or {@code -1}
     * if no task is scheduled.
     */
    protected final long nextScheduledTaskDeadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
        return scheduledTask != null ? scheduledTask.deadlineNanos() : -1;
    }

    final ScheduledFutureTask<?> peekScheduledTask() {
        Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        return scheduledTaskQueue != null ? scheduledTaskQueue.peek() : null;
>>>>>>> dev
    }

    /**
     * Returns {@code true} if a scheduled task is ready for processing.
     *
     * This method MUST be called only when {@link #inEventLoop()} is {@code true}.
     */
    protected final boolean hasScheduledTasks() {
<<<<<<< HEAD
        assert inEventLoop();
        Queue<RunnableScheduledFutureNode<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        RunnableScheduledFutureNode<?> scheduledTask = scheduledTaskQueue == null ? null : scheduledTaskQueue.peek();
=======
        ScheduledFutureTask<?> scheduledTask = peekScheduledTask();
>>>>>>> dev
        return scheduledTask != null && scheduledTask.deadlineNanos() <= nanoTime();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        requireNonNull(command, "command");
        requireNonNull(unit, "unit");
        if (delay < 0) {
            delay = 0;
        }
<<<<<<< HEAD
        RunnableScheduledFuture<?> task = newScheduledTaskFor(Executors.callable(command),
                deadlineNanos(unit.toNanos(delay)), 0);
        return schedule(task);
=======
        validateScheduled0(delay, unit);

        return schedule(new ScheduledFutureTask<Void>(
                this,
                command,
                deadlineNanos(unit.toNanos(delay))));
>>>>>>> dev
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        requireNonNull(callable, "callable");
        requireNonNull(unit, "unit");
        if (delay < 0) {
            delay = 0;
        }
<<<<<<< HEAD
        RunnableScheduledFuture<V> task = newScheduledTaskFor(callable, deadlineNanos(unit.toNanos(delay)), 0);
        return schedule(task);
=======
        validateScheduled0(delay, unit);

        return schedule(new ScheduledFutureTask<V>(this, callable, deadlineNanos(unit.toNanos(delay))));
>>>>>>> dev
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        requireNonNull(command, "command");
        requireNonNull(unit, "unit");
        if (initialDelay < 0) {
            throw new IllegalArgumentException(
                    String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (period <= 0) {
            throw new IllegalArgumentException(
                    String.format("period: %d (expected: > 0)", period));
        }

<<<<<<< HEAD
        RunnableScheduledFuture<?> task = newScheduledTaskFor(Executors.<Void>callable(command, null),
                deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period));
        return schedule(task);
=======
        return schedule(new ScheduledFutureTask<Void>(
                this, command, deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
>>>>>>> dev
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        requireNonNull(command, "command");
        requireNonNull(unit, "unit");
        if (initialDelay < 0) {
            throw new IllegalArgumentException(
                    String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0) {
            throw new IllegalArgumentException(
                    String.format("delay: %d (expected: > 0)", delay));
        }

<<<<<<< HEAD
        RunnableScheduledFuture<?> task = newScheduledTaskFor(Executors.<Void>callable(command, null),
                deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay));
        return schedule(task);
=======
        validateScheduled0(initialDelay, unit);
        validateScheduled0(delay, unit);

        return schedule(new ScheduledFutureTask<Void>(
                this, command, deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }

    @SuppressWarnings("deprecation")
    private void validateScheduled0(long amount, TimeUnit unit) {
        validateScheduled(amount, unit);
>>>>>>> dev
    }

    /**
     * Add the {@link RunnableScheduledFuture} for execution.
     */
<<<<<<< HEAD
    protected final <V> ScheduledFuture<V> schedule(final RunnableScheduledFuture<V> task) {
        if (inEventLoop()) {
            add0(task);
        } else {
            execute(() -> add0(task));
=======
    @Deprecated
    protected void validateScheduled(long amount, TimeUnit unit) {
        // NOOP
    }

    final void scheduleFromEventLoop(final ScheduledFutureTask<?> task) {
        // nextTaskId a long and so there is no chance it will overflow back to 0
        scheduledTaskQueue().add(task.setId(++nextTaskId));
    }

    private <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task) {
        if (inEventLoop()) {
            scheduleFromEventLoop(task);
        } else {
            final long deadlineNanos = task.deadlineNanos();
            // task will add itself to scheduled task queue when run if not expired
            if (beforeScheduledTaskSubmitted(deadlineNanos)) {
                execute(task);
            } else {
                lazyExecute(task);
                // Second hook after scheduling to facilitate race-avoidance
                if (afterScheduledTaskSubmitted(deadlineNanos)) {
                    execute(WAKEUP_TASK);
                }
            }
>>>>>>> dev
        }
        return task;
    }

<<<<<<< HEAD
    private <V> void add0(RunnableScheduledFuture<V> task) {
        final RunnableScheduledFutureNode node;
        if (task instanceof RunnableScheduledFutureNode) {
            node = (RunnableScheduledFutureNode) task;
        } else {
            node = new DefaultRunnableScheduledFutureNode<V>(task);
        }
        scheduledTaskQueue().add(node);
    }

    final void removeScheduled(final RunnableScheduledFutureNode<?> task) {
        if (inEventLoop()) {
            scheduledTaskQueue().removeTyped(task);
        } else {
            execute(() -> removeScheduled(task));
        }
    }

    /**
     * Returns a new {@link RunnableFuture} build on top of the given {@link Promise} and {@link Callable}.
     *
     * This can be used if you want to override {@link #newTaskFor(Callable)} and return a different
     * {@link RunnableFuture}.
     */
    protected static <V> RunnableScheduledFuture<V> newRunnableScheduledFuture(
            AbstractScheduledEventExecutor executor, Promise<V> promise, Callable<V> task,
            long deadlineNanos, long periodNanos) {
        return new RunnableScheduledFutureAdapter<V>(executor, promise, task, deadlineNanos, periodNanos);
    }

    /**
     * Returns a {@code RunnableScheduledFuture} for the given values.
     */
    protected <V> RunnableScheduledFuture<V> newScheduledTaskFor(
            Callable<V> callable, long deadlineNanos, long period) {
        return newRunnableScheduledFuture(this, this.newPromise(), callable, deadlineNanos, period);
    }

    interface RunnableScheduledFutureNode<V> extends PriorityQueueNode, RunnableScheduledFuture<V> { }

    private static final class DefaultRunnableScheduledFutureNode<V> implements RunnableScheduledFutureNode<V> {
        private final RunnableScheduledFuture<V> future;
        private int queueIndex = INDEX_NOT_IN_QUEUE;

        DefaultRunnableScheduledFutureNode(RunnableScheduledFuture<V> future) {
            this.future = future;
        }

        @Override
        public EventExecutor executor() {
            return future.executor();
        }

        @Override
        public long deadlineNanos() {
            return future.deadlineNanos();
        }

        @Override
        public long delayNanos() {
            return future.delayNanos();
        }

        @Override
        public long delayNanos(long currentTimeNanos) {
            return future.delayNanos(currentTimeNanos);
        }

        @Override
        public RunnableScheduledFuture<V> addListener(
                GenericFutureListener<? extends Future<? super V>> listener) {
            future.addListener(listener);
            return this;
        }

        @Override
        public boolean isPeriodic() {
            return future.isPeriodic();
        }

        @Override
        public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
            return queueIndex;
        }

        @Override
        public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i) {
            queueIndex = i;
        }

        @Override
        public void run() {
            future.run();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return future.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return future.get(timeout, unit);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return future.getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return future.compareTo(o);
        }

        @Override
        public RunnableFuture<V> sync() throws InterruptedException {
            future.sync();
            return this;
        }

        @Override
        public RunnableFuture<V> syncUninterruptibly() {
            future.syncUninterruptibly();
            return this;
        }

        @Override
        public RunnableFuture<V> await() throws InterruptedException {
            future.await();
            return this;
        }

        @Override
        public RunnableFuture<V> awaitUninterruptibly() {
            future.awaitUninterruptibly();
            return this;
        }

        @Override
        public boolean isSuccess() {
            return future.isSuccess();
        }

        @Override
        public boolean isCancellable() {
            return future.isCancellable();
        }

        @Override
        public Throwable cause() {
            return future.cause();
        }

        @Override
        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return future.await(timeout, unit);
        }

        @Override
        public boolean await(long timeoutMillis) throws InterruptedException {
            return future.await(timeoutMillis);
        }

        @Override
        public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
            return future.awaitUninterruptibly(timeout, unit);
        }

        @Override
        public boolean awaitUninterruptibly(long timeoutMillis) {
            return future.awaitUninterruptibly(timeoutMillis);
        }

        @Override
        public V getNow() {
            return future.getNow();
=======
    final void removeScheduled(final ScheduledFutureTask<?> task) {
        assert task.isCancelled();
        if (inEventLoop()) {
            scheduledTaskQueue().removeTyped(task);
        } else {
            // task will remove itself from scheduled task queue when it runs
            lazyExecute(task);
>>>>>>> dev
        }
    }

    /**
     * Called from arbitrary non-{@link EventExecutor} threads prior to scheduled task submission.
     * Returns {@code true} if the {@link EventExecutor} thread should be woken immediately to
     * process the scheduled task (if not already awake).
     * <p>
     * If {@code false} is returned, {@link #afterScheduledTaskSubmitted(long)} will be called with
     * the same value <i>after</i> the scheduled task is enqueued, providing another opportunity
     * to wake the {@link EventExecutor} thread if required.
     *
     * @param deadlineNanos deadline of the to-be-scheduled task
     *     relative to {@link AbstractScheduledEventExecutor#nanoTime()}
     * @return {@code true} if the {@link EventExecutor} thread should be woken, {@code false} otherwise
     */
    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }

    /**
     * See {@link #beforeScheduledTaskSubmitted(long)}. Called only after that method returns false.
     *
     * @param deadlineNanos relative to {@link AbstractScheduledEventExecutor#nanoTime()}
     * @return  {@code true} if the {@link EventExecutor} thread should be woken, {@code false} otherwise
     */
    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }
}
