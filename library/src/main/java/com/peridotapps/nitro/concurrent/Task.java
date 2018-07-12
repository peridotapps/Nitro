package com.peridotapps.nitro.concurrent;

public interface Task {

    int STEP_ON_START = 0;
    int STEP_ON_RUN = 1;
    int STEP_ON_COMPLETE = 2;

    void onStart();

    void onStop();

    void onCompleted();

    void onFailed(Throwable t);

    interface TaskListener {

        void started();

        void completed();

        void failed(Throwable t);

    }

    enum TaskMode {
        MAIN,
        NEW
    }
}