package com.peridotapps.nitro.concurrent.task;

import android.support.annotation.NonNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class CallableTask<T> extends CoreTask implements Callable<T>, Comparable<CallableTask<T>> {
  
  private T defaultResult = null;
  private T result = null;
  
  public CallableTask () {
    super();
  }
  
  public CallableTask (String taskId) {
    super(taskId);
  }
  
  public abstract T onRun () throws Exception;
  
  @Override
  public final T doWork () throws Exception {
    this.result = onRun();
    return this.result;
  }
  
  @Override
  public final T call () throws Exception {
    try {
      super.run();
    } catch (Exception e) {
      return defaultResult;
    }
    return (this.result != null) ? this.result : this.defaultResult;
  }
  
  public final T execute () throws ExecutionException, InterruptedException {
    return TaskManager.execute(this);
  }
  
  public final Future<T> getFuture () {
    return TaskManager.getFuture(this);
  }
  
  public final CallableTask<T> setDefaultResult (T value) {
    this.defaultResult = value;
    return this;
  }
  
  public final CallableTask<T> addProperty (String key, Object value) {
    super.addProperty(key, value);
    return this;
  }
  
  public final CallableTask<T> addProperties (Map<String, Object> properties) {
    super.addProperties(properties);
    return this;
  }
  
  public final CallableTask<T> addListener (TaskListener listener) {
    super.addListener(listener);
    return this;
  }
  
  public final CallableTask<T> addListeners (Collection<TaskListener> listenerCollection) {
    super.addListeners(listenerCollection);
    return this;
  }
  
  @Override
  public final int compareTo (@NonNull CallableTask<T> o) {
    return this.getTaskId()
               .compareTo(o.getTaskId());
  }
  
  @Override
  public final boolean equals (Object obj) {
    if (obj instanceof CallableTask) {
      return ((CallableTask) obj).getTaskId()
                                 .equals(getTaskId());
    }
    return false;
  }
  
  @Override
  protected void notifyStarted (List<TaskListener> listeners) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        for (TaskListener listener : listeners) {
          notifyTaskStarted(listener);
        }
      }
    };
  }
  
  @Override
  protected void notifyCompleted (List<TaskListener> listeners) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        for (TaskListener listener : listeners) {
          notifyTaskCompleted(listener);
        }
      }
    };
  }
  
  @Override
  protected void notifyFailure (List<TaskListener> listeners, Throwable t) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        for (TaskListener listener : listeners) {
          notifyTaskFailed(listener, t);
        }
      }
    };
  }
  
  @Override
  protected void notifyTaskStarted (TaskListener listener) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        listener.started();
      }
    }.execute();
  }
  
  @Override
  protected void notifyTaskCompleted (TaskListener listener) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        listener.completed();
      }
    }.execute();
  }
  
  @Override
  protected void notifyTaskFailed (TaskListener listener, Throwable t) {
    new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        listener.failed(t);
      }
    }.execute();
  }
  
}
