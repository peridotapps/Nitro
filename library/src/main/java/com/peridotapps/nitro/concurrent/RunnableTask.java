package com.peridotapps.nitro.concurrent;

import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.random.RandomString;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.peridotapps.nitro.string.CharacterSet.ALPHA_NUMERIC;

public abstract class RunnableTask implements Task, Runnable, Comparable<RunnableTask> {

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean runningAtomicBoolean = new AtomicBoolean(false);
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final List<Task.TaskListener> listeners = new LinkedList<>();
    private final Map<String, Object> properties = new ConcurrentHashMap<>();
    private final String taskId = generateTaskId();

    private TaskMode taskMode = TaskMode.NEW;
    private Thread runnableThread = null;

    public static RunnableTask createActionRunnable(Runnable runnable) {
        return new RunnableTask() {
            @Override
            protected void onRun() throws Exception {
                runnable.run();
            }
        };
    }

    private static String generateTaskId() {
        return new RandomString().setCharacterSet(ALPHA_NUMERIC).setMinLength(10).setMaxLength(25).generate();
    }

    public RunnableTask() {
    }

    protected abstract void onRun() throws Exception;

    protected final boolean isCancelled() {
        boolean cancel;
        synchronized (cancelled) {
            cancel = cancelled.get();
        }
        return cancel;
    }

    private void setCancelled(boolean cancel) {
        synchronized (cancelled) {
            cancelled.set(cancel);
        }
    }

    public final void execute() {
        execute(0L);
    }

    public final void execute(long delayInMillis) {
        if (!isRunning()) {
            setCancelled(false);
            resetStopRequested();
            TaskManager.executeTask(this, delayInMillis);
        }
    }

    @Override
    public final void run() {
        if (!isRunning()) {
            try {
                runnableThread = Thread.currentThread();
                for (int currentStep = 0; currentStep < 3; currentStep++) {

                    if (isCancelled()) {
                        if (Thread.currentThread() != Looper.getMainLooper().getThread() && Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (!Thread.currentThread().isInterrupted() && !wasStopRequested()) {
                        switch (currentStep) {
                            case STEP_ON_START:
                                onStart();
                                break;
                            case STEP_ON_RUN:
                                onRun();
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
            }

            onStop();
            runnableThread = null;
        } else {
            throw new RuntimeException("Requested task (" + taskId + ") is already running", new Throwable());
        }
    }

    @CallSuper
    @Override
    public void onStart() {
        if (!isRunning()) {
            setRunning(true);

            List<Task.TaskListener> listenerList = getListeners();

            if (listenerList != null && !listenerList.isEmpty()) {
                notifyStarted(listenerList).execute();
            }
        }
    }

    @CallSuper
    @Override
    public void onStop() {
        setRunning(false);
    }

    @CallSuper
    @Override
    public void onCompleted() {
        List<Task.TaskListener> listenerList = getListeners();
        if (listenerList != null && !listenerList.isEmpty()) {
            notifyCompleted(listenerList).execute();
        }
    }

    @CallSuper
    @Override
    public void onFailed(Throwable t) {
        List<Task.TaskListener> listenerList = getListeners();
        if (listenerList != null && !listenerList.isEmpty()) {
            notifyFailure(listenerList, t).execute();
        }
    }

    public final RunnableTask setTaskThreadMode(TaskMode mode) {
        this.taskMode = mode;
        return this;
    }

    public final RunnableTask addProperty(String key, Object value) {
        synchronized (properties) {
            properties.put(key, value);
        }
        return this;
    }

    public final RunnableTask addProperties(Map<String, Object> properties) {
        synchronized (this.properties) {
            this.properties.putAll(properties);
        }
        return this;
    }

    public final RunnableTask addListener(Task.TaskListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        return this;
    }

    public final RunnableTask addListeners(Collection<Task.TaskListener> listenerCollection) {
        synchronized (this.listeners) {
            listeners.addAll(listenerCollection);
        }
        return this;
    }

    public final void cancel() {
        if (runnableThread != null) {
            runnableThread.interrupt();
        }
        requestStop();
        setCancelled(true);
    }

    public final void requestStop() {
        this.stopRequested.set(true);
    }

    protected final boolean wasStopRequested() {
        boolean stop;
        synchronized (this.stopRequested) {
            stop = stopRequested.get();
        }
        return stop;
    }

    public final Object getProperty(String key) {
        return getProperties().get(key);
    }

    public final Map<String, Object> getProperties() {
        Map<String, Object> propertyMap;

        synchronized (this.properties) {
            propertyMap = new HashMap<>(this.properties);
        }

        return propertyMap;
    }

    public final boolean isRunning() {
        synchronized (runningAtomicBoolean) {
            return runningAtomicBoolean.get();
        }
    }

    private void setRunning(boolean running) {
        synchronized (runningAtomicBoolean) {
            this.runningAtomicBoolean.set(running);
        }
    }

    public final String getTaskId() {
        return taskId;
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

    private List<Task.TaskListener> getListeners() {
        List<Task.TaskListener> taskListeners = new LinkedList<>();

        synchronized (listeners) {
            taskListeners.addAll(listeners);
        }

        return taskListeners;
    }

    private RunnableTask notifyStarted(final List<Task.TaskListener> listeners) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskStarted(listener).setTaskThreadMode(taskMode).execute();
                    }
                }
            }
        }.setTaskThreadMode(taskMode);
    }

    private RunnableTask notifyCompleted(final List<Task.TaskListener> listeners) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskCompleted(listener).setTaskThreadMode(taskMode).execute();
                    }
                }
            }
        }.setTaskThreadMode(taskMode);
    }

    private RunnableTask notifyFailure(final List<Task.TaskListener> listeners, final Throwable t) {
        return new RunnableTask() {
            @Override
            protected void onRun() {
                for (Task.TaskListener listener : listeners) {
                    if (listener != null) {
                        notifyTaskFailed(listener, t).setTaskThreadMode(taskMode).execute();
                    }
                }
            }
        }.setTaskThreadMode(taskMode);
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

    @Override
    public final int compareTo(@NonNull RunnableTask o) {
        return taskId.compareTo(o.taskId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof RunnableTask) {
            return ((RunnableTask) obj).taskId.equals(taskId);
        }
        return false;
    }

    public final TaskMode getTaskMode() {
        return taskMode;
    }

    private void resetStopRequested() {
        synchronized (this.stopRequested) {
            this.stopRequested.set(false);
        }
    }


}
