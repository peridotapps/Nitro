package com.peridotapps.nitro.concurrent.task;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

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

final class TaskManager implements LifecycleObserver {
  
  private static final int INITIAL_THREAD_POOL_SIZE = 0;
  private static final int THREAD_POOL_SIZE_MULTIPLIER = 8;
  private static final int MAX_THREAD_POOL_SIZE = 24;
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
  private static final AtomicReference<TaskManager> sharedAtomicInstance = new AtomicReference<>(new TaskManager());
  
  private final AtomicReference<Handler> handlerAtomicReference = new AtomicReference<>(initHandler());
  private final AtomicReference<ScheduledExecutorService> scheduledExecutorServiceAtomicReference = new AtomicReference<>(initScheduledExecutorService());
  private final AtomicReference<ExecutorService> executorServiceAtomicReference = new AtomicReference<>(initExecutorService());
  
  private final int threadPoolSize;
  
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
  
  static void execute(RunnableTask runnableTask) {
    if (runnableTask.getTaskMode() == TaskMode.MAIN) {
      executeOnMain(runnableTask);
    } else {
      executeOnNew(runnableTask);
    }
  }
  
  static <T> T execute(CallableTask<T> callableTask) throws ExecutionException, InterruptedException {
    return getFuture(callableTask).get();
  }
  
  static <T> Future<T> getFuture(CallableTask<T> callableTask) {
    return getSharedInstance().getExecutorServiceInstance().submit(callableTask);
  }
  
  private static void executeOnNew(RunnableTask runnableTask) {
    if (runnableTask.getDelayInMilliseconds() > 0) {
      getSharedInstance().getScheduledExecutorServiceInstance().schedule(runnableTask, runnableTask.getDelayInMilliseconds(), DEFAULT_TIME_UNIT);
    } else {
      getSharedInstance().getExecutorServiceInstance().execute(runnableTask);
    }
  }
  
  private static void executeOnMain(RunnableTask runnableTask) {
    if (runnableTask.getDelayInMilliseconds() > 0) {
      getSharedInstance().getHandlerInstance().postDelayed(runnableTask, runnableTask.getDelayInMilliseconds());
    } else {
      getSharedInstance().getHandlerInstance().post(runnableTask);
    }
  }
  
  private TaskManager() {
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    this.threadPoolSize = calculateThreadPoolLimits();
    
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  void resetTaskManager() {
    getExecutorServiceInstance().shutdown();
    getScheduledExecutorServiceInstance().shutdown();
    executorServiceAtomicReference.set(null);
    handlerAtomicReference.set(null);
    scheduledExecutorServiceAtomicReference.set(null);
    sharedAtomicInstance.set(null);
  }
  
  static String generateTaskId() {
    return new RandomString()
        .setCharacterSet(ALPHA_NUMERIC)
        .setMinLength(10)
        .setMaxLength(25)
        .generate();
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
    return Executors.newScheduledThreadPool(INITIAL_THREAD_POOL_SIZE);
  }
  
  @NonNull
  private ExecutorService initExecutorService() {
    return Executors.newFixedThreadPool(this.threadPoolSize);
  }
  
  private int calculateThreadPoolLimits() {
    return (Cpu.getNumberOfProcessorCores() * THREAD_POOL_SIZE_MULTIPLIER > MAX_THREAD_POOL_SIZE)
        ? MAX_THREAD_POOL_SIZE
        : Cpu.getNumberOfProcessorCores() * THREAD_POOL_SIZE_MULTIPLIER;
  }
  
}
