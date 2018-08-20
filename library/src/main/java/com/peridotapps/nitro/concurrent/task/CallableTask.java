package com.peridotapps.nitro.concurrent.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class CallableTask<T> extends CoreTask implements Callable<T>, Comparable<CallableTask<T>> {

    @Nullable
    private T defaultResult = null;

    @Nullable
    private T result = null;

    public CallableTask() {
        super();
    }

    public CallableTask(@NonNull String taskId) {
        super(taskId);
    }

    @Nullable
    public abstract T onRun() throws Exception;

    @Nullable
    @Override
    public final T doWork() throws Exception {
        this.result = onRun();
        return this.result;
    }

    @Nullable
    @Override
    public final T call() throws Exception {
        try {
            super.run();
        } catch (Exception e) {
            return defaultResult;
        }
        return (this.result != null) ? this.result : this.defaultResult;
    }

    @Nullable
    public final T execute() throws ExecutionException, InterruptedException {
        return TaskManager.execute(this);
    }

    @NonNull
    public final Future<T> getFuture() {
        return TaskManager.getFuture(this);
    }

    @NonNull
    public final CallableTask<T> setDefaultResult(@NonNull T value) {
        this.defaultResult = value;
        return this;
    }

    @Override
    @NonNull
    public final CallableTask<T> addProperty(@NonNull String key, @NonNull Object value) {
        super.addProperty(key, value);
        return this;
    }

    @Override
    @NonNull
    public final CallableTask<T> addProperties(@NonNull Map<String, Object> properties) {
        super.addProperties(properties);
        return this;
    }

    @Override
    @NonNull
    public final CallableTask<T> addListener(@NonNull TaskListener listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    @NonNull
    public final CallableTask<T> addListeners(@NonNull Collection<TaskListener> listenerCollection) {
        super.addListeners(listenerCollection);
        return this;
    }

    @Override
    public final int compareTo(@NonNull CallableTask<T> o) {
        return this.getTaskId()
                .compareTo(o.getTaskId());
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof CallableTask) {
            return ((CallableTask) obj).getTaskId()
                    .equals(getTaskId());
        }
        return false;
    }

    @Override
    protected void notifyStarted(@NonNull List<TaskListener> listeners) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                for (TaskListener listener : listeners) {
                    notifyTaskStarted(listener);
                }
            }
        };
    }

    @Override
    protected void notifyCompleted(@NonNull List<TaskListener> listeners) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                for (TaskListener listener : listeners) {
                    notifyTaskCompleted(listener);
                }
            }
        };
    }

    @Override
    protected void notifyFailure(@NonNull List<TaskListener> listeners, @NonNull Throwable t) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                for (TaskListener listener : listeners) {
                    notifyTaskFailed(listener, t);
                }
            }
        };
    }

    @Override
    protected void notifyTaskStarted(@NonNull TaskListener listener) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                listener.started();
            }
        }.execute();
    }

    @Override
    protected void notifyTaskCompleted(@NonNull TaskListener listener) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                listener.completed();
            }
        }.execute();
    }

    @Override
    protected void notifyTaskFailed(@NonNull TaskListener listener, @NonNull Throwable t) {
        new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                listener.failed(t);
            }
        }.execute();
    }

}
