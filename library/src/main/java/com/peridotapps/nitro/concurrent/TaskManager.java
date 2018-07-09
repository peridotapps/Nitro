package com.peridotapps.nitro.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.peridotapps.nitro.hardware.Cpu;
import com.peridotapps.nitro.logging.Logger;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

final class TaskManager {
  
  private static final int INITIAL_THREAD_POOL_SIZE = 0;
  private static final int THREAD_POOL_SIZE_MULTIPLIER = 8;
  private static final int MAXIMUM_THREAD_POOL_SIZE = Cpu.getNumberOfProcessorCores() * THREAD_POOL_SIZE_MULTIPLIER;
  private static final long DEFAULT_DELAY_EMPTY = 0L;
  private static final long DEFAULT_IDLE_KEEP_ALIVE_DURATION = 100L;
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
  private static final AtomicReference<TaskManager> sharedAtomicInstance = new AtomicReference<>(new TaskManager());
  
  private final AtomicReference<Handler> handlerAtomicReference = new AtomicReference<>(initHandler());
  private final AtomicReference<ScheduledExecutorService> scheduledExecutorServiceAtomicReference = new AtomicReference<>(initScheduledExecutorService());
  private final AtomicReference<ExecutorService> executorServiceAtomicReference = new AtomicReference<>(initExecutorService());
  
  static TaskManager getSharedInstance() {
    TaskManager instance;
    synchronized (sharedAtomicInstance) {
      if (sharedAtomicInstance.get() == null) {
        sharedAtomicInstance.set(new TaskManager());
      }
      instance = sharedAtomicInstance.get();
    }
    
    return instance;
  }
  
  
  private TaskManager() {
  }
  
  static void executeTask(@NonNull final Runnable runnable, long delayInMilliseconds) {
    if (runnable instanceof RunnableTask) {
      RunnableTask task = (RunnableTask) runnable;
      if (task.getTaskMode() == Task.TaskMode.MAIN) {
        executeTaskOnMain(task, delayInMilliseconds);
      } else if (task.getTaskMode() == Task.TaskMode.NEW) {
        executeTaskInNewThread(task, delayInMilliseconds);
      }
    } else {
      executeTaskInNewThread(runnable, delayInMilliseconds);
    }
  }
  
  private static void executeTaskOnMain(final Runnable runnable, long delayInMilliseconds) {
    if (delayInMilliseconds > DEFAULT_DELAY_EMPTY) {
      getSharedInstance().getHandlerInstance().postDelayed(runnable, delayInMilliseconds);
    } else {
      getSharedInstance().getHandlerInstance().post(runnable);
    }
  }
  
  private static void executeTaskInNewThread(final Runnable runnable, long delayInMilliseconds) {
    if (delayInMilliseconds > DEFAULT_DELAY_EMPTY) {
      getSharedInstance().getScheduledExecutorServiceInstance().schedule(runnable, delayInMilliseconds, DEFAULT_TIME_UNIT);
    } else {
      getSharedInstance().getExecutorServiceInstance().execute(runnable);
    }
  }
  
  @NonNull
  Handler getHandlerInstance() {
    synchronized (handlerAtomicReference) {
      if (handlerAtomicReference.get() == null) {
        handlerAtomicReference.set(initHandler());
      }
    }
    
    return handlerAtomicReference.get();
  }
  
  @NonNull
  ScheduledExecutorService getScheduledExecutorServiceInstance() {
    synchronized (scheduledExecutorServiceAtomicReference) {
      if (scheduledExecutorServiceAtomicReference.get() == null) {
        scheduledExecutorServiceAtomicReference.set(initScheduledExecutorService());
      }
    }
    
    return scheduledExecutorServiceAtomicReference.get();
  }
  
  @NonNull
  ExecutorService getExecutorServiceInstance() {
    synchronized (executorServiceAtomicReference) {
      if (executorServiceAtomicReference.get() == null) {
        executorServiceAtomicReference.set(initExecutorService());
      }
    }
    
    return executorServiceAtomicReference.get();
  }
  
  @NonNull
  private Handler initHandler() {
    return new Handler(Looper.getMainLooper());
  }
  
  @NonNull
  private ScheduledExecutorService initScheduledExecutorService() {
    return Executors.newScheduledThreadPool(MAXIMUM_THREAD_POOL_SIZE);
  }
  
  @NonNull
  private ExecutorService initExecutorService() {
    return Executors.newFixedThreadPool(MAXIMUM_THREAD_POOL_SIZE);
  }
  
  static <T> T executeTask(Callable<T> task) {
    return executeTask(task, 0L);
  }
  
  static <T> T executeTask(Callable<T> task, long delayInMilliseconds) {
    T value = null;
    
    try {
      Future<T> future = getFuture(task, delayInMilliseconds);
      value = future.get();
    } catch (InterruptedException e) {
      Logger.E(task, e);
    } catch (ExecutionException e) {
      Logger.E(task, e);
    } catch (Exception e) {
      Logger.E(task, e);
    }
    
    return value;
  }
  
  static <T> Future<T> getFuture(Callable<T> task, long delayInMilliseconds) {
    if (delayInMilliseconds > DEFAULT_DELAY_EMPTY) {
      return getSharedInstance().getScheduledExecutorServiceInstance().schedule(task, delayInMilliseconds, DEFAULT_TIME_UNIT);
    } else {
      return getSharedInstance().getExecutorServiceInstance().submit(task);
    }
  }
}
