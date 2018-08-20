package com.peridotapps.nitro.hardware;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.NitroApplication;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class Memory {

    private Memory() {
    }

    private static final long BYTES_IN_MEGABYTE = 0x100000L;
    private static final long UNABLE_TO_MEASURE = -1L;

    private static final AtomicLong totalMemory = new AtomicLong(0L);

    private static final AtomicReference<ActivityManager> activityManagerAtomicReference = new AtomicReference<>(initActivityManager());

    @NonNull
    private static ActivityManager getActivityManagerInstance() {
        synchronized (activityManagerAtomicReference) {
            if (activityManagerAtomicReference.get() == null) {
                activityManagerAtomicReference.set(initActivityManager());
            }
        }
        return activityManagerAtomicReference.get();
    }

    public static long getTotalMemory() {
        long memoryTotal;

        memoryTotal = getMemoryTotal();

        if (memoryTotal <= 0L) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                memoryTotal = getMemoryInfoFromActivityManager().totalMem / BYTES_IN_MEGABYTE;
            } else {
                memoryTotal = UNABLE_TO_MEASURE;
            }
        }

        setMemoryTotal(memoryTotal);

        return memoryTotal;
    }

    public static long getFreeMemory() {
        return getMemoryInfoFromActivityManager().availMem / BYTES_IN_MEGABYTE;
    }

    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    @Nullable
    private static ActivityManager initActivityManager() {
        return (ActivityManager) NitroApplication.getSharedInstance()
                .getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
    }

    @NonNull
    private static ActivityManager.MemoryInfo getMemoryInfoFromActivityManager() {
        ActivityManager activityManager = getActivityManagerInstance();
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);
        return mi;
    }

    private static void setMemoryTotal(long memoryTotal) {
        synchronized (totalMemory) {
            totalMemory.set(memoryTotal);
        }
    }

    private static long getMemoryTotal() {
        long memoryTotal;
        synchronized (totalMemory) {
            memoryTotal = totalMemory.get();
        }
        return memoryTotal;
    }
}
