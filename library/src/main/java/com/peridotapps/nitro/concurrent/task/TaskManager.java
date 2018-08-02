package com.peridotapps.nitro.concurrent.task;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.hardware.Cpu;
import com.peridotapps.nitro.random.RandomString;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.peridotapps.nitro.string.CharacterSet.ALPHA_NUMERIC;

final class TaskManager {
  
  private static final AtomicReference<TaskExecutor> sharedAtomicInstance = new AtomicReference<>(new TaskExecutor());
  
  private static final int INITIAL_THREAD_POOL_SIZE = 0;
  private static final int MAX_THREAD_POOL_SIZE = 24;
  private static final int TASK_ID_MAX_LENGTH = 25;
  private static final int TASK_ID_MIN_LENGTH = 10;
  private static final int THREAD_POOL_SIZE_MULTIPLIER = 8;
  
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
  
  private static TaskExecutor getSharedInstance () {
    TaskExecutor instance;
    
    synchronized (sharedAtomicInstance) {
      if (sharedAtomicInstance.get() == null) {
        sharedAtomicInstance.set(new TaskExecutor());
      }
      instance = sharedAtomicInstance.get();
    }
    
    return instance;
  }
  
  static String generateTaskId () {
    return new RandomString()
        .setCharacterSet(ALPHA_NUMERIC)
        .setMinLength(TASK_ID_MIN_LENGTH)
        .setMaxLength(TASK_ID_MAX_LENGTH)
        .generate();
  }
  
  static void execute (@NonNull RunnableTask runnableTask) {
    if (runnableTask.getTaskMode() == TaskMode.MAIN) {
      executeOnMain(runnableTask, runnableTask.getDelayInMilliseconds());
    } else {
      executeOnNew(runnableTask, runnableTask.getDelayInMilliseconds());
    }
  }
  
  @Nullable
  static <T> T execute (@NonNull CallableTask<T> callableTask) throws ExecutionException, InterruptedException {
    return getFuture(callableTask).get();
  }
  
  @NonNull
  static <T> Future<T> getFuture (@NonNull CallableTask<T> callableTask) {
    return getSharedInstance()
        .getExecutorServiceInstance()
        .submit(callableTask);
  }
  
  private static void executeOnNew (@NonNull Runnable runnable, long delayInMilliseconds) {
    if (delayInMilliseconds > 0) {
      getSharedInstance()
          .getScheduledExecutorServiceInstance()
          .schedule(runnable, delayInMilliseconds, DEFAULT_TIME_UNIT);
    } else {
      getSharedInstance()
          .getExecutorServiceInstance()
          .execute(runnable);
    }
  }
  
  private static void executeOnMain (@NonNull Runnable runnable, long delayInMilliseconds) {
    if (delayInMilliseconds > 0) {
      getSharedInstance()
          .getHandlerInstance()
          .postDelayed(runnable, delayInMilliseconds);
    } else {
      getSharedInstance()
          .getHandlerInstance()
          .post(runnable);
    }
  }
  
  static class TaskExecutor implements LifecycleObserver {
  
    private final ExecutorService executorServiceAtomicReference;
    private final Handler handlerAtomicReference;
    private final ScheduledExecutorService scheduledExecutorServiceAtomicReference;
    private final int threadPoolSize;
    
    TaskExecutor () {
      ProcessLifecycleOwner.get()
                           .getLifecycle()
                           .addObserver(this);
      
      this.threadPoolSize = this.calculateThreadPoolLimits();
      this.executorServiceAtomicReference = this.initExecutorService();
      this.handlerAtomicReference = this.initHandler();
      this.scheduledExecutorServiceAtomicReference = this.initScheduledExecutorService();
    }
    
    @NonNull
    private Handler getHandlerInstance () {
      return handlerAtomicReference;
    }
  
    @NonNull
    private ScheduledExecutorService getScheduledExecutorServiceInstance () {
      return scheduledExecutorServiceAtomicReference;
    }
  
    @NonNull
    private ExecutorService getExecutorServiceInstance () {
      return executorServiceAtomicReference;
    }
  
    @NonNull
    private Handler initHandler () {
      return new Handler(Looper.getMainLooper());
    }
  
    @NonNull
    private ScheduledExecutorService initScheduledExecutorService () {
      return Executors
          .newScheduledThreadPool(INITIAL_THREAD_POOL_SIZE);
    }
  
    @NonNull
    private ExecutorService initExecutorService () {
      return Executors
          .newFixedThreadPool(this.threadPoolSize);
    }
  
    private int calculateThreadPoolLimits () {
      return (Cpu.getNumberOfProcessorCores() * THREAD_POOL_SIZE_MULTIPLIER > MAX_THREAD_POOL_SIZE)
             ? MAX_THREAD_POOL_SIZE
             : Cpu.getNumberOfProcessorCores() * THREAD_POOL_SIZE_MULTIPLIER;
    }
  
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void resetTaskManager () {
      getExecutorServiceInstance().shutdown();
      getScheduledExecutorServiceInstance().shutdown();
      sharedAtomicInstance.set(null);
    }
    
  }
}
