package com.peridotapps.nitro.concurrent;

import android.support.annotation.NonNull;

import com.peridotapps.nitro.concurrent.task.RunnableTask;
import com.peridotapps.nitro.concurrent.task.TaskMode;

public final class ConcurrentHandler {
    private static ConcurrentHandler sharedInstance;

    @NonNull
    public static ConcurrentHandler getSharedInstance() {
        ConcurrentHandler instance;
        synchronized (ConcurrentHandler.class) {
            if (sharedInstance == null) {
                sharedInstance = new ConcurrentHandler();
            }
            instance = sharedInstance;
        }
        return instance;
    }

    private ConcurrentHandler() {
    }

    public final void runOnNewThread(@NonNull Runnable action) {
        RunnableTask.createActionRunnable(action)
                .execute();
    }

    public final void runOnNewThread(@NonNull Runnable action, long delay) {
        RunnableTask.createActionRunnable(action)
                .setTaskDelay(delay)
                .execute();
    }

    public final void runOnUiThread(@NonNull Runnable action) {
        RunnableTask.createActionRunnable(action)
                .setTaskThreadMode(TaskMode.MAIN)
                .execute();
    }

    public final void runOnUiThread(@NonNull Runnable action, long delay) {
        RunnableTask.createActionRunnable(action)
                .setTaskThreadMode(TaskMode.MAIN)
                .setTaskDelay(delay)
                .execute();
    }
}
