package com.peridotapps.nitro.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.peridotapps.nitro.NitroApplication;

public final class BootReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent != null &&
        intent.getAction() != null &&
        intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      NitroApplication.getSharedInstance().onDeviceBootCompleted();
    }
  }
  
}
