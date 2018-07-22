package com.peridotapps.nitro.async;

import android.support.annotation.NonNull;

import com.peridotapps.nitro.concurrent.task.RunnableTask;
import com.peridotapps.nitro.concurrent.task.Task;
import com.peridotapps.nitro.concurrent.task.TaskMode;

import java.util.Collection;
import java.util.Map;

public abstract class AsyncOperation<I, O> extends RunnableTask {
  
  private I input = null;
  private O result;
  private AsyncOperation<O, ?> nextTask;
  
  public AsyncOperation() {
    super();
  }
  
  public AsyncOperation(@NonNull I input) {
    super();
    this.setInput(input);
  }
  
  public AsyncOperation(@NonNull String taskId) {
    super(taskId);
  }
  
  public AsyncOperation(@NonNull String taskId, @NonNull I input) {
    super(taskId);
    this.input = input;
  }
  
  public abstract O onRun(I input) throws Exception;
  
  @Override
  public final void onRun() throws Exception {
    this.result = onRun(input);
  }
  
  @Override
  public void onCompleted() {
    super.onCompleted();
    queueNextTask();
  }
  
  private void queueNextTask() {
    if (nextTask != null) {
      nextTask.addProperties(this.getProperties()).setInput(getResult()).execute();
    }
  }
  
  public final AsyncOperation<I, O> setInput(@NonNull I input) {
    this.input = input;
    return this;
  }
  
  public final AsyncOperation<I, O> setNextTask(@NonNull AsyncOperation<O, ?> task) {
    this.nextTask = task;
    return this;
  }
  
  @Override
  public final AsyncOperation<I, O> setTaskDelay(long delayInMilliseconds) {
    super.setTaskDelay(delayInMilliseconds);
    return this;
  }
  
  @Override
  public final AsyncOperation<I, O> setTaskThreadMode(@NonNull TaskMode mode) {
    super.setTaskThreadMode(mode);
    return this;
  }
  
  
  @Override
  public final AsyncOperation<I, O> addProperty(@NonNull String key, Object value) {
    super.addProperty(key, value);
    return this;
  }
  
  
  @Override
  public final AsyncOperation<I, O> addProperties(@NonNull Map<String, Object> properties) {
    super.addProperties(properties);
    return this;
  }
  
  
  @Override
  public final AsyncOperation<I, O> addListener(@NonNull Task.TaskListener listener) {
    super.addListener(listener);
    return this;
  }
  
  @Override
  public final AsyncOperation<I, O> addListeners(@NonNull Collection<TaskListener> listenerCollection) {
    super.addListeners(listenerCollection);
    return this;
  }
  
  public I getInput() {
    return input;
  }
  
  public O getResult() {
    return result;
  }
}
