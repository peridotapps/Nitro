package com.peridotapps.nitro.concurrent.task;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RunnableTask extends CoreTask implements Runnable, Comparable<RunnableTask> {

    private TaskMode taskMode = TaskMode.NEW;
    private final AtomicLong delay = new AtomicLong(0L);

    public static RunnableTask createActionRunnable(Runnable runnable) {
        return new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                runnable.run();
            }
        };
    }

    @CallSuper
    @Override
    @Nullable
    public Void doWork() throws Exception {
        onRun();
        return null;
    }

    public RunnableTask() {
        super();
    }

    public RunnableTask(@NonNull String taskId) {
        super(taskId);
    }

    public abstract void onRun() throws Exception;

    public final void execute() {
        TaskManager.execute(this);
    }

    @CallSuper
    public RunnableTask setTaskDelay(long delayInMilliseconds) {
        synchronized (this.delay) {
            this.delay.set(delayInMilliseconds);
        }
        return this;
    }

    @CallSuper
    public RunnableTask setTaskThreadMode(TaskMode mode) {
        this.taskMode = mode;
        return this;
    }

    @NonNull
    @CallSuper
    @Override
    public RunnableTask addProperty(@NonNull String key, @NonNull Object value) {
        super.addProperty(key, value);
        return this;
    }

    @NonNull
    @CallSuper
    @Override
    public RunnableTask addProperties(@NonNull Map<String, Object> properties) {
        super.addProperties(properties);
        return this;
    }

    @NonNull
    @CallSuper
    @Override
    public RunnableTask addListener(@NonNull Task.TaskListener listener) {
        super.addListener(listener);
        return this;
    }

    @NonNull
    @CallSuper
    @Override
    public RunnableTask addListeners(@NonNull Collection<Task.TaskListener> listenerCollection) {
        super.addListeners(listenerCollection);
        return this;
    }

    @Override
    public final int compareTo(@NonNull RunnableTask o) {
        return this.getTaskId()
                .compareTo(o.getTaskId());
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof RunnableTask) {
            return ((RunnableTask) obj).getTaskId()
                    .equals(this.getTaskId());
        }
        return false;
    }

    @NonNull
    public final TaskMode getTaskMode() {
        return taskMode;
    }

    public long getDelayInMilliseconds() {
        long delayInMilliseconds;
        synchronized (delay) {
            delayInMilliseconds = delay.get();
        }
        return delayInMilliseconds;
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
