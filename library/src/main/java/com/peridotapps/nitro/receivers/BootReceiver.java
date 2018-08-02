package com.peridotapps.nitro.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.NitroApplication;

public final class BootReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive (@NonNull Context context, @NonNull Intent intent) {
    if (intent.getAction() != null && intent.getAction()
                                                              .equals(Intent.ACTION_BOOT_COMPLETED)) {
      NitroApplication.getSharedInstance()
                      .onDeviceBootCompleted();
    }
  }
  
}
