package cn.wanther.toolkit.component.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可以用handler post出去并且可以取消的跑在主线程上的task
 */
public abstract class FutureRunnable implements RunnableFuture<Void> {

    private static final int STATUS_PEDDING = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_CACELLED = 2;
    private static final int STATUS_DONE = 3;

    private AtomicInteger mStatus = new AtomicInteger(STATUS_PEDDING);

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (mStatus.intValue() > STATUS_PEDDING) {
            throw new RuntimeException("cannot execute repeatedly");
        }

        mStatus.set(STATUS_RUNNING);
        doInFuture();
        mStatus.set(STATUS_DONE);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // do not interrupt main thread
        mStatus.set(STATUS_CACELLED);
        return true;
    }

    @Override
    public boolean isCancelled() {
        return mStatus.intValue() == STATUS_CACELLED;
    }

    @Override
    public boolean isDone() {
        return mStatus.intValue() >= STATUS_CACELLED;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    protected abstract void doInFuture();
}
