package com.peridotapps.nitro.concurrent;

public final class ConcurrentHandler {

    private static ConcurrentHandler sharedInstance;

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

    public final void runOnNewThread(Runnable action) {
        RunnableTask.createActionRunnable(action).execute();
    }

    public final void runOnNewThread(Runnable action, long delay) {
        RunnableTask.createActionRunnable(action).execute(delay);
    }

    public final void runOnUiThread(Runnable action) {
        RunnableTask.createActionRunnable(action).setTaskThreadMode(Task.TaskMode.MAIN).execute();
    }

    public final void runOnUiThread(Runnable action, long delay) {
        RunnableTask.createActionRunnable(action).setTaskThreadMode(Task.TaskMode.MAIN).execute(delay);
    }
}
