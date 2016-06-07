package cn.wanther.toolkit.component.async;

import android.util.Log;

import cn.wanther.toolkit.BuildConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: make this better
 *
 * status:
 *
 * STATUS_NEW --> STATUS_RUNNING --> STATUS_COMPLETE
 * STATUS_NEW --> STATUS_RUNNING --> STATUS_ERROR
 * STATUS_NEW --> STATUS_RUNNING --> STATUS_CANCELLED
 * STATUS_NEW --> STATUS_CANCELLED
 * @param <V>
 */
/*package*/ class FutureTask<V> implements Runnable, Future<V> {
    private static final String TAG = "FutureTask";

    private static final int STATUS_NEW = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_COMPLETE = 2;
    private static final int STATUS_CANCELLED = 3;
    private static final int STATUS_ERROR = 4;

    private AtomicInteger status = new AtomicInteger(STATUS_NEW);
    private Callable<V> callable;
    private Thread runner;
    private V result;
    private Exception exception;

    public FutureTask(Callable<V> callable) {
        if (callable == null) {
            throw new RuntimeException("callable cannot be null");
        }
        this.callable = callable;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        status.set(STATUS_CANCELLED);
        if (mayInterruptIfRunning && runner != null) {
            runner.interrupt();
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return status.get() == STATUS_CANCELLED;
    }

    @Override
    public boolean isDone() {
        return status.get() >= STATUS_COMPLETE;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        try {
            if (exception != null) {
                throw new ExecutionException(exception);
            } else {
                return result;
            }
        } finally {
            result = null;
            exception = null;
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // TODO:
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void run() {
        try {
            if (!status.compareAndSet(STATUS_NEW, STATUS_RUNNING)) {
                if (status.get() != STATUS_CANCELLED) {
                    throw new RuntimeException("do not repeat execute the same task");
                }

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "task is cancelled before run");
                }

                return;
            }

            runner = Thread.currentThread();

            result = callable.call();

            status.compareAndSet(STATUS_RUNNING, STATUS_COMPLETE);
        } catch (Exception e) {
            exception = e;
            status.compareAndSet(STATUS_RUNNING, STATUS_ERROR);
        } finally {
            try {
                done();
                // reset interrupt flag
                Thread.interrupted();
            } finally {
                callable = null;
                runner = null;
            }
        }
    }

    protected void done(){}
}
