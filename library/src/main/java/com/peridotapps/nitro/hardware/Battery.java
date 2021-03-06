package com.peridotapps.nitro.hardware;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.NitroApplication;

public final class Battery {

    private Battery() {
    }

    @Nullable
    private static Intent getBatteryStatus() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return NitroApplication.getSharedInstance()
                .registerReceiver(null, intentFilter);
    }

    public static boolean isCharging() {
        Intent batteryStatus = getBatteryStatus();
        int status = (batteryStatus != null) ? batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public static boolean isUSBCharging() {
        Intent batteryStatus = getBatteryStatus();
        int chargePlug = (batteryStatus != null) ? batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) : -1;
        return chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public static boolean isACCharging() {
        Intent batteryStatus = getBatteryStatus();
        int chargePlug = (batteryStatus != null) ? batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) : -1;
        return chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }

    public static float currentBatteryLevel() {
        Intent batteryStatus = getBatteryStatus();
        int level = (batteryStatus != null) ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = (batteryStatus != null) ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        return level / (float) scale;
    }
}
