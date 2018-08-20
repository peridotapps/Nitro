package com.peridotapps.nitro.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.concurrent.task.RunnableTask;
import com.peridotapps.nitro.concurrent.task.Task;
import com.peridotapps.nitro.concurrent.task.TaskMode;

import java.util.Collection;
import java.util.Map;

public abstract class AsyncOperation<I, O> extends RunnableTask {

    @Nullable
    private I input = null;

    @Nullable
    private O result = null;

    @Nullable
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

    @Nullable
    public abstract O onRun(@Nullable I input) throws Exception;

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
            nextTask.addProperties(this.getProperties())
                    .setInput(getResult())
                    .execute();
        }
    }

    @NonNull
    public final AsyncOperation<I, O> setInput(@Nullable I input) {
        this.input = input;
        return this;
    }

    @NonNull
    public final AsyncOperation<I, O> setNextTask(@NonNull AsyncOperation<O, ?> task) {
        this.nextTask = task;
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> setTaskDelay(long delayInMilliseconds) {
        super.setTaskDelay(delayInMilliseconds);
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> setTaskThreadMode(@NonNull TaskMode mode) {
        super.setTaskThreadMode(mode);
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> addProperty(@NonNull String key, Object value) {
        super.addProperty(key, value);
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> addProperties(@NonNull Map<String, Object> properties) {
        super.addProperties(properties);
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> addListener(@NonNull Task.TaskListener listener) {
        super.addListener(listener);
        return this;
    }

    @NonNull
    @Override
    public final AsyncOperation<I, O> addListeners(@NonNull Collection<TaskListener> listenerCollection) {
        super.addListeners(listenerCollection);
        return this;
    }

    @Nullable
    public I getInput() {
        return input;
    }

    @Nullable
    public O getResult() {
        return result;
    }
}
