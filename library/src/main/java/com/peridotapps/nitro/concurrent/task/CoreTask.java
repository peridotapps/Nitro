package com.peridotapps.nitro.concurrent.task;

import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class CoreTask implements Task {
  
  private final String taskId;
  private final AtomicBoolean cancelled = new AtomicBoolean(false);
  private final AtomicBoolean runningAtomicBoolean = new AtomicBoolean(false);
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);
  private final List<Task.TaskListener> listeners = new LinkedList<>();
  private final Map<String, Object> properties = new ConcurrentHashMap<>();
  private Thread taskThread = null;
  
  public CoreTask () {
    this(TaskManager.generateTaskId());
  }
  
  public CoreTask (@NonNull String taskId) {
    this.taskId = taskId;
  }
  
  public final void run () {
    try {
      if (!isRunning()) {
        
        taskThread = Thread.currentThread();
        for (int currentStep = 0; currentStep < 3; currentStep++) {
          
          if (isCancelled()) {
            if (Thread.currentThread() != Looper.getMainLooper()
                                                .getThread() && Thread.currentThread()
                                                                      .isAlive() && !Thread.currentThread()
                                                                                           .isInterrupted()) {
              Thread.currentThread()
                    .interrupt();
            }
          }
          
          if (!Thread.currentThread()
                     .isInterrupted() && !isStopRequested()) {
            switch (currentStep) {
              case STEP_ON_START:
                onStart();
                break;
              case STEP_ON_RUN:
                doWork();
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
        
        onStop();
        taskThread = null;
        
      } else {
        throw new RuntimeException("Requested task (" + taskId + ") is already running", new Throwable());
      }
      
    } catch (Exception e) {
      onFailed(e);
    }
  }
  
  @Nullable
  public abstract <T> T doWork () throws Exception;
  
  @CallSuper
  @NonNull
  public CoreTask addProperty (@NonNull String key, @NonNull Object value) {
    synchronized (properties) {
      properties.put(key, value);
    }
    return this;
  }
  
  @CallSuper
  @NonNull
  public CoreTask addProperties (@NonNull Map<String, Object> properties) {
    synchronized (this.properties) {
      this.properties.putAll(properties);
    }
    return this;
  }
  
  @CallSuper
  @NonNull
  public CoreTask addListener (@NonNull Task.TaskListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
    return this;
  }
  
  @CallSuper
  @NonNull
  public CoreTask addListeners (@NonNull Collection<TaskListener> listenerCollection) {
    synchronized (this.listeners) {
      listeners.addAll(listenerCollection);
    }
    return this;
  }
  
  @CallSuper
  @Override
  public void onStart () {
    if (!isRunning()) {
      setRunning(true);
      
      List<Task.TaskListener> listenerList = getListeners();
      
      if (listenerList != null && !listenerList.isEmpty()) {
        notifyStarted(listenerList);
      }
    }
  }
  
  @CallSuper
  @Override
  public void onStop () {
    setRunning(false);
  }
  
  @CallSuper
  @Override
  public void onCompleted () {
    List<Task.TaskListener> listenerList = getListeners();
    if (listenerList != null && !listenerList.isEmpty()) {
      notifyCompleted(listenerList);
    }
  }
  
  @CallSuper
  @Override
  public void onFailed (@NonNull Throwable t) {
    List<Task.TaskListener> listenerList = getListeners();
    if (listenerList != null && !listenerList.isEmpty()) {
      notifyFailure(listenerList, t);
    }
  }
  
  public final void cancel () {
    if (taskThread != null) {
      taskThread.interrupt();
    }
    requestStop();
    setCancelled(true);
  }
  
  public final void requestStop () {
    this.stopRequested.set(true);
  }
  
  public final @NonNull
  String getTaskId () {
    return taskId;
  }
  
  public final boolean isCancelled () {
    boolean cancel;
    synchronized (cancelled) {
      cancel = cancelled.get();
    }
    return cancel;
  }
  
  public final boolean isStopRequested () {
    boolean stop;
    synchronized (this.stopRequested) {
      stop = stopRequested.get();
    }
    return stop;
  }
  
  public final @NonNull
  Map<String, Object> getProperties () {
    Map<String, Object> propertyMap;
    
    synchronized (this.properties) {
      propertyMap = new HashMap<>(this.properties);
    }
    
    return propertyMap;
  }
  
  public final boolean isRunning () {
    synchronized (runningAtomicBoolean) {
      return runningAtomicBoolean.get();
    }
  }
  
  public final @Nullable
  Object getProperty (@NonNull String key) {
    return getProperties().get(key);
  }
  
  public final void removeProperty (@NonNull String key) {
    synchronized (properties) {
      properties.remove(key);
    }
  }
  
  public final void clearProperties () {
    synchronized (properties) {
      properties.clear();
    }
  }
  
  public final void removeListener (@NonNull Task.TaskListener listener) {
    synchronized (this.listeners) {
      listeners.remove(listener);
    }
  }
  
  public final void removeListener (int position) {
    synchronized (this.listeners) {
      listeners.remove(position);
    }
  }
  
  public final void clearListeners () {
    synchronized (listeners) {
      listeners.clear();
    }
  }
  
  protected abstract void notifyStarted (@NonNull final List<Task.TaskListener> listeners);
  
  protected abstract void notifyCompleted (@NonNull final List<Task.TaskListener> listeners);
  
  protected abstract void notifyFailure (@NonNull final List<Task.TaskListener> listeners, @NonNull final Throwable t);
  
  protected abstract void notifyTaskStarted (@NonNull final Task.TaskListener listener);
  
  protected abstract void notifyTaskCompleted (@NonNull final Task.TaskListener listener);
  
  protected abstract void notifyTaskFailed (@NonNull final Task.TaskListener listener, @NonNull final Throwable t);
  
  protected final void resetStopRequested () {
    synchronized (this.stopRequested) {
      this.stopRequested.set(false);
    }
  }
  
  private void setCancelled (boolean cancel) {
    synchronized (cancelled) {
      cancelled.set(cancel);
    }
  }
  
  private void setRunning (boolean running) {
    synchronized (runningAtomicBoolean) {
      this.runningAtomicBoolean.set(running);
    }
  }
  
  private List<Task.TaskListener> getListeners () {
    List<Task.TaskListener> taskListeners = new LinkedList<>();
    
    synchronized (listeners) {
      taskListeners.addAll(listeners);
    }
    
    return taskListeners;
  }
  
}
