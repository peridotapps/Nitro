package com.peridotapps.nitro.concurrent.task;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.random.RandomString;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.peridotapps.nitro.string.CharacterSet.ALPHA_NUMERIC;

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
  public Void doWork() throws Exception {
    onRun();
    return null;
  }
  
  public RunnableTask() {
    super();
  }
  
  public RunnableTask(String taskId) {
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
  
  @CallSuper
  @Override
  public RunnableTask addProperty(String key, Object value) {
    super.addProperty(key, value);
    return this;
  }
  
  @CallSuper
  @Override
  public RunnableTask addProperties(Map<String, Object> properties) {
    super.addProperties(properties);
    return this;
  }
  
  @CallSuper
  @Override
  public RunnableTask addListener(Task.TaskListener listener) {
    super.addListener(listener);
    return this;
  }
  
  @CallSuper
  @Override
  public RunnableTask addListeners(Collection<Task.TaskListener> listenerCollection) {
    super.addListeners(listenerCollection);
    return this;
  }
  
  @Override
  public final int compareTo(@NonNull RunnableTask o) {
    return this.getTaskId().compareTo(o.getTaskId());
  }
  
  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof RunnableTask) {
      return ((RunnableTask) obj).getTaskId().equals(this.getTaskId());
    }
    return false;
  }
  
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
  protected void notifyStarted(List<TaskListener> listeners) {
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
  protected void notifyCompleted(List<TaskListener> listeners) {
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
  protected void notifyFailure(List<TaskListener> listeners, Throwable t) {
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
  protected void notifyTaskStarted(TaskListener listener) {
    new RunnableTask() {
      @Override
      public void onRun() throws Exception {
        listener.started();
      }
    }.execute();
  }
  
  @Override
  protected void notifyTaskCompleted(TaskListener listener) {
    new RunnableTask() {
      @Override
      public void onRun() throws Exception {
        listener.completed();
      }
    }.execute();
  }
  
  @Override
  protected void notifyTaskFailed(TaskListener listener, Throwable t) {
    new RunnableTask() {
      @Override
      public void onRun() throws Exception {
        listener.failed(t);
      }
    }.execute();
  }
  
  private static String generateTaskId() {
    return new RandomString().setCharacterSet(ALPHA_NUMERIC).setMinLength(10).setMaxLength(25).generate();
  }
}
