package com.peridotapps.nitro.concurrent;

import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.logging.Logger;
import com.peridotapps.nitro.random.RandomString;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.peridotapps.nitro.string.CharacterSet.ALPHA_NUMERIC;

public abstract class CallableTask<T> implements Callable<T>, Task, Comparable<CallableTask<T>> {

    private final AtomicBoolean cancelRequested = new AtomicBoolean(false);
    private final AtomicBoolean runningAtomicBoolean = new AtomicBoolean(false);
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final List<Task.TaskListener> listeners = new LinkedList<>();
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    private final String taskId = generateTaskId();

    private static String generateTaskId() {
        return new RandomString().setCharacterSet(ALPHA_NUMERIC).setMinLength(10).setMaxLength(25).generate();
    }

    private T defaultResult = null;
    private T result = null;

    private Thread callableThread;

    public final T getResult() {
        return getResult(0L);
    }

    public final T getResult(long delayInMilliseconds) {
        setCancelled(false);
        setStopRequested(false);
        return TaskManager.executeTask(this, delayInMilliseconds);
    }

    public final Future<T> getFuture() {
        return TaskManager.getFuture(this, 0L);
    }

    public final Future<T> getFuture(long delayInMilliseconds) {
        return TaskManager.getFuture(this, delayInMilliseconds);
    }

    @Override
    public final T call() throws Exception {
        if (!isRunning()) {
            try {
                callableThread = Thread.currentThread();
                for (int currentStep = 0; currentStep < 3; currentStep++) {
                    if (isCancelled()) {
                        if (Thread.currentThread() != Looper.getMainLooper().getThread() && Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (!isCancelled() && !wasStopRequested()) {
                        switch (currentStep) {
                            case STEP_ON_START:
                                onStart();
                                break;
                            case STEP_ON_RUN:
                                this.result = onRun();
                                break;
                            case STEP_ON_COMPLETE:
                                onCompleted();
                                break;
                        }
                    } else {
                        if (isCancelled()) {
                            throw new InterruptedException("Task " + taskId + " was cancelled");
                        } else {
                            throw new InterruptedException("A stop was requested for task: " + taskId);
                        }
                    }
                }
            } catch (Exception e) {
                onFailed(e);
                this.result = getDefaultResult();
            }

            onStop();
            callableThread = null;
        } else {
            throw new RuntimeException("Requested task (" + taskId + ") is already running", new Throwable());
        }

        return this.result;
    }

    public final boolean isRunning() {
        synchronized (runningAtomicBoolean) {
            return runningAtomicBoolean.get();
        }
    }

    public abstract T onRun() throws Exception;

    @Override
    @CallSuper
    public void onStart() {
        setRunning(true);
        notifyStarted(getListeners()).execute();
    }

    @Override
    @CallSuper
    public void onStop() {
        setRunning(false);
    }

    @Override
    @CallSuper
    public void onCompleted() {
        notifyCompleted(getListeners()).execute();
    }

    @Override
    @CallSuper
    public void onFailed(Throwable t) {
        notifyFailure(getListeners(), t).execute();
        Logger.E(this, t);
    }

    public final void cancel() {
        if (callableThread != null) {
            callableThread.interrupt();
        }
        requestStop();
        setCancelled(true);
    }

    public final void requestStop() {
        setStopRequested(true);
    }

    public final String getTaskId() {
        return taskId;
    }

    public final T getDefaultResult() {
        return defaultResult;
    }

    public final CallableTask<T> setDefaultResult(T value) {
        this.defaultResult = value;
        return this;
    }

    public final CallableTask<T> addProperty(String key, Object value) {
        synchronized (properties) {
            properties.put(key, value);
        }
        return this;
    }

    public final CallableTask<T> addProperties(Map<String, Object> properties) {
        synchronized (this.properties) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public final CallableTask<T> addListener(TaskListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        return this;
    }

    public final CallableTask<T> addListeners(Collection<TaskListener> listenerCollection) {
        synchronized (this.listeners) {
            listeners.addAll(listenerCollection);
        }
        return this;
    }

    public final Object getProperty(String key) {
        Object value = null;
        synchronized (properties) {
            value = properties.get(key);
        }
        return value;
    }

    public final void removeProperty(String key) {
        synchronized (properties) {
            properties.remove(key);
        }
    }

    public final void clearProperties() {
        synchronized (properties) {
            properties.clear();
        }
    }

    public final void removeListener(Task.TaskListener listener) {
        synchronized (this.listeners) {
            listeners.remove(listener);
        }
    }

    public final void removeListener(int position) {
        synchronized (this.listeners) {
            listeners.remove(position);
        }
    }

    public final void clearListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    @Override
    public final int compareTo(@NonNull CallableTask<T> o) {
        return this.taskId.compareTo(o.taskId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof CallableTask) {
            return ((CallableTask) obj).taskId.equals(taskId);
        }
        return false;
    }

    protected final boolean isCancelled() {
        boolean cancel;
        synchronized (cancelRequested) {
            cancel = cancelRequested.get();
        }
        return cancel;
    }

    private RunnableTask notifyStarted(final List<Task.TaskListener> listeners) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskStarted(listener).setTaskThreadMode(TaskMode.NEW).execute();
                    }
                }
            }
        }.setTaskThreadMode(TaskMode.NEW);
    }

    private List<TaskListener> getListeners() {
        List<Task.TaskListener> taskListeners = new LinkedList<>();

        synchronized (listeners) {
            taskListeners.addAll(listeners);
        }

        return taskListeners;
    }

    private RunnableTask notifyCompleted(final List<Task.TaskListener> listeners) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskCompleted(listener).setTaskThreadMode(TaskMode.NEW).execute();
                    }
                }
            }
        }.setTaskThreadMode(TaskMode.NEW);
    }

    private RunnableTask notifyFailure(final List<Task.TaskListener> listeners, final Throwable t) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskFailed(listener, t).setTaskThreadMode(TaskMode.NEW).execute();
                    }
                }
            }
        }.setTaskThreadMode(TaskMode.NEW);
    }

    private RunnableTask notifyTaskStarted(final Task.TaskListener listener) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                listener.started();
            }
        };
    }

    private RunnableTask notifyTaskCompleted(final Task.TaskListener listener) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                listener.completed();
            }
        };
    }

    private RunnableTask notifyTaskFailed(final Task.TaskListener listener, final Throwable t) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                listener.failed(t);
            }
        };
    }

    private void setRunning(boolean running) {
        synchronized (runningAtomicBoolean) {
            runningAtomicBoolean.set(running);
        }
    }

    private void setStopRequested(boolean stop) {
        synchronized (stopRequested) {
            stopRequested.set(stop);
        }
    }

    private void setCancelled(boolean cancel) {
        synchronized (cancelRequested) {
            cancelRequested.set(cancel);
        }
    }

    private boolean wasStopRequested() {
        boolean stop;
        synchronized (stopRequested) {
            stop = stopRequested.get();
        }
        return stop;
    }

}
